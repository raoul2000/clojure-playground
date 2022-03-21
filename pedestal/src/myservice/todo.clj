(ns myservice.todo)

(defonce database (atom {}))



(defn make-list [list-name]
  {:name  list-name
   :items {}})

(defn make-list-item [name]
  {:name  name
   :done? false})

(def db-interceptor
  {:name :database-interceptor
   :enter
   ;; on enter, add the :database key with the databaso as value
   (fn [context]
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

(def list-create
  {:name :list-create
   :enter
   (fn [context]
     (let [nm       (get-in context [:request :query-params :name] "Unnamed List")
           new-list (make-list nm)
           db-id    (str (gensym "l"))]
       (assoc context :tx-data [assoc db-id new-list])))})