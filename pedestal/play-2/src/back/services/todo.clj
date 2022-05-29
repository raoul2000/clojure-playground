(ns services.todo
  (:require [shared.db :as db]
            [response :as resp]
            [clojure.spec.alpha :as spec]))

(def todo-list-file-path "todo-list.edn")

;; Consider more efficient serialization/deserialization technique
;; see https://github.com/ptaoussanis/nippy 

(defn write-to-file [file-path todo-list]
  {:pre [(spec/valid? :todo/list todo-list)]}
  (spit file-path  todo-list))

(defn read-from-file [file-path]
  {:post [(spec/valid? :todo/list %)]}
  (clojure.edn/read-string (slurp file-path)))


;; interceptor - handler ---------------------------------

(def respond-todo-list
  {:name ::respond-todo-list
   :enter (fn [context]
            (assoc context :response (resp/ok (read-from-file todo-list-file-path))))})

(def update-todo-list
  {:name ::update-todo-list
   :enter (fn [context]
            (let [transit-params (get-in context [:request :transit-params])]
              (prn transit-params)
              ;; TODO: check is valid 
              (write-to-file todo-list-file-path transit-params)
              (assoc context :response (resp/ok transit-params))))})



