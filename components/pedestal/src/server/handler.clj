(ns server.handler
  (:require [server.response :as response]
            [server.domain.greeting :as greeting]))


(defn make-hello-handler [app-component]
  (fn  [request]
    (tap> request)
    (tap> app-component)
    (let [name (get-in request [:query-params :name])] 
      (if-let [reply (greeting/greeting-for name)]
        (response/ok reply)
        (response/error-NOT_FOUND "wrong request !")))))

(defn respond-hello [request]
  (tap> request)
  (let [name (get-in request [:query-params :name])]
    (if-let [reply (greeting/greeting-for name)]
      (response/ok reply)
      (response/error-NOT_FOUND "wrong request !"))))