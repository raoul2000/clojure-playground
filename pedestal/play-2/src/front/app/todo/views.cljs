(ns app.todo.views
  (:require [re-frame.core :as rf]
            [app.todo.events]
            [app.todo.subs]
            [reagent.dom :as rdom]))

(defn todo-render [{:keys [:todo/id :todo/title :todo/done]}]
  [:div {:key id}
   [:input {:type :checkbox
            :checked done
            :on-change #(rf/dispatch [:toggle-todo-item id])}]
   [:div title]])

(defn todo-list
  []
  [:div
   [:h1 @(rf/subscribe [:todo-list-title])]
   (let [todo-items  (rf/subscribe [:todo-list-items])]
     [:div (map todo-render @todo-items)])
   [:div.todo-stat
    [:div (str "total : " @(rf/subscribe [:todo-items-count]))]]])