(ns server.job.core
  (:require [clj-http.client :as client]))

(def job-counter (atom 0))

;; jobs list map
;; - key : job id
;; - value : job params map
(def jobs (atom {}))

(defn send-request [success-handler error-handler]
  (try
    (let [resp (client/get "https://jsonplaceholder.typicode.com/users")]
      (println "ok")
      (success-handler))
    (catch Exception ex
      (do
        (println "error")
        (error-handler)))))

(defn job-runner [func id]
  (fn []
    (loop [stop? false]
      (if stop?

        "done"
        (do
          (println "running job " id)
          (func
           (fn []
             (swap! jobs update-in  [id :success] inc))
           (fn []
             (swap! jobs update-in  [id :error] inc)))
          (Thread/sleep 1000)
          (recur (get-in @jobs [id :stop])))))))


(defn get-job-by-id [id]
  (if-let [job (get @jobs id)]
    job
    (throw (ex-info "job not found" {:id id}))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn job-exists? [id]
  (boolean (get @jobs id)))



(defn create-job []
  (let [id     (str "job-" (java.util.UUID/randomUUID))
        newJob {:id      id
                :thread  (Thread. (job-runner send-request id) (str "thread-" id))
                :stop    false
                :success 0
                :error   0}]
    (swap! jobs assoc id newJob)
    (-> newJob
        (dissoc :thread)
        (dissoc :stop))))

(defn list-jobs []
  (map (fn [[_id job]]
         (-> job
             (dissoc :thread)
             (dissoc :stop)
             (assoc  :state (str (.getState (:thread job)))))) @jobs))

(defn start-job [id]
  (-> (get-job-by-id id)
      (:thread)
      (.start))
  true)

(defn stop-job [id]
  (get-job-by-id id)
  (swap! jobs assoc-in [id :stop] true)
  true)

(defn suspend-job [id]
  (-> (get-job-by-id id)
      (:thread)
      (.suspend)))

(defn resume-job [id]
  (-> (get-job-by-id id)
      (:thread)
      (.resume)))

(comment
  @job-counter
  @jobs
  (job-runner send-request "1")
  (.start (get-in @jobs [1 :thread]))

  (create-job)
  (tap> @jobs)
  (get-in @jobs ["job-3" :thread])
  (.start (get-in @jobs ["job-3" :thread]))

  (stop-job "job-1")

  (swap! jobs assoc-in ["job-2" :stop] true)
  ;;
  )