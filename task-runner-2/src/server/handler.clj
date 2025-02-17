(ns server.handler
  (:require [reitit.ring :as ring]
            [reitit.ring.middleware.parameters :as parameters]
            [ring.middleware.json :refer [wrap-json-response wrap-json-body]]
            [server.controllers.hello :as hello-ctl]
            [server.controllers.job :as job-ctl]
            [ring.middleware.keyword-params :refer [wrap-keyword-params]]))

(defn app [db]
  (ring/ring-handler
   (ring/router
    [["/hello" {:handler hello-ctl/say-hello}]
     ["/bye"   {:post hello-ctl/say-bye}]
     ["/job"  {:middleware [wrap-json-response]}
      ["/list"     {:get job-ctl/list-jobs}]
      ["/create"   {:handler job-ctl/create-job}]
      ["/start"    {:handler job-ctl/start-job}]
      ["/stop"     {:handler job-ctl/stop-job}]
      ["/suspend"  {:handler job-ctl/suspend-job}]
      ["/resume"   {:handler job-ctl/resume-job}]]]

    {:data {:db db
            :middleware [parameters/parameters-middleware
                         wrap-keyword-params
                         wrap-json-body
                         wrap-json-response]}})
   (ring/routes
    (ring/create-default-handler
     {:not-found (constantly {:status 404 :body "Not found"})}))))