(ns todo.events
  (:require [re-frame.core :as rf]))

;; coeffect to inject a new uuid
(rf/reg-cofx
 :uuid
 (fn [coeffects _]
   (assoc coeffects :uuid (.toString (random-uuid)))))

 ;; add a todo item to the todo list
;; usage:  (dispatch [:add-todo  "a description string"])
(rf/reg-event-db

 :add-todo                    

 [(rf/inject-cofx :uuid)]
 (fn [db [_ text]]
   (-> db
       (update :todos conj {:id uuid,  :text text})
       (assoc  :form ""))))


;; usage:  (dispatch [:update-form "some text"])
(rf/reg-event-db

 :update-form                      ;; todo form value has changed

 (fn [db [_ value]]
   (assoc db :form value)))

