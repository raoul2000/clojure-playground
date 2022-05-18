(ns raoul.contact
  (:require [io.pedestal.http :as http]
            [io.pedestal.http.route :as route]
            [io.pedestal.interceptor :as i]
            [raoul.content-neg :as cneg]
            [clojure.core.async :as async])
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

;; Handler functions, like our respond-hello, are special cases. Pedestal can wrap 
;; a plain old Clojure function with an interceptor that takes the request map out 
;; of the context map, passes it to the function, and uses the return value of the function 
;; as the response map.
(defn respond-hello [request]
  (ok (str "hello " (or (read-query-param request :name)
                        "stranger") " !")))

(defn respond-id [request]
  (ok (str "the id is "
           (read-path-param request :id))))

;; More often, interceptor are defined as map with keys :enter, : leave or :error
;; Note that pedestal will call i/interceptor on this map to create the actual 
;; interceptor function (see interc-custom-default below)

(def echo-interceptor
  {:name ::echo
   :enter (fn [context]
            (let [request (:request context)
                  response (ok request)]
              (assoc context :response response)))})

;; The 3 interceptors below are dedicated to illustrae interceptor chain
;; They print to stdout so we can follow invocation order in route /chain-1 
;; Route /chain-2 illustrate interceptor shortcut

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

;; Note: to short-circuit; the response must include headers or empty map (nil is not good
;; as it will not let pedestal decide that a valid response has been provided and continue
;; in the interceptor chain)

(def interc-shortcut
  {:name ::interc-shortcut
   :enter (fn [context]
            (println "interc-shortcut : enter")
            (assoc context :response (ok "shortcut")))
   :leave (fn [context]
            (println "interc-shortcut : leave")
            context)})

;; adding an interceptor into the chain of default interceptor set by pedestal 
;; Define our custom default interceptor
;; then see (start-dev) ...
;; ref : http://pedestal.io/reference/default-interceptors

(def interc-custom-default
  {:name ::interc-custom-default
   :enter (fn [context]
            (println "custom default interc : enter")
            context)
   :leave (fn [context]
            (println "custom default interc : leave")
            context)})

;; Asynchronous request handler are supported
;; Using a go block, the returned channel must be a context (possibly updated)
;; pedestal will wait for the context value to be pushed to the returned channel

(def interc-ws-call
  {:name ::interc-ws-call
   :enter (fn [context]
            (async/go  ;; returns a channel
              (assoc context :response (ok (slurp "https://jsonplaceholder.typicode.com/todos/1")))))})

;; Error are handled by the function value defined at the :error key
(def error-handler-1
  {:name ::error-handler-1
   :error (fn [context ex-info]
            (println (format "error by %s"
                             (:interceptor ex-info)))
            context ;; This is "catching" the error. Because the context map has no error bound to it, 
                    ;; Pedestal will exit error handling and execute any remaining :leave handlers
            )})


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

     ["/async-1"
      :get
      [interc-ws-call]
      :route-name :async-1]

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
  {::http/routes            routes
   ::http/type              :jetty
   ::http/resource-path     "/public"    ;; serve static resources from /resources/public
                                         ;; http://localhost:8890/about.html
   ;; uncomment to disable logging
   ;; ::http/request-logger nil
   ::http/port              8890})

(defn start []
  (http/start (http/create-server service-map)))


;; interactive development ----------------------------------------------

;; the one and only server - used during DEV
(defonce server (atom nil))

;; start the server in de mode
;; add a custom interceptor to the default interceptors' list: it will be called
;; on each requests
(defn start-dev []
  (reset! server (-> service-map
                     ;; in dev mode, does not block the repl
                     (assoc ::http/join? false)

                     ;; add built-in default interceptors
                     (http/default-interceptors)

                     ;; update the default interceptors' list by adding a custom one
                     ;; note usage of i/interceptor to actually create the interceptor function
                     ;; out of the map definition
                     (update ::http/interceptors conj (i/interceptor interc-custom-default))
                     (http/create-server)
                     (http/start))))

;; start the server in de mode
;; no particular change is done to the service-map
(defn start-dev-1 []
  (reset! server
          (http/start (http/create-server
                       (assoc service-map ::http/join? false)))))

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
