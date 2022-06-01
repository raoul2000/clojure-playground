(ns server
  (:require [io.pedestal.http :as http]
            [services.core :as service]
            [cli :refer [parse-cli-options help-option? usage show-errors cli-opt-working-dir]]
            [services.todo :refer [prepare-working-dir default-base-path]])
  (:gen-class))

;; Entry point ----------------------------------------------

(defn start-server [parsed-opts port]
  (let [working-dir-path (cli-opt-working-dir parsed-opts)]
    (println (format "working dir = %s" working-dir-path))
    (prepare-working-dir working-dir-path))
  (println (format "Creating server...\nport = %d" port))
  (http/start (http/create-server (assoc service/service ::http/port port))))

(defn -main [& args]
  (let [parsed-opts (parse-cli-options args)
        errors      (:errors parsed-opts)]
    (cond
      (help-option? parsed-opts)  (println (usage parsed-opts))
      errors                      (println (show-errors errors))
      :else                       (start-server parsed-opts
                                                (get-in parsed-opts [:options :port])))
    (flush)))

;; interactive development ----------------------------------------------

;; the one and only server - used during DEV
(defonce server (atom nil))

(defn stop-dev []
  (http/stop @server))

(defn start-dev
  []
  (prepare-working-dir default-base-path)
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

(defn restart []
  (stop-dev)
  (start-dev))

