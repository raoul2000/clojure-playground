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

;; interceptors -----------------------------------------------------------------

(def db-interceptor
  {:name :database-interceptor
   :enter
   ;; on enter, add the :database key with the databaso as value
   (fn [context]
     (log/info "db-interceptor" "hello")
     (update context :request assoc :database @database))
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

;; handler (last interceptor) -----------------------------------------------------------------

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
              :response (resp/created new-list  "Location" url))))})

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

(def entity-render
  {:name :entity-render
   :leave
   (fn [context]
     (if-let [item (:result context)]
       (assoc context :response (resp/ok item))
       context))})