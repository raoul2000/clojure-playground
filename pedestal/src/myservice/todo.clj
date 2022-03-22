(ns myservice.todo
  (:require [io.pedestal.log :as log]
            [myservice.response :as resp]
            [io.pedestal.http.route :refer [url-for]]))

(defonce database (atom {}))

(defn make-list [list-name]
  {:name  list-name
   :items {}})

(defn make-list-item [name]
  {:name  name
   :done? false})

(defn find-list-by-id [dbval db-id]
  (get dbval db-id))

(defn find-list-item-by-ids [dbval list-id item-id]
  (get-in dbval [list-id :items item-id] nil))

(defn list-item-add
  [dbval list-id item-id new-item]
  (if (contains? dbval list-id)
    (assoc-in dbval [list-id :items item-id] new-item)
    dbval))

;; interceptors -----------------------------------------------------------------

(def db-interceptor
  {:name :database-interceptor
   :enter
   ;; on enter, add the :database key with the databaso as value
   (fn [context]
     (log/info "db-interceptor" "hello")
     (update context :request assoc :database @database))  ;; put the current database in the context
   :leave
   ;; if the :tx-data key is set, it contains transaction data consisting in operation
   ;; and required argumnts to apply to the database. These are taken into account and applied
   ;; to the database stored in :database key during the enter phase
   (fn [context]
     (if-let [[op & args] (:tx-data context)]
       (do
         (apply swap! database op args)     ;; with op = assoc and args = db-id new-list
                                            ;; add a new list into the database where the key
                                            ;; is the id of the new list
         (assoc-in context [:request :database] @database))
       context))})

(def entity-render
  {:name :entity-render
   :leave
   (fn [context]
     (if-let [item (:result context)]
       (assoc context :response (resp/ok item))
       context))})

(def list-create
  "Interceptor for list creation.
   - `:query-params :name` : (optional) Name of the list to create
   "
  {:name :list-create
   :enter
   (fn [context]
     (let [nm       (get-in context [:request :query-params :name] "Unnamed List")
           new-list (make-list nm)
           db-id    (str (gensym "l"))
           url      (url-for :list-view :params {:list-id db-id})]
       (assoc context
              :tx-data [assoc db-id new-list]
              :response (resp/created {:id   db-id
                                       :item new-list}  "Location" url))))})

(def list-view
  "given a list id in the `:list-id` path param, add the key `:result` to the context
   with value being the list with id `:list-id`"
  {:name :list-view
   :enter
   (fn [context]
     (if-let [db-id (get-in context [:request :path-params :list-id])]
       (if-let [the-list (find-list-by-id (get-in context [:request :database]) db-id)]
         (assoc context :result the-list)
         context)
       context))})

(def list-item-view
  {:name :list-item-view
   :leave
   (fn [context]
     (if-let [list-id (get-in context [:request :path-params :list-id])]
       (if-let [item-id (get-in context [:request :path-params :item-id])]
         (if-let [item (find-list-item-by-ids (get-in context [:request :database]) list-id item-id)] ;; if the item could not be added to the database
                                                                                                      ;; (e.g. list was deleted) return context with no :result
           (assoc context :result item)
           context) ;; no :result in context : entity-render will not produce :response (404)
         context)
       context))})

;; final interceptor for todo item creation
;; - Checks that list can be found with id list-id
;; - create a new item
;; - store all in transaction data key (:tx-data) in the context
;; - also add the new item id to the context
;; The item will be added in an atomic way by the db-interceptor. This is to be sure that between the time the item is
;; created and actually added to the list, the list has not been deleted: the operation must be atomic

(def list-item-create
  {:name :list-item-create
   :enter
   (fn [context]
     (if-let [list-id (get-in context [:request :path-params :list-id])]                  ;; ensure list-id exists
       (let [nm       (get-in context [:request :query-params :name] "Unnamed Item")
             new-item (make-list-item nm)
             item-id  (str (gensym "i"))]                                                 ;; generate a new id for the item
         (-> context
             (assoc :tx-data  [list-item-add list-id item-id new-item])                   ;; list-item-add will be invoked on swap! database
                                                                                          ;; by the db-interceptor
             (assoc-in [:request :path-params :item-id] item-id)))                        ;; item-id will be needed by the db-interceptor
       context))})