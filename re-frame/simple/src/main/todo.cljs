(ns todo
  (:require [reagent.dom]
            [re-frame.core :as rf]
            [clojure.string :as str]))

;; 1. Event Dispatch ----------------------------------------

(defn dispatch-add-todo [text]
  (rf/dispatch [:add text]))

;; 2. Event Handlers -----------------------------------------

(rf/reg-event-db
 :initialize
 (fn [_ _]
   {:todos ["first task"]
    :form ""}))

(rf/reg-event-db
 :add
 (fn [db [_ text]]
   (-> db
       (update :todos conj text)
       (assoc :form ""))))

(rf/reg-event-db
 :update-form                      ;; todo form value has changed
 (fn [db [_ value]]
   (assoc db :form value)))

;; 4. Query ------------------------------------------------

(rf/reg-sub
 :add
 (fn [db _]
   (:todos db)))

(rf/reg-sub
 :form-change
 (fn [db _]
   (:form db)))

;; 5. View Functions -----------------------------------------

(defn todo-list
  []
  [:div.todo-list
   (for [todo-text @(rf/subscribe [:add])]
     [:div.todo-item {:key todo-text} todo-text])])

(defn input-form []
  (let [form-val @(rf/subscribe [:form-change])]
    [:div.todo-input
     [:input {:type "text"
              :value form-val
              :on-change #(rf/dispatch [:update-form (.-value (.-target %))])}]
     [:button {:disabled  (str/blank? form-val)
               :on-click #(dispatch-add-todo  form-val)}
      "Add"]]))

(defn ui
  []
  [:div.todo-app
   [:h2 "Todos"]
   [todo-list]
   [input-form]])

;; -- Entry Point ----------------------------------

(defn render
  []
  (reagent.dom/render [ui]
                      (js/document.getElementById "app")))

(defn ^:dev/after-load clear-cache-and-render!
  []
  ;; The `:dev/after-load` metadata causes this function to be called
  ;; after shadow-cljs hot-reloads code. We force a UI update by clearing
  ;; the Reframe subscription cache.
  (rf/clear-subscription-cache!)
  (render))

(defn run
  []
  (rf/dispatch-sync [:initialize]) ;; put a value into application state
  (render))                         ;; mount the application's ui into '<div id="app" />'


