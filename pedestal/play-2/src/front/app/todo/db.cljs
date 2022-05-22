(ns app.todo.db
  (:require [cljs.spec.alpha :as s]
            [clojure.string :as str]))

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

;; -----------------------------------------------------

(defn create-todo [title done]
  {:pre [(s/valid? :todo/title title)
         (s/valid? :todo/done  done)]
   :post [(s/valid? :todo/item %)]}
  {:todo/id    (random-uuid)
   :todo/title (str/trim title)
   :todo/done  done})

(defn add-todo-to-list [todo-list todo]
  (conj todo-list todo))

(defn read-todo-by-id [todo-list id]
  (first (filter #(= id (:todo/id %)) todo-list)))

(defn delete-todo [todo-list id]
  (remove #(= id (:todo/id %)) todo-list))

(defn update-todo [todo-list id new-todo]
  (map #(if (= id (:todo/id %))
          (assoc new-todo :todo/id id)
          %)
       todo-list))

(defn mark-done [todo-list id done]
  (when-let [subject (read-todo-by-id todo-list id)]
    (->> (assoc subject :done done)
         (update-todo todo-list id))))

(comment
  (create-todo "title" true)
  (create-todo "title" false)
  (create-todo "d" false)

  (def list1 [{:todo/id "1"}
              {:todo/id "2"}])
  (read-todo-by-id [{:todo/id "1"}
                    {:todo/id "2"}]
                   "2")

  (update-todo list1 "1" {:color "green"})
  (s/explain :todo/list (mark-done list1 "2" false))
  )