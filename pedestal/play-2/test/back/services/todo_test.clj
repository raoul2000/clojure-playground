(ns services.todo-test
  (:require [clojure.test :refer :all]
            [io.pedestal.test :as pt]
            [io.pedestal.http :as http]
            [io.pedestal.http.route :as route]
            [services.core :as service]))

(def service
  "Service under test"
  (::http/service-fn (http/create-servlet service/service)))

(def url-for
  "Test url generator."
  (route/url-for-routes service/routes))

(deftest upload-file-test
  (let [form-body (str "--XXXX\r\n"
                       "Content-Disposition: form-data; name=\"file1\"; filename=\"foobar1.txt\"\r\n\r\n"
                       "bar\r\n"
                       "--XXXX\r\n"
                       "Content-Disposition: form-data; name=\"file2\"; filename=\"foobar2.txt\"\r\n\r\n"
                       "baz\r\n"
                       "--XXXX--")

        response (pt/response-for service
                                  :post    (url-for :post-upload)
                                  :body    form-body
                                  :headers {"Content-Type" "multipart/form-data; boundary=XXXX"})]

    (is (= 200 (:status response))))
  ;;
  )

(deftest about-service
  (let [response (pt/response-for service
                                  :get (url-for :get-about))]
    (is (= 200 (:status response)))

    (is (= "text/plain"
           (get-in response [:headers "Content-Type"])))))