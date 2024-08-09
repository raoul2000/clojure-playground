(ns http.core
  (:require [portal.api :as p]
            [clj-http.client :as client]
            [cheshire.core :refer [generate-string]]))

(def config (read-string (slurp "config.edn")))

(comment

  (def p (p/open {:launcher :vs-code}))
  (add-tap #'p/submit)

  (tap> "hello")
  (p/clear)

  ;;
  )


(comment
  ;; making GET request using clj-http
  ;; see https://github.com/dakrone/clj-http?tab=readme-ov-file#quickstart


  (client/get "https://jsonplaceholder.typicode.com/todos/1")
  (client/get "https://jsonplaceholder.typicode.com/todos/1" {:as :auto})

  (def body (generate-string {:applicationId   "test-http-client"
                              :deviceId        "test"
                              :connectionId    (:connectionId config)
                              :username        (:username config)
                              :password        (:password config)}))

  (client/post (:url config)
               {:content-type :json
                :body body
                :insecure? true
                :as :json})


  ;;
  )


