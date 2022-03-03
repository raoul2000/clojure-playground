(ns todo.events
  (:require [re-frame.core :as rf]))

;; coeffect to inject a new uuid
(rf/reg-cofx
 :uuid
 (fn [coeffects _]
   (assoc coeffects :uuid (.toString (random-uuid)))))


;; usage:  (dispatch [:add-todo  "a description string"])
(rf/reg-event-fx

 :add-todo
 [(rf/inject-cofx :uuid)]
 (fn [{:keys [db uuid] :as cofx} [_ text]]
   (print db)
   (assoc cofx :db (-> db
                        (update :todos conj {:id uuid
                                             :text text})
                        (assoc  :form "")))))



;; usage:  (dispatch [:update-form "some text"])
(rf/reg-event-db

 :update-form                      ;; todo form value has changed

 (fn [db [_ value]]
   (assoc db :form value)))

