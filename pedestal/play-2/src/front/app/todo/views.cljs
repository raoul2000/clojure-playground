(ns app.todo.views
  (:require [re-frame.core :as rf]
            [app.todo.events]
            [app.todo.subs]
            [reagent.core :as r]
            [reagent.dom :as rdom]))

(defn main-toolbar []
  [:div.todo-action
   [:div "Add TODO"]])

(defn todo-view [id title done]
  [:li {:key id
        :class [(when done "todo-done")]}

   [:label
    [:input {:id (str "chk-" id)
             :type :checkbox
             :checked done
             :on-change #(rf/dispatch [:toggle-todo-item id])}]
    title]
   [:div.todo-action
    [:div {:on-click #(rf/dispatch [:edit-todo-item   id])} "edit"]
    [:div {:on-click #(rf/dispatch [:delete-todo-item id])} "delete"]]])

(defn todo-edit [id title]
  (let [title-val (r/atom title)]
    (fn []
      [:li
       [:textarea {:value @title-val
                   :on-change #(reset! title-val (-> % .-target .-value))}]
       [:div.todo-action
        [:div {:on-click #(rf/dispatch [:cancel-edit-todo  id])}            "cancel"]
        [:div {:on-click #(rf/dispatch [:update-todo-title id @title-val])} "save"]]])))

(defn todo-render [todo-edit-id {:keys [:todo/id :todo/title :todo/done]}]
  (if (= todo-edit-id id)
    [(todo-edit id title)]
    (todo-view id title done)))

(defn todo-list
  []
  [:div.todo-list
   [:h1 @(rf/subscribe [:todo-list-title])]
   (main-toolbar)

   (let [todo-items  @(rf/subscribe [:todo-list-items])
         todo-edit-id @(rf/subscribe [:todo-edit-id])]
     (when (seq todo-items)
       [:ul
        (map (partial todo-render todo-edit-id) todo-items)]))])