(ns services.todo
  (:require [shared.db :as db]
            [response :as resp]
            [clojure.spec.alpha :as spec]
            [babashka.fs :as fs]))

(def default-base-path
  "A String representing the absolute path to the working dir"
  (str (fs/path (fs/home) ".todos")))

(def db-config (atom {::base-path default-base-path}))

(defn todo-file-path []
  (::todo-file-path @db-config))

(defn validate-working-dir 
  "Returns *true* if the *path* exist and is a folder or
   if the path doesn't exist" 
  [path]
  (or (not (fs/exists? path))
      (fs/directory? path)))

(defn prepare-working-dir 
  "Given `base-path` the working dir path, create it if needed or create the
   todo-list.edn file if it doesn't exist." 
  [base-path]
  (let [todo-file-path (str (fs/path base-path "todo-list.edn"))]
    (try
      (when-not (fs/exists? base-path)
        (fs/create-dirs base-path))
      (when-not (fs/exists? todo-file-path)
        (println "initiating todo list")
        (spit todo-file-path db/initial-todo-list)
        (flush))
      (swap! db-config #(-> %
                            (assoc ::base-path      base-path)
                            (assoc ::todo-file-path todo-file-path)))
      (catch Exception e (str "Failed to prepare working dir: " (.getMessage e))))))

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
            (assoc context :response (resp/ok (read-from-file (todo-file-path)))))})

(def update-todo-list
  {:name ::update-todo-list
   :enter (fn [context]
            (let [transit-params (get-in context [:request :transit-params])]
              (prn transit-params)
              ;; TODO: check is valid Spec
              (write-to-file (todo-file-path) transit-params)
              (assoc context :response (resp/ok transit-params))))})

