(ns app.todo.events
  (:require [re-frame.core :as rf]
            [shared.db :as db]
            [clojure.spec.alpha :as s]
            [ajax.core :as ajax]))


(def check-todo-spec-interceptor
  (rf/->interceptor {:id :check-todo-spec
                     :after (fn [context]
                              (let [todos (get-in context [:effects :db :todos])
                                    first-id (:todo/id (first (:todo-list/items todos)))]
                                (js/console.log todos)
                                (js/console.log first-id)
                                (js/console.log (s/valid? :todo/id first-id))
                                (js/console.log (uuid? first-id))
                                ;;(db/check-and-throw :todo/id first-id)
                                ;;(db/check-and-throw :todo/list todos)
                                ;;
                                ))}))

(defn dispatch-initialize-todo []
  (rf/dispatch-sync [:fetch-todo-list]))

(rf/reg-event-db
 :initialize-todo
 (fn [_ _]
   {:todos db/initial-todo-list
    :todo-edit-id nil}))

(rf/reg-event-fx
 :fetch-todo-list
 (fn [{:keys [db]} _]
   {:http-xhrio {:method          :get
                 :uri             "/todo"
                 :response-format (ajax/transit-response-format)
                 :on-success      [:success-fetch-todo-list]
                 :on-failure      [:failure-fetch-todo-list]}
    :db          (assoc db :loading? true)}))

(extend-type com.cognitect.transit.types/UUID IUUID)

(rf/reg-event-db
 :success-fetch-todo-list
 ;;[check-todo-spec-interceptor]
 (fn [db [_ result]]
   (-> db
       (assoc :todos    result)
       (assoc :loading? false))))


(rf/reg-event-db
 :failure-fetch-todo-list
 (fn [db [_ result]]
    ;; result is a map containing details of the failure
   (-> db
       (assoc :failure-http-result result)
       (assoc :loading? false))))


(rf/reg-event-db
 :toggle-todo-item
 (fn [db [_ id]]
   (let [todo-list (:todos db)
         todo      (db/read-todo-by-id todo-list id)
         new-list  (db/update-todo todo-list
                                   id
                                   (update todo :todo/done not))]
     (js/console.log (type (:todo/id todo )))
     (when-not (s/valid? :todo/item todo)
       (js/console.log (s/explain-str :todo/item todo)))
     ;;(db/check-and-throw :todo/item todo)
     (assoc db :todos new-list))))

(rf/reg-event-db
 :delete-todo-item
 (fn [db [_ id]]
   (update db :todos #(db/delete-todo % id))))

(rf/reg-event-db
 :edit-todo-item
 (fn [db [_ id]]
   (assoc db :todo-edit-id id)))

(rf/reg-event-db
 :cancel-edit-todo
 (fn [db _]
   (assoc db :todo-edit-id nil)))

(rf/reg-event-db
 :update-todo-title
 (fn [db [_ id title]]
   (-> db
       (update :todos #(db/update-todo-title % id title))
       (assoc :todo-edit-id nil))))

(rf/reg-event-db
 :add-todo-item
 (fn [db _]
   (let [new-todo (db/create-todo "Enter your description ..." false)]
     (-> db
         (update :todos #(db/add-todo-to-list % new-todo))
         (assoc :todo-edit-id (:todo/id new-todo))))))

