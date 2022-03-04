(ns todo.views
  (:require [re-frame.core :as rf]
            [clojure.string :as str]
            [reagent.core :as re]))


(defn todo-item [{:keys [id text done]}]
  [:div.todo-item {:key id
                   :style (when done {:text-decoration "line-through"})}
   [:div text]
   [:button
    {:on-click #(rf/dispatch [:toggle-done id])}
    (if done "undo" "done")]
   [:button
    {:on-click #(rf/dispatch [:remove-todo id])}
    "delete"]])

(defn todo-list
  []
  [:div.todo-list
   (map todo-item @(rf/subscribe [:todos]))])

(defn todo-form []
  (let [text-val (re/atom "")]
    (fn []                        ;; create a closure to capture the atom
      [:div.todo-form
       [:input {:type "text"
                :value @text-val
                :on-change #(reset! text-val (-> % .-target .-value))}]
       [:button {:disabled (str/blank? @text-val)
                 :on-click #(let [todo-text @text-val]
                              (rf/dispatch [:add-todo todo-text])
                              (reset! text-val ""))}
        "Add"]])))


(defn todos-count []
  [:div @(rf/subscribe [:todos-count])])

(defn todos-done-count []
  [:div @(rf/subscribe [:todos-done-count])])

(defn todo-stats []
  [:div.todo-stats
   (todos-count)
   (todos-done-count)])

(defn ui
  []
  [:div.todo-app
   [:h2 "Todos"]
   [todo-stats]
   [todo-list]
   [(todo-form)]])


