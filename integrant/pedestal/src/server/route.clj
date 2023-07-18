(ns server.route
  (:require
   [server.handler :as handler]
   [server.response :as response]))

;;; ----------------------------------------------------------------------------------

(defn interceptor-chain [handler]
  [response/coerce-body
   response/content-neg-intc
   handler])


(defn create-routes [options]
  #{["/greet" :get
     (interceptor-chain (handler/create-hello options))
     :route-name :greet]

    ["/greet2" :get    [response/coerce-body
                        response/content-neg-intc
                        (handler/create-hello (:greet options))] :route-name :greet-2]})


(def routes #{["/greet" :get    [response/coerce-body
                                 response/content-neg-intc
                                 handler/respond-hello] :route-name :greet]

              ["/greet2" :get    [response/coerce-body
                                  response/content-neg-intc
                                  handler/respond-hello] :route-name :greet-2]})

