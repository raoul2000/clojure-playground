(ns raoul.contact
  (:require [io.pedestal.http :as http]
            [io.pedestal.http.route :as route]
            [io.pedestal.http.content-negotiation :as conneg]
            [clojure.data.json :as json]
            [raoul.content-neg :as cneg])
  (:gen-class))

;; helper  --------------------------------------------

(defn response [status body & {:as headers}]
  {:status status :body body :headers (or headers {})})

(def ok        (partial response 200))
(def created   (partial response 201))
(def accepted  (partial response 202))
(def not-found (partial response 404))

(defn read-query-param [request name-k]
  (get-in request [:query-params name-k]))

(defn read-path-param [request name-k]
  (get-in request [:path-params name-k]))

;; handler --------------------------------------------

(defn respond-hello [request]
  (ok (str "hello " (or (read-query-param request :name)
                        "stranger") " !")))
(def echo-interceptor
  {:name ::echo
   :enter (fn [context]
            (let [request (:request context)
                  response (ok request)]
              (assoc context :response response)))})

(defn respond-id [request]
  (ok (str "the id is "
           (read-path-param request :id))))

(def interc-1
  {:name ::interc-1
   :enter (fn [context]
            (println "interc-1 : enter")
            (-> context
                (assoc :steps-enter ["interc-1"])
                (assoc :steps-leave [])))
   :leave (fn [context]
            (println "interc-1 : leave")
            (let [context-updated (update context :steps-leave #(conj % "interc-1"))]
              (println (str "enter : " (:steps-enter context-updated)))
              (println (str "leave : " (:steps-leave context-updated)))
              context-updated))})

(def interc-2
  {:name ::interc-2
   :enter (fn [context]
            (println "interc-2 : enter")
            (update context :steps-enter #(conj % "interc-2")))
   :leave (fn [context]
            (println "interc-2 : leave")
            (update context :steps-leave #(conj % "interc-2")))})

(def interc-3
  {:name ::interc-3
   :enter (fn [context]
            (println "interc-3 : enter")
            (update context :steps-enter #(conj % "interc-3")))
   :leave (fn [context]
            (println "interc-3 : leave")
            (update context :steps-leave #(conj % "interc-3")))})

;; Handler functions, like our respond-hello, are special cases. Pedestal can wrap 
;; a plain old Clojure function with an interceptor that takes the request map out 
;; of the context map, passes it to the function, and uses the return value of the function 
;; as the response map.

;; Note: to short-circuit; the response must include headers or empty map

(def interc-shortcut
  {:name ::interc-shortcut
   :enter (fn [context]
            (println "interc-shortcut : enter")
            (assoc context :response (ok "shortcut")))
   :leave (fn [context]
            (println "interc-shortcut : leave")
            context)})
;; Routes ---------------------------------------------

(def routes
  (route/expand-routes
   #{["/greet"  :get respond-hello :route-name :greet]
     ["/greet2" :get [cneg/coerce-body cneg/content-neg-intc respond-hello] :route-name :greet-with-content-neg]
     ["/echo"   :get echo-interceptor]

     ["/path1/:id"
      :get
      [cneg/coerce-body cneg/content-neg-intc respond-id]
      :route-name :path-id-1]

     ["/path2/:id"
      :get
      [cneg/coerce-body cneg/content-neg-intc respond-id]
      :route-name :path-id-2
      :constraints {:id #"[0-9]+"}]

     ["/chain-1"
      :get
      [interc-1 interc-2 interc-3 respond-hello]
      :route-name :chain-1]

     ["/chain-2"
      :get
      [interc-1 interc-2 interc-shortcut interc-3 respond-hello]
      :route-name :chain-2]


     ;;
     }))

(comment
  (def url-for (route/url-for-routes routes))

  (url-for :greet)
  (url-for :greet :params {:name "bob"})
  (url-for :greet-with-content-neg)
  (url-for :raoul.contact/echo)
  (url-for :path-id-1 :params {:id 12})
  (url-for :path-id-2)

  ;;
  )
;; Server ----------------------------------------------

(def service-map
  {::http/routes routes
   ::http/type   :jetty
   ::http/port   8890})

(defn start []
  (http/start (http/create-server service-map)))

;; For interactive development
(defonce server (atom nil))

(defn start-dev []
  (reset! server
          (http/start (http/create-server
                       (assoc service-map
                              ::http/join? false)))))

(defn stop-dev []
  (http/stop @server))

(defn restart []
  (stop-dev)
  (start-dev))

;; tests
(comment
  (route/try-routing-for routes :prefix-tree "/greet" :get)
  (route/try-routing-for routes :prefix-tree "/greet?name=bob" :get))


(defn greet
  "Callable entry point to the application."
  [data]
  (println (str "Hello, " (or (:name data) "World") "!")))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (greet {:name (first args)}))
