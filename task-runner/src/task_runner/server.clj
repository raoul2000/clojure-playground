(ns task-runner.server
  (:require [org.httpkit.server :as server]
            [cheshire.core :as json]
            [clojure.java.io :as io]
            [compojure.route :as compr]
            [compojure.core :as compc]
            [task-runner.concurrency.first :as tr]))

(declare stop-server start-server)
(defonce server (atom nil))

(defn app [_]
  (prn "hello")
  {:status  200
   :headers {"Content-Type" "text/html"}
   :body    (str "<h1>Hello, world!</h1>")})

(defn echo-id [req]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body    (str "id = " ((req :route-params) :id))})

(defn resp-json [_]
  {:status 200
   :headers {"Content-Type" "application/json"}
   :body (json/encode '{:a 1 :b [1 2 3] :boolean true :str "a string"})})

(defn bye-bye [_]
  (.start (Thread. (fn []
                     (println "shutting down server ...")
                     (Thread/sleep 5000)
                     (stop-server)
                     (println "server stopped."))))
  {:status 200
   :headers {"Content-Type" "text/plain"}
   :body "shutting down ..."})

(defn start-task [_]
  (reset! tr/interrupt false)
   (future (tr/run-task2b #(tr/task3 :web)))
  {:status 200
   :headers {"Content-Type" "text/plain"}
   :body "starting"})

(defn stop-task [_]
  (reset! tr/interrupt true)
  {:status 200
   :headers {"Content-Type" "text/plain"}
   :body "stopping"})

(compojure.core/defroutes all-routes
  (compc/GET "/" [] app)
  (compc/GET "/task-start" [] start-task)
  (compc/GET "/task-stop"  [] stop-task)
  (compc/GET "/json" [] resp-json)
  (compc/GET "/stop" [] bye-bye)
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
