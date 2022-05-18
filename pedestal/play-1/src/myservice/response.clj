(ns myservice.response
  "Set of helpers function to deal with HTTP response")

(defn- create-response [status body & {:as headers}]
  {:status status :body body :headers headers})

(def ok       
  "Returns a function to create a HTTP 200 response given a body
   and optional headers
   ```
   (ok \"response body\" \"Custom-Header\" \"custom value\")
   ```
   "
  (partial create-response 200))

(def created  (partial create-response 201))
(def accepted (partial create-response 202))


