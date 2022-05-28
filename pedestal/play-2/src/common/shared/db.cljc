(ns shared.db
  (:require [clojure.spec.alpha :as s]
            ;;[com.cognitect.transit.types :as ty]
            [clojure.string :as str]))

;;(extend-type ty/UUID IUUID)
;; Spec ------------------------------------------------------------

(s/def :todo/id    uuid?)
(s/def :todo/title (s/and string? #(> (count (str/trim %)) 0)))
(s/def :todo/done  boolean?)

(s/def :todo/item (s/keys :req [:todo/id :todo/title :todo/done]))

(s/def :todo-list/title string?)
(s/def :todo-list/items (s/coll-of :todo/item
                                   :kind     vector?    ;; preserve order
                                   :distinct true))

(s/def :todo/list (s/keys :req [:todo-list/title :todo-list/items]))

(s/def :todo-list/list (s/coll-of :todo-list/list
                                  :kind vector?))

;; ------------------------------------------------------------
(comment

  (s/valid? :todo/list {:todo-list/title "things to do "
                        :todo-list/items [{:todo/title "ee"
                                           :todo/id  #uuid "28db773c-24a6-446d-855f-6e8e6828c18e"
                                           :todo/done false}]}))

;; CRUD -----------------------------------------------------

(defn create-todo [title done]
  {:pre [(s/valid? :todo/title title)
         (s/valid? :todo/done  done)]
   :post [(s/valid? :todo/item %)]}
  {:todo/id    (random-uuid)
   :todo/title (str/trim title)
   :todo/done  done})

(defn create-todo-list [title]
  {:pre [(s/valid? :todo-list/title title)]
   :post [(s/valid? :todo/list %)]}
  {:todo-list/title title
   :todo-list/items []})

(defn add-todo-to-list [todo-list todo]
  (update todo-list :todo-list/items #(conj % todo)))

(defn read-todo-ids
  "retuns a vector of all todo ids in the given todo list. Returns an empty
   vector when todo list is empty."
  [todo-list]
  (let [todo-items (:todo-list/items todo-list)]
    (mapv :todo/id todo-items)))

(defn read-todo-by-id
  "Given a todo list and a todo id, returns the todo-item with the given id
   or *nil* when not found."
  [todo-list id]
  (first (filter #(= id (:todo/id %)) (:todo-list/items todo-list))))

(defn delete-todo [todo-list id]
  (update todo-list :todo-list/items
          (fn [todos] (filterv #(not= id (:todo/id %)) todos))))

(defn update-todo [todo-list id new-todo]
  (when (read-todo-by-id todo-list id)
    (update todo-list :todo-list/items (fn [todos]
                                         (mapv #(if (= id (:todo/id %))
                                                  (assoc new-todo :todo/id id)
                                                  %)
                                               todos)))))

(defn update-title-by-id [id new-title todo-item]
  (if (= (:todo/id todo-item) id)
    (assoc todo-item :todo/title new-title)
    todo-item))

(defn update-todo-title [todo-list id new-title]
  (update todo-list :todo-list/items
          #(mapv (partial update-title-by-id id new-title) %)))

(defn todo-done? [todo-item]
  (:todo/done todo-item))

(def initial-todo-list (-> (create-todo-list "My List")
                           (add-todo-to-list (create-todo "do somthing" false))
                           (add-todo-to-list (create-todo "do another thing" false))
                           (add-todo-to-list (create-todo "do one last thing" false))))

;; helper -----------------

(defn check-and-throw
  "Throws an exception if `data` doesn't match the Spec `a-spec`."
  [a-spec data]
  (when-not (s/valid? a-spec data)
    (throw (ex-info (str "spec check failed: " (s/explain-str a-spec data)) {}))))

(comment
 (check-and-throw :todo/list
                  {:todo-list/title "things to do "
                   :todo-list/items [{:todo/title "title"
                                      :todo/id  #uuid "edf45f-d54951-4ce8-8281-dc031d8e74ea"
                                      :todo/done false}]})
  ;;
  )
