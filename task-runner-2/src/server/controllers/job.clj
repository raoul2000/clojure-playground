(ns server.controllers.job
  (:require [ring.util.response :as resp]
            [server.job.core :as job]))


(defn create-job [req]
  (resp/response (str (job/create-job))))

(defn list-jobs [req]
  (resp/response (str (job/list-jobs))))

(defn start-job [req]
  (if-let [id (get-in req [:params :id])]
    (resp/response (str (job/start-job id)))
    (resp/bad-request "missing job id")))

(defn stop-job [req]
  (if-let [id (get-in req [:params :id])]
    (resp/response (str (job/stop-job id)))
    (resp/bad-request "missing job id")))

(defn suspend-job [req]
  (if-let [id (get-in req [:params :id])]
    (resp/response (str (job/suspend-job id)))
    (resp/bad-request "missing job id")))

(defn resume-job [req]
  (if-let [id (get-in req [:params :id])]
    (resp/response (str (job/resume-job id)))
    (resp/bad-request "missing job id")))