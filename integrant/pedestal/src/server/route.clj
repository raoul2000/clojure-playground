(ns server.route
  (:require
   [server.handler.goodbye :as goodbye]
   [server.handler.greeting :as greeting]
   [server.response :as response]))

;;; ----------------------------------------------------------------------------------

(defn interceptor-chain [handler]
  [response/coerce-body
   response/content-neg-intc
   handler])


(defn create-routes [options]
  (tap> options)
  #{["/greet" :get
     (interceptor-chain (greeting/create-handler options))
     :route-name :greet]

    ["/greet2" :get
     (interceptor-chain (greeting/create-handler options))
     :route-name :greet-2]

    ["/bye" :get
     (interceptor-chain  (goodbye/create-handler options))
     :route-name :bye]})



