(ns server.route
  (:require
   [server.handler :as handler]
   [server.response :as response]))

;;; ----------------------------------------------------------------------------------

(defn make-routes [app-component]
  #{["/greet" :get    [response/coerce-body
                       response/content-neg-intc
                       (handler/make-hello-handler app-component)]  :route-name :greet]})

(def routes #{["/greet" :get    [response/coerce-body
                                 response/content-neg-intc
                                 ()] :route-name :greet]

              ["/greet2" :get    [response/coerce-body
                                  response/content-neg-intc
                                  handler/respond-hello] :route-name :greet-2]})

