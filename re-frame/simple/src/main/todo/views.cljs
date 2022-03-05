(ns todo.views
  (:require [re-frame.core :as rf]
            [clojure.string :as str]
            [reagent.core :as re]))

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

(defn todo-item [todo-id]
  ;; keep track of render count when todo info change.
  ;; Use a non-reactive atom (a clojure atom instead of a reagent atom) because
  ;; we don't want to trigger a render on each atom change
  (let [count-render (atom 0)]
    (fn []
      (let [{:keys [text done]} @(rf/subscribe [:todo-info todo-id])]
        (swap! count-render inc)
        [:div {:key todo-id
               :style (when done {:text-decoration "line-through"})}
         [:div
          (str text " (render counr = " @count-render ") ")]
         [:button
          {:on-click #(rf/dispatch [:toggle-done todo-id])}
          (if done "undo" "done")]
         [:button
          {:on-click #(rf/dispatch [:remove-todo todo-id])}
          "delete"]]))))

(defn todo-list []
  [:div.todo-list2
   (for [id @(rf/subscribe [:todo-ids])]
     [(todo-item id)])])


(defn ui
  []
  [:div.todo-app
   [:h2 "Todos"]
   [todo-stats]
   [todo-list]
   [(todo-form)]
   [:button {:on-click #(rf/dispatch [:fetch-todos])}
    "Load from server"]])


