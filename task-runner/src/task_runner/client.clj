(ns task-runner.client
  (:require [org.httpkit.client :as http]))

(defn cb [{:keys [status body error]}]
  (prn "A"))

(time (repeatedly 10  #(http/get "http://localhost:8080/" cb)))

(comment
  (http/get "http://localhost:8080/" cb)

  (let [resp (http/get "http://localhost:8080/")]
    @resp)

  (cb '{:status 12 :body "body"}))