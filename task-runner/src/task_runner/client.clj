(ns task-runner.client
  (:require [org.httpkit.client :as http]
            [cheshire.core :as json]))


;; asynchronous (fire and forget)
(http/get "https://jsonplaceholder.typicode.com/users" prn)

;; use callback
(http/get "https://jsonplaceholder.typicode.com/users"
          (fn [{:keys [opts status body headers error] :as resp}]
            (println status)
            (println body)))

;; store the promise (resp)
(let [resp (http/get "https://jsonplaceholder.typicode.com/users")]
  (println "the request has been sent...")
  (if (= 200 (@resp :status))
    (println (@resp :body))
    (println "status = " (@resp :status))))

;; POST a json body and process response via callback
(http/post "https://reqres.in/api/users"
           {:headers {"Content-Type" "application/json"}
            :body (json/encode {:name "bobby" :job "singer"})}
           (fn [{:keys [opts status body headers error] :as resp}]
             (if (= 201 status)
               (do
                 (println "=== success")
                 (let [body (json/parse-string body)
                       name (body "name") ;; because once parsed keys are string, not keywords
                       job (body "job")]
                   (println "=== response = name and job :" name " " job)))
               (println "!!! ERROR : status code = " status))))

