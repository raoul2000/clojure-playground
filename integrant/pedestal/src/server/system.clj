(ns server.system
  (:require [io.pedestal.http :as http]
            [io.pedestal.http.route :as route]
            [integrant.core :as ig]
            [server.route :as rt])
  (:gen-class))

(def config
  ;; the default configuration - can be over written by user config
  {:app/config      {:param1                   "value1"
                     :param2                   {:nested-p1 true
                                                :nested-p2 12
                                                :nested-p3 "some string"}
                     :polite?                  true
                     :nice-goodbye?            false}

   :server/routes    {:config                  (ig/ref :app/config)}

   :server/server    {::http/routes            (ig/ref :server/routes)
                      ::http/resource-path     "/public"
                      ::http/type              :jetty
                      ::http/port              8890
                      ::http/join?             false}})

(defmethod ig/init-key :app/config
  [_ config]
  config)

(defmethod ig/init-key :server/routes
  [_ {:keys [config]}]
  (-> config
      rt/create-routes
      route/expand-routes))

(defmethod ig/init-key  :server/server
  [_ service-map]
  (http/start (http/create-server service-map)))

(defmethod ig/halt-key! :server/server [_ server]
  (http/stop server))

(defn -main []
  (-> config
      (assoc-in [:app/config :polite?] true)
      ig/init))

