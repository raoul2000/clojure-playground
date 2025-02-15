(ns server.job.core
  (:require [clj-http.client :as client]))

(def job-counter (atom 0))
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

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn create-job []
  (let [id (str "job-" (swap! job-counter inc))
        newJob {:id id
                :thread (Thread. (job-runner send-request id) (str "bob-" id))
                :stop    false
                :success 0
                :error   0}]
    (swap! jobs assoc id newJob)
    (-> newJob
        (dissoc :thread)
        (dissoc :stop))))

(defn list-jobs []
  (map (fn [[id job]]
         (-> job
             (dissoc :thread)
             (dissoc :stop)
             (assoc  :state (str (.getState (:thread job)))))) @jobs))

(defn start-job [id]
  (-> @jobs
      (get-in [id :thread])
      (.start)))

(defn stop-job [id]
  (swap! jobs assoc-in [id :stop] true))

(defn suspend-job [id]
  (-> @jobs
      (get-in [id :thread])
      (.suspend)))

(defn resume-job [id]
  (-> @jobs
      (get-in [id :thread])
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