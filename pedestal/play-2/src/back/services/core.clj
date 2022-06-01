(ns services.core
  (:require [io.pedestal.http :as http]
            [io.pedestal.http.route :as route]
            [io.pedestal.http.ring-middlewares :as ring-mw]
            [clojure.java.io :as io]
            [response :as resp]
            [io.pedestal.http.body-params :refer [body-params]]
            [services.todo :as todo]
            [babashka.fs :as fs]))

(def common-interceptors [resp/coerce-body resp/content-negotiator (body-params)])

(def echo-interceptor
  "Dummy interceptor returning the request as response body"
  {:name ::echo
   :enter (fn [context]
            (let [request  (:request context)
                  response (resp/ok request)]
              (assoc context :response response)))})

(defn about
  "Request handler returing clojure and Java version "
  [_]
  (resp/ok {:clojure-version (clojure-version)
            :java-version    (System/getProperty "java.version")
            :java-vm-version (System/getProperty "java.vm.version")
            :java-vendor     (System/getProperty "java.vendor")}))

(def download-file-handler
  "Dummy interceptor returning the request as response body"
  {:name ::download-file-handler
   :enter (fn [context]
            (assoc context :response
                   (resp/ok (fs/file "c:\\tmp\\NR_37_20220309_8.pdf")
                            ;; set Content-Disposition header to force download.
                            ;; Replace 'attachment' with 'inline' to ask the browser to show the
                            ;; file content
                            {"Content-Disposition" "attachment; filename=\"filename.pdf\""}
                            ;; Note that the Content-Type header is set by the ring-mw/file-info interceptor
                            ;; (see route)
                            ;; Other option is to force the Content-Type header :
                            ;; "Content-Type" "image/jpg"
                            )))})

(defn store-upload-file
  "Store the file being uploaded given a map describing it. Map keys are
   
   - `:filename`
   - `:stream`
   - `:content-type`
   
   The return value will be used as the value for the parameter in the multipart 
   parameter map.

   see [wrap-multipart-params](https://ring-clojure.github.io/ring/ring.middleware.multipart-params.html#var-multipart-params-request)"
  [item]
  (io/copy (:stream item) (io/file (str "c:\\tmp\\" (:filename item))))
  {:result "good"})

(defn upload-progress [request bytes-read content-length item-count]
  (print (format "item : %d - \ncontent length : %d\nbytes-read : %d" item-count content-length bytes-read)))


;; Routes -------------------------------------------------------------

(def routes
  (route/expand-routes
   #{["/echo"  :get (conj common-interceptors echo-interceptor)         :route-name :get-echo]
     ["/about" :get (conj common-interceptors about)                    :route-name :get-about]

     ;; todo service -------------------------------------------------------------------------

     ["/todo"  :get (conj common-interceptors todo/respond-todo-list)   :route-name :get-todo]
     ["/todo"  :put (conj common-interceptors todo/update-todo-list)    :route-name :put-todo]

     ;; upload and download routes -----------------------------------------------------------
     
     ["/dwn"     :get   [;; file-info interceptor will set the content-type of the response
                        ;; based on the extension of the file to download. 
                        ;; If not set, content-type defaults to application/octet-stream 
                         (ring-mw/file-info)
                         download-file-handler]  :route-name :get-dwn]
     ["/upload"  :post   [(ring-mw/multipart-params {:store       store-upload-file
                                                     :progress-fn upload-progress})
                          echo-interceptor]   :route-name :post-upload]
     ;;
     }))

;; Service ------------------------------------------------------------

(def service
  "The main service map"
  {:env                     :prod
   ::http/routes            routes
   ::http/type              :jetty
   ::http/resource-path     "/public"    ;; serve static resources from /resources/public
                                         ;; http://localhost:8890/about.html

   ;; This is required for a static served HTML page to load JS
   ;; TODO: study this settings to use the appropriate values   
   ::http/secure-headers   {:content-security-policy-settings {:object-src "none"}}

   ;; uncomment to disable logging
   ;; ::http/request-logger nil
   ::http/port              8890})