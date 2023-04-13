(ns play-3.play-3
  (:require [clojure.data.json :as json]
            [io.pedestal.http :as http]
            [io.pedestal.http.route :as route]
            [io.pedestal.http.content-negotiation :as conneg]
            [play-3.handler :as h]))

(defn response [status body & {:as headers}]
  {:status status :body body :headers headers})

(def ok       (partial response 200))

;; ----------------------------------------------------------------


(def supported-types ["text/html" "application/edn" "application/json" "text/plain"])
;; create a content negociator interceptor for the given list
;; of supported content types
(def content-neg-intc (conneg/negotiate-content supported-types))


(defn accepted-type
  "returns the accepted content type from the context map or `text/plain` if not set"
  [context]
  (get-in context [:request :accept :field] "text/plain"))

(defn transform-content
  "Converts and returns *body* into the given *content-type*"
  [body content-type]
  (case content-type
    "text/html"        body
    "text/plain"       body
    "application/edn"  (pr-str body)
    "application/json" (json/write-str body)))

(defn coerce-to
  "Updates and returns a response map given a *content-type*. The *response* body
   is coerced to the *content-type* and the Content-Type header is assigned the right value"
  [response content-type]
  (-> response
      (update :body transform-content content-type)
      (assoc-in [:headers "Content-Type"] content-type)))

(defn no-content-type?
  "Returns TRUE if the response header map doesn't contain any *Content-Type* key"
  [context]
  (nil? (get-in context [:response :headers "Content-Type"])))

;; interceptor with only a 'leave' handler dedicated to be executed last to
;; convert the response body into the best match for accepted content type
;; as it was computed by content negociation interceptor (content-neg-intc)
(def coerce-body
  {:name ::coerce-body
   :leave (fn [context]
            (cond-> context
              (no-content-type? context) (update-in [:response] coerce-to (accepted-type context))))})

;; handlers ---------------------------------------------



(defn respond-hello [request]
  (let [name (get-in request [:query-params :name])
        resp {:reply (h/greeting-for name)}]
    (if resp
      (ok resp)
      {:status 404 :body "Not found or forbidden name\n"})))


;; Route ---------------------------------------------------------

(def routes 
  
  (route/expand-routes
   #{["/greet"                    :get    [coerce-body
                                           content-neg-intc
                                           respond-hello]         :route-name :greet]
     ;;
     }))

;; server ----------------------------------------------------------

(defn service-map []
  (println "loading service-map...")
  {::http/routes            routes
   ::http/resource-path     "/public"
   ::http/type              :jetty
   ::http/port              8890})

(defn start [_]
  (http/start (http/create-server
               (assoc (service-map)
                      ::http/join? false))))

(defn stop [server-state]
  (http/stop server-state))

(comment
  (require '[clojure.tools.namespace.repl :refer [refresh]])
  (def my_srv (start nil))
  (stop my_srv)
  (refresh)


  ;;
  )
