(ns raoul.content-neg
  (:require  [io.pedestal.http.content-negotiation :as conneg]
             [clojure.data.json :as json]
             [cognitect.transit :as transit])
  (:import  [java.io ByteArrayInputStream ByteArrayOutputStream]))

;; read/write map to/from JSON
;; requires custom reader/writer for uuid type
(comment
  (json/write-str {:todo/title "title" :todo/id (random-uuid)}
                  :value-fn (fn [k v]
                              (case k
                                :todo/id (.toString v)
                                v)))

  (json/read-str "{\"title\":\"title\",\"id\":\"00000000-0000-0000-0000-000000000000\"}"
                 :key-fn (fn [k]
                           (keyword "todo" k))
                 :value-fn (fn [k v]
                             (prn k)
                             (case k
                               :todo/id  (parse-uuid v)
                               v)))
  ;;
  )

;; let's see what 'transit' can do for us
;; see https://github.com/cognitect/transit-format
;; for Clojure implementation see https://github.com/cognitect/transit-clj
;; using transit, values are tagged to preserve their semamtic type.

(comment
  (def out (ByteArrayOutputStream. 4096))
  (def writer (transit/writer out :json))
  (transit/write writer {:todo/id (random-uuid)})
  (.toString out)


  (def in (ByteArrayInputStream. (.toByteArray out)))
  (def reader (transit/reader in :json))
  (def my-obj (transit/read reader))
  (prn (:todo/id my-obj))
  ;;
  )


(defn transit+json [body]
  (let [out (ByteArrayOutputStream. 4096)
        writer (transit/writer out :json)]
    (transit/write writer body)
    (.toString out)))

;; ----------------------------------------------------------------------------------

(def supported-types ["text/html" "application/edn" "application/transit+json" "text/plain"])
(def content-neg-intc (conneg/negotiate-content supported-types))

(defn accepted-type
  [context]
  (get-in context [:request :accept :field] "text/plain"))

(defn transform-content
  [body content-type]
  (case content-type
    "text/html"                body
    "text/plain"               body
    "application/edn"          (pr-str body)
    "application/transit+json" (transit+json body)))

(defn coerce-to
  [response content-type]
  (-> response
      (update :body transform-content content-type)
      (assoc-in [:headers "Content-Type"] content-type)))

;; interceptor

(def coerce-body
  {:name ::coerce-body
   :leave
   (fn [context]
     (cond-> context
       (nil? (get-in context [:response :headers "Content-Type"]))
       (update-in [:response] coerce-to (accepted-type context))))})