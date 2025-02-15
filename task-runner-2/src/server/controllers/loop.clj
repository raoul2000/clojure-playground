(ns server.controllers.loop
  (:require [clj-http.client :as client]))

(comment

  ;; sync HTTP calls

  (client/get "https://jsonplaceholder.typicode.com/users/not_found")

  (try
    (client/get "https://jsonplaceholder.typicode.com/users/not_found")
    (catch Exception ex
      (.getMessage ex)))

  ;; async HTTP calls

  (client/get "https://jsonplaceholder.typicode.com/users"
              {:async? true}
              (fn [response] (println "success"))
              (fn [exception] (println "error : " (.getMessage exception))))

  (doall (repeatedly 5 (fn []
                         (client/get "https://jsonplaceholder.typicode.com/users"
                                     {:async? true}
                                     (fn [response] (println "success"))
                                     (fn [exception] (println "error : " (.getMessage exception)))))))


  ;;
  )
(def job-info (atom {}))

(defn send-request [options success-handler error-handler]
  (println "I'm " (:name options) " doing ok")
  (try
    (let [resp (client/get "https://jsonplaceholder.typicode.com/users")]
      (println "ok")
      (success-handler))
    (catch Exception ex
      (do
        (println "error")
        (error-handler)))))

(defn job-runner [func job-options]
  (fn []
    (loop [loop-count 0]
      (if (> loop-count 5)
        "done"
        (do
          (println "running job " (:name job-options))
          (func job-options
                (fn []
                  (swap! job-info update (:name job-options) conj "ok"))
                (fn []
                  (swap! job-info update (:name job-options) conj "ERROR")))
          (Thread/sleep 1000)
          (recur (inc loop-count)))))))

(defn create-job [func {:keys [name] :as job-options}]
  (println "creating job : " (:name job-options))
  {:id   name
   :name name
   :thread  (Thread. (job-runner
                      func
                      job-options) (str "bob-" (:name job-options)))
   :manage {:state :created}})

(defn start-job [{:keys [id thread]}]
  (println "starting job " id)
  (.start thread))

(def job-map (atom {}))

(comment

  (def job-config [{:name "job 1"}
                   {:name "job 2"}
                   {:name "job 3"}])
  (->> job-config
       (map #(create-job send-request %))
       (map start-job))

  (->> job-config
       (map #(create-job send-request %))
       (reduce (fn [res {:keys [name] :as job}]
                 (assoc res name job)) {})
       (reset! job-map))

  @job-info
  @job-map

  (map (fn [[job-name job]]
         (println "starting " job-name)
         (.start (:thread job))) @job-map)

  (map (fn [[job-name job]]
         (println "suspending " job-name)
         (.suspend (:thread job))) @job-map)

  (map (fn [[job-name job]]
         (println "resuming " job-name)
         (.resume (:thread job))) @job-map)

  (map (fn [[job-name job]]
         (println "stopping  " job-name)
         (.stop (:thread job))
         #_(swap! job-map update job-name assoc :job nil)) @job-map)

  (map (fn [[job-name job]]
         (println "job  " job-name " state = " (str (.getState (:thread job))))) @job-map)

  ()

  ;;
  )