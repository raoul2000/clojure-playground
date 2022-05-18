(ns myservice.helper
  (:require [myservice.api :refer [server]]
            [io.pedestal.test :as test]
             [io.pedestal.http :as http]))

(defn test-request [verb url]
  (io.pedestal.test/response-for (::http/service-fn @server) verb url))

(comment 
  (test-request :get "/greet")
  )