(ns services.core
  (:require [io.pedestal.http :as http]
            [io.pedestal.http.route :as route]
            [response :as resp]
            [io.pedestal.http.body-params :refer [body-params]]
            [services.todo :as todo]))

(def common-interceptors [resp/coerce-body resp/content-negotiator (body-params)])

(def echo-interceptor
  "Dummy interceptor returning the request as response body"
  {:name ::echo
   :enter (fn [context]
            (let [request  (:request context)
                  response (resp/ok request)]
              (assoc context :response response)))})

(defn about
  "Request handler returing clojure version"
  [_]
  (resp/ok {:clojure-version (clojure-version)
            :java-version    (System/getProperty "java.version")
            :java-vm-version (System/getProperty "java.vm.version")
            :java-vendor     (System/getProperty "java.vendor")}))

;; Routes -------------------------------------------------------------

(def routes
  (route/expand-routes
   #{["/echo"  :get (conj common-interceptors echo-interceptor)         :route-name :get-echo ]
     ["/about" :get (conj common-interceptors about)                    :route-name :get-about]
     
     ["/todo"  :get (conj common-interceptors todo/respond-todo-list)   :route-name :get-todo]
     ["/todo"  :put (conj common-interceptors todo/update-todo-list)    :route-name :put-todo]
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