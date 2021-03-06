(ns todo.events
  (:require [re-frame.core :as rf]
            [ajax.core :as ajax]
            [day8.re-frame.http-fx]))

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

;; ---------------------------------------------
(defn toggle-todo-done [todos todo-id]
  (map (fn [{:keys [id] :as m}]
         (if (= id todo-id)
           (update m :done not)
           m)) todos))

(rf/reg-event-db :toggle-done
                 (fn [db [_ todo-id]]
                   (update db :todos toggle-todo-done todo-id)))

;; ---------------------------------------------

(defn remove-todo [todos todo-id]
  (remove #(= todo-id (:id %)) todos))

(rf/reg-event-db

 :remove-todo

 (fn [db [_ todo-id]]
   (update db :todos remove-todo todo-id)))


;; ---------------------------------------------

(defn response->todos
  "Convert request response into a seq of todo maps"
  [response]
  (->> response
       (take 10)
       (map #(hash-map :id   (:id %)
                       :text (:title %)
                       :done (:completed %)))))

(rf/reg-event-db
 :process-response
 (fn [db [_ response]]
   (-> db
       (assoc :todos  (response->todos response))
       (assoc :loading false))))

(rf/reg-event-db
 :bad-response
 (fn [db _]
   (-> db
       (assoc :todos [])
       (assoc :loading false))))

(rf/reg-event-fx
 :fetch-todos

 (fn [{:keys [db]} _]
   {:http-xhrio {:method          :get
                 :uri             "https://jsonplaceholder.typicode.com/todos"
                 :format          (ajax/json-request-format)
                 :response-format (ajax/json-response-format {:keywords? true})
                 :on-success      [:process-response]
                 :on-failure      [:bad-response]}
    :db (assoc db :loading true)}))

;; filter ---------------------------------------------

;; set the show todo filter
;; usage (dispatch [:select-filter :done])

(rf/reg-event-db
 :select-filter

 (fn [db [_ filter-name]]
   (assoc db :show filter-name)))