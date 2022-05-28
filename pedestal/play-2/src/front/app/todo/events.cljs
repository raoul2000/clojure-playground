(ns app.todo.events
  (:require [re-frame.core :as rf]
            [shared.db :as db]
            [clojure.spec.alpha :as s]
            [ajax.core :as ajax]))


(def check-todo-spec-interceptor
  (rf/->interceptor {:id :check-todo-spec
                     :after (fn [context]
                              (let [todo-list (get-in context [:effects :db :todos])]
                                (db/check-and-throw :todo/list todo-list)
                                context))}))



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



(rf/reg-event-db
 :success-fetch-todo-list
 [check-todo-spec-interceptor]
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

(rf/reg-event-fx
 :put-todo-list
 (fn [{:keys [db]} _]
   {:http-xhrio {:method          :put
                 :uri             "/todo"
                 :format          (ajax/transit-request-format)
                 :params          (:todos db)
                 :response-format (ajax/transit-response-format)
                 :on-success      [:success-put-todo-list]
                 :on-failure      [:failure-put-todo-list]}
    :db          db}))

;; ok we want to save the todo-list to the server each time its done state changes
;; In this first version, we register an event as -fx so it not only updated the 
;; todo-list stored in the db, but also sets an effect to trigger the :put-todo-list event
;; implemented just above.

(rf/reg-event-fx
 :toggle-todo-item-1
 (fn [{:keys [db]} [_ id]]                                              ;; destructure the context map to get the db
   (let [todo-list (:todos db)
         todo      (db/read-todo-by-id todo-list id)
         new-list  (db/update-todo todo-list                            ;; create the updated todo-list
                                   id
                                   (update todo :todo/done not))]
     {:db       {:todos new-list}                                       ;; returns the updated :todos
      :dispatch [:put-todo-list]})))                                    ;; add the :dispatch effect to trigger :put-todo-list event

;; In this second implementation, we'll be using a custom interceptor applied 'after' the event handler 
;; is invoked. This interceptor ('save-todo-list-intrec') take the :todos from the db and PUT them to the server.
;; This is done by adding the :http-xhrio effect to the map.

(def save-todo-list-intrec
  (rf/->interceptor {:id :save-todo-list-interc
                     :after (fn [context]       ;; applied after the db is updated
                              (let [todo-list (get-in context [:effects :db :todos])]
                                (update-in context [:effects] #(assoc % :http-xhrio {:method          :put
                                                                                     :uri             "/todo"
                                                                                     :format          (ajax/transit-request-format)
                                                                                     :params          todo-list
                                                                                     :response-format (ajax/transit-response-format)
                                                                                     :on-success      [:success-put-todo-list]
                                                                                     :on-failure      [:failure-put-todo-list]}))))}))

(rf/reg-event-db
 :toggle-todo-item
 [save-todo-list-intrec]                                              ;; delcare interceptor and that's all
 (fn [db [_ id]]
   (let [todo-list (:todos db)
         todo      (db/read-todo-by-id todo-list id)
         new-list  (db/update-todo todo-list
                                   id
                                   (update todo :todo/done not))]
     (assoc db :todos new-list))))

;; Conclusion: using a dedicated custom interceptor keeps the event handler code cleaner. The same behavior
;; to save todo-list to the server can be added to many event handler without code changes, just by adding
;; this interceptor
;; ..and this we will do.

(rf/reg-event-db
 :delete-todo-item
 [save-todo-list-intrec]
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
 [save-todo-list-intrec]
 (fn [db [_ id title]]
   (-> db
       (update :todos #(db/update-todo-title % id title))
       (assoc :todo-edit-id nil))))

(rf/reg-event-db 
 :add-todo-item
 [save-todo-list-intrec]
 (fn [db _]
   (let [new-todo (db/create-todo "Enter your description ..." false)]
     (-> db
         (update :todos #(db/add-todo-to-list % new-todo))
         (assoc :todo-edit-id (:todo/id new-todo))))))

