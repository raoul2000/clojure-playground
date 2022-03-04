(ns todo.events
  (:require [re-frame.core :as rf]))

;; coeffect to inject a new uuid
(rf/reg-cofx
 :uuid
 (fn [coeffects _]
   (assoc coeffects :uuid (.toString (random-uuid)))))

 ;; add a todo item to the todo list
;; usage:  (dispatch [:add-todo  "a description string"])
(rf/reg-event-fx

 :add-todo
 [(rf/inject-cofx :uuid)]
 (fn [{:keys [db uuid]} [_ text]]
   (print db)
   {:db (-> db
            (update :todos conj {:id   uuid
                                 :text text
                                 :done false}))}))


;; usage:  (dispatch [:update-form "some text"])
(rf/reg-event-db

 :update-form                      ;; todo form value has changed

 (fn [db [_ value]]
   (assoc db :form value)))


(defn toggle-todo-done [todos todo-id]
  (map (fn [{:keys [id] :as m}]
         (if (= id todo-id)
           (update m :done not)
           m)) todos))

(rf/reg-event-db

 :toggle-done

 (fn [db [_ todo-id]]
   (update db :todos toggle-todo-done todo-id)))

