(ns myservice.api
  (:require [io.pedestal.http :as http]
            [io.pedestal.http.route :as route])
  (:gen-class))

(defn ok [body]
  {:status 200 :body body})

(defn respond-hello [request]
  (let [name (get-in request [:query-params :name] "stranger")]
    (ok (str "hello, " name))))

(def routes
  (route/expand-routes
   #{["/greet" :get respond-hello :route-name :greet]}))

(def service-map
  {::http/routes routes
   ::http/type   :jetty
   ::http/port   8890})

(defn start []
  (http/start (http/create-server service-map)))

(defn start-server []
  (http/start (http/create-server
               (assoc service-map
                      ::http/join? false))))

;; interactive development ----------------------------------------------

(defonce server (atom nil))

(defn start-dev []
  (reset! server (start-server)))

(defn stop-dev []
  (http/stop @server))

(defn restart []
  (stop-dev)
  (start-dev))

;; CLI ----------------------------------------------------------------

(defn greet
  "Callable entry point to the application."
  [data]
  (println (str "Hello, " (or (:name data) "World") "!")))

(defn -main
  [& args]
  (greet {:name (first args)})
  (println (str "now starting server at port " (service-map ::http/port)))
  (start-server))
