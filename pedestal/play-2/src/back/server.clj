(ns server
  (:require [io.pedestal.http :as http]
            [services.core :as service])
  (:gen-class))


;; Entry point ----------------------------------------------

(defn -main
  [& args]
  (println "\nCreating server...")
  (http/start (http/create-server service/service)))

;; interactive development ----------------------------------------------

;; the one and only server - used during DEV
(defonce server (atom nil))

(defn stop-dev []
  (http/stop @server))

(defn start-dev
  []
  (println "\nCreating [DEV] server...")
  (reset! server (-> service/service ;; start with production configuration
                     (merge {:env :dev
                            ;; do not block thread that starts web server
                             ::http/join? false

                             ;; all origins are allowed in dev mode
                             ::http/allowed-origins {:creds true :allowed-origins (constantly true)}

                             ;; Content Security Policy (CSP) is mostly turned off in dev mode
                             ::http/secure-headers {:content-security-policy-settings {:object-src "'none'"}}})
                     ;; Wire up interceptor chains
                     http/default-interceptors
                     http/dev-interceptors
                     http/create-server
                     http/start)))


