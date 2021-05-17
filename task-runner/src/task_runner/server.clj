(ns task-runner.server
  (:require [org.httpkit.server :as server]
            [cheshire.core :as json]
            [clojure.java.io :as io]
            [compojure.route :as compr]
            [compojure.core :as compc]))

(defonce server (atom nil))

(defn app [req]
  (prn "hello")
  {:status  200
   :headers {"Content-Type" "text/html"}
   :body    (str "<h1>Hello, world!</h1>")})

(defn echo-id [req]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body    (str "id = " ((req :route-params) :id))})

(defn resp-json [req]
  {:status 200
   :headers {"Content-Type" "application/json"}
   :body (json/encode '{:a 1 :b [1 2 3] :boolean true :str "a string"})})

(compojure.core/defroutes all-routes
  (compc/GET "/" [] app)
  (compc/GET "/json" [] resp-json)
  (compc/GET "/user/:id" [] echo-id)
  (compr/files "/static/") ;; static file url prefix /static, in `public` folder
  (compr/not-found "<p>Page not found.</p>"))

(defn stop-server []
  (when-not (nil? @server)
    ;; graceful shutdown: wait 100ms for existing requests to be finished
    ;; :timeout is optional, when no timeout, stop immediately
    (@server :timeout 100)
    (reset! server nil)))

(defn start-server
  []
  (reset! server (server/run-server all-routes {:port 8080})))

