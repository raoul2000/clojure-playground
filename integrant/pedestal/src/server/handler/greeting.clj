(ns server.handler.greeting
  (:require [server.response :as response]
            [server.domain.greeting :as greeting]))


(defn create-handler [{:keys [polite?] :as options}]
  (tap> options)
  (fn [request]
    (let [name (get-in request [:query-params :name])]
      (if-let [reply (greeting/greeting-for name polite?)]
        (response/ok reply)
        (response/error-NOT_FOUND "wrong request !")))))