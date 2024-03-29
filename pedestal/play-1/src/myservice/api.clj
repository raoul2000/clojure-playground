(ns myservice.api
  (:require [clojure.data.json :as json]
            [io.pedestal.http :as http]
            [io.pedestal.http.body-params :as body-params]
            [io.pedestal.http.route :as route]
            [io.pedestal.http.content-negotiation :as conneg]
            [io.pedestal.test :as test]
            [io.pedestal.log :as log]
            [myservice.todo :as todo])
  (:gen-class))

(defn response [status body & {:as headers}]
  {:status status :body body :headers headers})

(def ok       (partial response 200))
(def created  (partial response 201))
(def accepted (partial response 202))


(defn not-found []
  {:status 404 :body "Not found or forbidden name\n"})

;; /greet -------------------------------------------------

(defn greeting-for [name]
  (cond
    (nil? name)            "hello, stranger !! :)"
    (#{"bob" "max"} name)  nil
    :else                  (str "hello, " name)))

(defn respond-hello [request]
  (let [name (get-in request [:query-params :name])
        resp {:reply (greeting-for name)}]
    (if resp
      (ok resp)
      (not-found))))

;; /echo -------------------------------------------------

;; define a basic 'echo' interceptor
(def echo
  {:name ::echo
   :enter #(assoc % :response (ok (:request %))) ;; anonymous function to update the :response
                                                 ;; key in the context map
   })


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


;; to test route in the REPL
;; (route/try-routing-for hello/routes :prefix-tree "/greet" :get)
(defn routes []
  (println "loading route ...")
  (route/expand-routes
   #{["/greet"                    :get    [coerce-body
                                           content-neg-intc
                                           respond-hello]         :route-name :greet]

     ["/echo"                     :get    [echo] :route-name :echo]

     ;; the inteceptor is responsible for parsing the body content. Depending on the
     ;; Content-Type header value, the appropriate parser is used among : json, HTML form
     ;; EDN.
     ;; Depending on content type, the parsed value is stored in a different key in the
     ;; request. For example, application/json will be stored into key ::json-params
     ;;
     ;; Context example : 
     ;; {:request {:json-params {:key "value"}}}
     ;; read more at http://pedestal.io/reference/parameters#_body_parameters
     ;;

     ["/echo"                     :post   [(body-params/body-params)
                                           echo]                   :route-name :echo-post] 
     ["/todo"                     :post   [todo/db-interceptor
                                           todo/list-create]       :route-name :list-create]

     ["/todo"                     :get    [todo/entity-render
                                           todo/db-interceptor
                                           todo/all-list-view]      :route-name :list-view-all]

     ["/todo/:list-id"            :get    [todo/entity-render
                                           todo/db-interceptor
                                           todo/list-view]          :route-name :list-view]

     ["/todo/:list-id"            :post   [todo/entity-render
                                           todo/list-item-view
                                           todo/db-interceptor
                                           todo/list-item-create]   :route-name :list-item-create]

     ["/todo/:list-id/:item-id"   :put   [todo/entity-render
                                          todo/list-item-view
                                          todo/db-interceptor
                                          todo/list-item-update]    :route-name :list-item-update]
     ;;
     }))

(defn service-map []
  (println "loading service-map...")
  {::http/routes            (routes)
   ::http/resource-path     "/public"
   ::http/type              :jetty
   ::http/port              8890})

(defn start []
  (http/start (http/create-server service-map)))

(defn start-server []
  (http/start (http/create-server
               (assoc (service-map)
                      ::http/join? false))))

;; interactive development ----------------------------------------------

;; the one and only server - used during DEV
(defonce server (atom nil))

(defn test-request [verb url]
  (io.pedestal.test/response-for (::http/service-fn @server) verb url))

(comment
  (test-request :post "/todo")
  ;;
  )

(defn start-dev []
  (reset! server (start-server)))

(defn stop-dev
  ([srv]
   (http/stop srv))
  ([]
   (http/stop @server)))

(defn restart []
  (stop-dev)
  (reset! server nil)
  (start-dev))

(comment
  (start-dev)
  (restart)
  (stop-dev)
  (test/response-for (:io.pedestal.http/service-fn @server) :get "/echo"
                     :headers {"Accept" "application/json"})

  (test/response-for (:io.pedestal.http/service-fn @server) :post "/echo"
                     :headers {"Content-Type" "application/json"}
                     :body "{\"a\": 1}")
  ;;
  )
;; CLI ----------------------------------------------------------------

(defn greet
  "Callable entry point to the application."
  [data]
  (println (str "Hello, " (or (:name data) "World") "!")))

(defn -main
  [& args]
  (greet {:name (first args)})
  #_(println (str "now starting server at port " (service-map ::http/port)))
  (start-server))
