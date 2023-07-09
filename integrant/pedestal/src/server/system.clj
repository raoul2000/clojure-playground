(ns server.system
  (:require [io.pedestal.http :as http]
            [io.pedestal.http.route :as route]
            [integrant.core :as ig]
            [server.route :as rt])
  (:gen-class))

(def config
  {:server/routes rt/routes

   :server/server {::http/routes            (ig/ref :server/routes)
                   ::http/resource-path     "/public"
                   ::http/type              :jetty
                   ::http/port              8890
                   ::http/join?             false}})

(defmethod ig/init-key :server/routes
  [_ route-spec]
  (route/expand-routes route-spec))

(defmethod ig/init-key  :server/server
  [_ service-map]
  (http/start (http/create-server service-map)))

(defmethod ig/halt-key! :server/server [_ server]
  (http/stop server))

(defn -main []
  (ig/init config))
