(ns server.route
  (:require
   [server.handler :as handler]
   [server.response :as response]))

;;; ----------------------------------------------------------------------------------

(def routes #{["/greet" :get    [response/coerce-body
                                 response/content-neg-intc
                                 handler/respond-hello] :route-name :greet]
              
              ["/greet2" :get    [response/coerce-body
                                  response/content-neg-intc
                                  handler/respond-hello] :route-name :greet-2]})

