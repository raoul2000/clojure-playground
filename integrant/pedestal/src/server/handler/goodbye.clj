(ns server.handler.goodbye
  (:require [server.domain.goodbye :as goodbye]
            [server.response :as response]))

(defn create-handler [{:keys [nice-goodbye?]}]
  (fn [_request]
    (response/ok (goodbye/say-goodbye nice-goodbye?))))