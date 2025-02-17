(ns server.controllers.job
  (:require [ring.util.response :as resp]
            [server.job.core :as job]))


(defn safe-run [fn]
  (try
    (fn)
    {:success true}
    (catch Exception e (ex-data e))))


(defn create-job [req]
  (resp/response (job/create-job)))

(defn list-jobs [req]
  (resp/response (job/list-jobs)))

(defn start-job [req]
  (tap> req)
  (if-let [id (get-in req [:params :id])]
    (safe-run (fn [] (job/start-job id)))
    (resp/bad-request "missing job id")))

(defn stop-job [req]
  (if-let [id (get-in req [:params :id])]
    (safe-run (fn [] (job/stop-job id)))
    (resp/bad-request "missing job id")))

(defn suspend-job [req]
  (if-let [id (get-in req [:params :id])]
    (resp/response (job/suspend-job id))
    (resp/bad-request "missing job id")))

(defn resume-job [req]
  (if-let [id (get-in req [:params :id])]
    (resp/response (job/resume-job id))
    (resp/bad-request "missing job id")))