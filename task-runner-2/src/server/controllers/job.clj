(ns server.controllers.job
  (:require [ring.util.response :as resp]
            [server.job.core :as job]))


(defn safe-run [fn success-resp]
  (try
    (fn)
    success-resp
    (catch clojure.lang.ExceptionInfo ex
      (resp/bad-request {:error true
                         :data (ex-data ex)})
      {:ex-data (ex-data ex)})
    (catch Exception ex
      (resp/bad-request {:error   true
                         :message (.getMessage ex)}))))




(defn create-job [req]
  (try
    (let [new-job (job/create-job)]
      (resp/created "" {:success true
                        :job new-job}))
    (catch clojure.lang.ExceptionInfo ex
      (resp/bad-request {:error true
                         :data (ex-data ex)})
      {:ex-data (ex-data ex)})
    (catch Exception ex
      (resp/bad-request {:error   true
                         :message (.getMessage ex)})))

  (safe-run job/create-job (resp/created "/job/1" {:success true})))

(defn list-jobs [req]
  (resp/response (job/list-jobs)))

(defn start-job [req]
  (tap> req)
  (if-let [id (get-in req [:params :id])]
    (if (job/job-exists? id)
      (safe-run (partial job/start-job id) (resp/response {:success true}))
      (resp/not-found {:error true
                       :message (str "job not found : " id)}))
    (resp/bad-request {:error true
                       :message "missing job id"})))

(defn stop-job [req]
  (if-let [id (get-in req [:params :id])]
    (if (job/job-exists? id)
      (safe-run (partial job/stop-job id) (resp/response {:success true}))
      (resp/not-found {:error true
                       :message (str "job not found : " id)}))
    (resp/bad-request {:error true
                       :message "missing job id"})))

(defn suspend-job [req]
  (if-let [id (get-in req [:params :id])]
    (if (job/job-exists? id)
      (safe-run (partial job/suspend-job id) (resp/response {:success true}))
      (resp/not-found {:error true
                       :message (str "job not found : " id)}))
    (resp/bad-request {:error true
                       :message "missing job id"})))

(defn resume-job [req]
  (if-let [id (get-in req [:params :id])]
    (if (job/job-exists? id)
      (safe-run (partial job/resume-job id) (resp/response {:success true}))
      (resp/not-found {:error true
                       :message (str "job not found : " id)}))
    (resp/bad-request {:error true
                       :message "missing job id"})))

(comment

  (try
    #_(throw (ex-info "something went wrong" {:reason "all broken"}))
    (/ 1 0)
    (catch clojure.lang.ExceptionInfo ex
      {:ex-data (ex-data ex)})
    (catch Exception ex
      {:error (.getMessage ex)}))

  (defn f1 [n]
    (* 2 n))

  (def cf1 (partial f1 4))
  (cf1)
    ;;
  )