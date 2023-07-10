(ns server.system
  (:require [io.pedestal.http :as http]
            [io.pedestal.http.route :as route]
            [server.route :as rt]
            [com.stuartsierra.component :as component])
  (:gen-class))

#_(def config
  {:server/routes rt/routes

   :server/server {::http/routes            (ig/ref :server/routes)
                   ::http/resource-path     "/public"
                   ::http/type              :jetty
                   ::http/port              8890
                   ::http/join?             false}})

(defrecord Database [host port connection]
  component/Lifecycle

  (start [component]
    (println ";; Starting database")
    (let [conn (str  host port)]
      (assoc component :connection conn)))

  (stop [component]
    (println ";; Stopping database")
    (assoc component :connection nil)))


(defn new-database [host port]
  (map->Database {:host host :port port}))

(defrecord SchedulerComponent [options]
 component/Lifecycle
  (start [this]
    (println ";; Starting SchedulerComponent")
    this)
  (stop [this]
    (println ";; Stoping SchedulerComponent")
    this
    )
 )

(defn new-scheduler [options]
  (map->SchedulerComponent options))

(defrecord ExampleComponent [options cache database scheduler]
  component/Lifecycle

  (start [this]
    (println ";; Starting ExampleComponent")
    ;; In the 'start' method, a component may assume that its
    ;; dependencies are available and have already been started.
    (assoc this :admin database))

  (stop [this]
    (println ";; Stopping ExampleComponent")
    ;; Likewise, in the 'stop' method, a component may assume that its
    ;; dependencies will not be stopped until AFTER it is stopped.
    this))


(defn example-component [config-options]
  (map->ExampleComponent {:options config-options
                          :cache (atom {})}))

(defn example-system [config-options]
  (let [{:keys [host port]} config-options]
    (component/system-map
     :db (new-database host port)
     :scheduler (new-scheduler config-options)
     :app (component/using
           (example-component config-options)
           {:database  :db
            :scheduler :scheduler}))))

(defn -main [])

(comment
  
(def system (component/start (example-system {:opt "value"})))
  (component/stop system)

  )

#_((defmethod ig/init-key :server/routes
     [_ route-spec]
     (route/expand-routes route-spec))

   (defmethod ig/init-key  :server/server
     [_ service-map]
     (http/start (http/create-server service-map)))

   (defmethod ig/halt-key! :server/server [_ server]
     (http/stop server))

   (defn -main []
     (ig/init config)))
