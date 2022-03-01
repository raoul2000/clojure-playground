(ns todo.events
  (:require [re-frame.core :as rf]))

;; usage:  (dispatch [:add-todo  "a description string"])
(rf/reg-event-db
 
 :add-todo
 
 (fn [db [_ text]]
   (-> db
       (update :todos conj text)
       (assoc  :form ""))))



;; usage:  (dispatch [:update-form "some text"])
(rf/reg-event-db
 
 :update-form                      ;; todo form value has changed

 (fn [db [_ value]]
   (assoc db :form value)))

