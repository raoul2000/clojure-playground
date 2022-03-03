(ns todo.views
  (:require [re-frame.core :as rf]
            [clojure.string :as str]))


(defn todo-list
  []
  [:div.todo-list
   (for [todo-text @(rf/subscribe [:todos])]
     [:div.todo-item {:key (:id todo-text)} (:text todo-text)])])

(defn input-form []
  (let [form-val @(rf/subscribe [:form-text])]
    [:div.todo-input
     [:input {:type "text"
              :value form-val
              :on-change #(rf/dispatch [:update-form (.-value (.-target %))])}]
     [:button {:disabled  (str/blank? form-val)
               :on-click #(rf/dispatch [:add-todo form-val])}
      "Add"]]))

(defn todo-stats []
  [:div.todo-stats
   [:span @(rf/subscribe [:todos-count])]])

(defn ui
  []
  [:div.todo-app
   [:h2 "Todos"]
   [todo-stats]
   [todo-list]
   [input-form]])


