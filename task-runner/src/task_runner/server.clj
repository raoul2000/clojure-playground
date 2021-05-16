(ns task-runner.server
  (:require [org.httpkit.server :as server]
            [cheshire.core :as json]
            [clojure.java.io :as io]))

(defonce server (atom nil))

(defn app [req]
  {:status  200
   :headers {"Content-Type" "text/html"}
   :body    "hello HTTP!"})


(defn stop-server []
  (when-not (nil? @server)
    ;; graceful shutdown: wait 100ms for existing requests to be finished
    ;; :timeout is optional, when no timeout, stop immediately
    (@server :timeout 100)
    (reset! server nil)))

(defn start-server
  []
  (reset! server (server/run-server #'app {:port 8080})))
