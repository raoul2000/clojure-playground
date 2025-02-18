(ns server.system
  (:require [integrant.core :as ig]
            [ring.adapter.jetty :refer [run-jetty]]
            [server.handler :as handler])
  (:gen-class))


(def config
  {:handler/run-app {:db "dummyDB"}
   :adapter/jetty   {:handler (ig/ref :handler/run-app)
                     :port    3001}})

(defmethod ig/init-key :handler/run-app [_ {:keys [db]}]
  (handler/app db))

(defmethod ig/init-key :adapter/jetty [_ {:keys [handler] :as opts}]
  (run-jetty handler (-> opts
                         (dissoc :handler)
                         (assoc :join? false))))

(defmethod ig/halt-key! :adapter/jetty [_ server]
  (.stop server))

(defn -main []
  (ig/init config))