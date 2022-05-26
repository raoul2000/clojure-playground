(ns raoul.todo
  (:require [shared.db :as db]))

(defn response [status body & {:as headers}]
  {:status status :body body :headers (or headers {})})

(def ok        (partial response 200))

(def init-todo-list  db/initial-todo-list)
(def l #:todo-list{:title "My List",
                   :items
                   [#:todo{:id #uuid "21451425-1fa5-4472-9aef-20d8386c49e1", :title "do somthing", :done false}
                    #:todo{:id #uuid "df8585ca-11f7-49fe-9f2f-57b6cfb96c04", :title "do another thing", :done false}
                    #:todo{:id #uuid "b0a071c5-231c-4933-ad20-7ffb551cc268", :title "do one last thing", :done false}]})

(def m {:title "My List",
        :items [{:id "1"
                 :title "t1"}
                {:id "2"
                 :title "t2"}]})

(def respond-todo-list
  {:name ::respond-todo-list
   :enter (fn [context]
            (assoc context :response {:status  200
                                      :body    db/initial-todo-list
                                      :headers {}}))})

