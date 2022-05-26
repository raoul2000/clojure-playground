(ns app.todo.views
  (:require [re-frame.core :as rf]
            [app.todo.events]
            [app.todo.subs]
            [reagent.core :as r]))

(defn main-toolbar []
  [:div.todo-action
   [:div {:on-click #(rf/dispatch [:add-todo-item])} "Add TODO"]])

(defn action-bar-view [todo-id]
  [:div.todo-action
   [:div {:on-click #(rf/dispatch [:edit-todo-item   todo-id])} "edit"]
   [:div {:on-click #(rf/dispatch [:delete-todo-item todo-id])} "delete"]])

(defn action-bar-edit [todo-id new-title]
  [:div.todo-action
   [:div {:on-click #(rf/dispatch [:cancel-edit-todo  todo-id])}           "cancel"]
   [:div {:on-click #(rf/dispatch [:update-todo-title todo-id new-title])} "save"]])

(defn todo-view [{:keys [:todo/id :todo/title :todo/done]}]
  (fn []
    [:li {:class [(when done "todo-done")]}
     [:label
      [:input {:id        (str "chk-" id)
               :type      :checkbox
               :checked   done
               :on-change #(rf/dispatch [:toggle-todo-item id])}]
      title]
     (action-bar-view id)]))

(defn todo-edit [{:keys [:todo/id :todo/title]}]
  (let [title-val (r/atom title)]
    (fn []
      [:li
       [:textarea {:value     @title-val
                   :placeholder "What do you have to do ?"
                   :on-change #(reset! title-val (-> % .-target .-value))}]
       (action-bar-edit id @title-val)])))

(defn todo-render [id]
  (let [todo-item    @(rf/subscribe [:todo-item id])
        todo-edit-id @(rf/subscribe [:todo-edit-id])]
    (if (= todo-edit-id (:todo/id todo-item))
      ^{:key id} [(todo-edit todo-item)]
      ^{:key id} [(todo-view todo-item)])))

(defn todo-list
  []
  [:div.todo-list
   [:h1 @(rf/subscribe [:todo-list-title])]

   (main-toolbar)

   (let [todo-ids @(rf/subscribe [:todo-items-id])]
     (when (seq todo-ids)
       (doall (map todo-render todo-ids))))])