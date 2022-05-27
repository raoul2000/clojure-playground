(ns app.main
  (:require [re-frame.core :as rf]
            [reagent.dom :as rdom]
            [app.todo.views :as tdv]
            [app.todo.events :as tde]
            [goog.dom :as gdom]
            [day8.re-frame.http-fx]))

;; helpers

(def gen-uuid #(str (random-uuid)))

(defn delete-contact-by-id [contact-id contact-list]
  (remove #(= contact-id (:id %)) contact-list))

;; Domino 1 - Event Dispatch -----------------------------------

(defn dispatch-initialize []
  (rf/dispatch-sync [:initialize]))

(defn dispatch-add-contact []
  (rf/dispatch [:add-contact]))

(defn dispatch-delete-contact [contact-id]
  (rf/dispatch [:delete-contact contact-id]))

;; Domino 2 - Event Handling -----------------------------------------

(rf/reg-event-db
 :initialize
 (fn [_ _]
   {:contacts [{:id   (gen-uuid)
                :name "bob"}
               {:id   (gen-uuid)
                :name "john"}]}))

(rf/reg-event-db
 :add-contact
 (fn [db _]
   (update db :contacts #(conj %  {:id   (gen-uuid)
                                   :name "bill"}))))

(rf/reg-event-db
 :delete-contact
 (fn [db [_ [contact-id]]]
   (update db :contacts (partial delete-contact-by-id contact-id))))

;; Domino 3 - Effect Handling --------------------------------------

;; Domino 4 - Query ----------------------------------------------

;; ------ Layer 2 - Extractors
;;
;; subscriptions which extract data directly from app-db, but do no further computation.

(defn query-contact-list [db v] (:contacts db))
(rf/reg-sub :contact-list  query-contact-list)


;; ------ Layer 3 - Materialised View
;;
;; subscriptions which obtain data from other subscriptions (never app-db directly), 
;; and compute derived data from their inputs

(rf/reg-sub
 :contact-count

 ;; signal function
 (fn [query-v]
   (rf/subscribe [:contact-list]))

 ;; computation function
 (fn [contact-list query-v]
   (count contact-list)))


;; Domino 5 - View -------------------------------------------------

(defn contact-render [{:keys [id name]}]
  [:div {:key id}
   [:div.contact-info name]
   [:div.contact-actions
    [:input {:type     "button"
             :value    "deleted"
             :on-click #(dispatch-delete-contact [id])}]]])

(defn contacts-list
  []
  [:div.contacts-list
   [:div (str "total : " @(rf/subscribe [:contact-count]))]
   (let [contacts  (rf/subscribe [:contact-list])]
     [:div (map contact-render @contacts)])])

(defn toolbar []
  [:div.toolbar
   [:input  {:type     "button"
             :value    "Add"
             :on-click #(dispatch-add-contact)}]])

(defn ui
  []
  [:div
   (tdv/todo-list)
   ;;[:h1 "Contacts"]
   ;;(contacts-list)
   ;;(toolbar)
   ])

(defn render
  []
  (rdom/render [ui]
               (js/document.getElementById "root")))


(defn run
  []
  ;;(dispatch-initialize)
  (tde/dispatch-initialize-todo)
  (render))

(defn init []
  (js/console.log "hello World !!"))

(defn my-init
  "executed only the first time the module loads
   and NOT on *hot reload*"
  []
  (js/console.log "my-init ...")
  (run)
  (js/fetch "http://localhost:8890/greet"))

(defn after-reload
  "executed after each *hot reload*"
  []
  (js/console.log "after-reload")
  (rf/clear-subscription-cache!)
  (run)
  ;;(js/fetch "http://localhost:8890/greet")
  )

