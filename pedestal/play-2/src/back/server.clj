(ns server
  (:require [io.pedestal.http :as http]
            [services.core :as service]
            [cli :refer [parse-cli-options help-option? usage]])
  (:gen-class))

;; Entry point ----------------------------------------------

(defn start-server [port]
  (println (format "\nCreating server...\nport = %d" port))
  (http/start (http/create-server (assoc service/service ::http/port port))))

(defn -main [& args]
  (let [parsed-opts (parse-cli-options args)]
    (cond
      (help-option? parsed-opts)  (println (usage parsed-opts))
      :else                       (start-server (get-in parsed-opts [:options :port])))
    (flush)))

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

