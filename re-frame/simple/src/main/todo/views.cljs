(ns todo.views
  (:require [re-frame.core :as rf]
            [clojure.string :as str]
            [reagent.core :as re]))


(defn todo-list
  []
  [:div.todo-list
   (for [todo-text @(rf/subscribe [:todos])]
     [:div.todo-item {:key (:id todo-text)} (:text todo-text)])])

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

(defn todo-stats []
  [:div.todo-stats
   [:span @(rf/subscribe [:todos-count])]])

(defn ui
  []
  [:div.todo-app
   [:h2 "Todos"]
   [todo-stats]
   [todo-list]
   [(todo-form)]])


