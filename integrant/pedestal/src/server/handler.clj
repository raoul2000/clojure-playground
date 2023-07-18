(ns server.handler
  (:require [server.response :as response]
            [server.domain.greeting :as greeting]))


(defn create-hello [options]
  (tap> options)
  (fn [request]
    (let [name (get-in request [:query-params :name])]
      (if-let [reply (greeting/greeting-for name (:hello-options options))]
        (response/ok reply)
        (response/error-NOT_FOUND "wrong request !"))))
  )

(defn respond-hello [request]
  (tap> request)
  (let [name (get-in request [:query-params :name])]
    (if-let [reply (greeting/greeting-for name false)]
      (response/ok reply)
      (response/error-NOT_FOUND "wrong request !"))))