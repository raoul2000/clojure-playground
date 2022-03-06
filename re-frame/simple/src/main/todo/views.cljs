(ns todo.views
  (:require [re-frame.core :as rf]
            [clojure.string :as str]
            [reagent.core :as re]))

(defn todo-form []
  (let [text-val (re/atom "")]
    (fn []                        ;; create a closure to capture the atom
      [:div.input
       [:input {:type "text"
                :value @text-val
                :on-change #(reset! text-val (-> % .-target .-value))}]
       [:button {:disabled (str/blank? @text-val)
                 :on-click #(let [todo-text @text-val]
                              (rf/dispatch [:add-todo todo-text])
                              (reset! text-val ""))}
        "Add"]])))

(defn todos-count []
  (let [todo-count @(rf/subscribe [:todos-count])]
    [:div (str "total : " todo-count)]))

(defn todos-done-count []
  (let [todo-done-count @(rf/subscribe [:todos-done-count])]
    [:div (str "done : " todo-done-count)]))

(defn todo-stats []
  [:div.todo-stats
   [todos-count]
   [todos-done-count]])

(defn todo-item [todo-id]
  ;; keep track of render count when todo info change.
  ;; Use a non-reactive atom (a clojure atom instead of a reagent atom) because
  ;; we don't want to trigger a render on each atom change
  (let [count-render (atom 0)]
    ;; form-2 re-agent component is required to capture the count-render atom
    (fn []
      (let [{:keys [text done]} @(rf/subscribe [:todo-info todo-id])]
        (swap! count-render inc)
        [:div {:class "todo"
               :key todo-id}
         [:div.text
          {:style (when done {:text-decoration "line-through"})}
          [:small
           (str " (render count = " @count-render ") ")]
          text]

         [:button
          {:on-click #(rf/dispatch [:toggle-done todo-id])}
          (if done "undo" "done")]
         [:button
          {:on-click #(rf/dispatch [:remove-todo todo-id])}
          "delete"]]))))

(defn todo-list []
  [:div.todo-list
   (for [id @(rf/subscribe [:filtered-todo-ids])]
     ^{:key id} [(todo-item id)])])

(defn view-filter []
  [:div#view-filter
   [:select {:name "view-filter"
             :on-change #(rf/dispatch [:select-filter (.-value (.-target %))])}
    [:option {:value :all}    "show all"]
    [:option {:value :done}   "done"]
    [:option {:value :undone} "not done"]]])

(defn ui
  []
  [:div.todo-list-container
   [:h1 "Todos"]
   (let [loading @(rf/subscribe [:loading])]
     (if loading
       [:div.loading
        "loading todo list, please wait ..."]
       [:div
        [view-filter]
        [todo-stats]
        [todo-list]
        [todo-form]]))

   [:button {:on-click #(rf/dispatch [:fetch-todos])}
    "Load from server"]])


