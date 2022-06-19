(ns core
  (:require [reagent.dom :as rdom]
            [re-frame.core :as rf]
            [ajax.core :refer [GET]]))

(defonce default-db {:loading false
                     :response nil
                     :count    0})

(rf/reg-event-db
 :initialize
 (fn [db [event-id args]]
   default-db))

(rf/reg-event-db
 :increment-count
 (fn [db _]
   (update db :count inc)))

(rf/reg-event-db
 :fetch-success
 (fn [db [_ response]]
   (js/console.log response)
   (-> db
       (assoc :response response)
       (assoc :loading false))))

(rf/reg-event-db
 :fetch-error
 (fn [db [_ response]]
   (-> db
       (assoc :error response)
       (assoc :loading false))))

(rf/reg-event-fx
 :fetch
 (fn [cofx [_ arg-map]]
   (-> cofx
       (assoc     :fetch-from    arg-map)
       (update-in [:db :loading] #(not %)))))

(rf/reg-fx
 :fetch-from
 (fn [url]
   (js/setTimeout #(do
                     (js/console.log "timeout")
                     (GET url {:handler         (fn [response] (rf/dispatch [:fetch-success response]))
                               :error-handler   (fn [response] (rf/dispatch [:fetch-error   response]))
                               :response-format :json})) 1000)))

(defn dispatch-initialize [] (rf/dispatch [:initialize]))
(defn dispatch-increment  [] (rf/dispatch [:increment-count]))
(defn dispatch-fetch      [] (rf/dispatch [:fetch "http://localhost:8890/greet"]))

(rf/reg-sub
 :counter-update
 (fn [db _]
   (:count db)))

(rf/reg-sub
 :loading
 (fn [db _]
   (:loading db)))

;; ---------------------------------------------------------------------------------

(defn handler [response]
  (.log js/console  response)
  (let [resp (js->clj response :keywordize-keys true)]
    (.log js/console (:reply resp))))

(defn error-handler [{:keys [status status-text]}]
  (.log js/console (str "something bad happened: " status " " status-text)))

(defn get-data []
  (GET "http://localhost:8890/greet" {:handler         handler
                                      :error-handler   error-handler
                                      :response-format :json}))
(defn ui-counter-view []
  (let [counter-val @(rf/subscribe [:counter-update])]
    [:div
     [:p "value is "
      [:span counter-val]]]))

(defn ui-loader []
  (let [loading @(rf/subscribe [:loading])]
    [:div (when loading "loading ...")]))

(defn view []
  [:div
   [:h1 "Hello"]
   [ui-counter-view]
   [:button {:onClick dispatch-increment} "increment"]
   [:button {:onClick dispatch-fetch} "fetch"]
   [ui-loader]])

(defn render
  []
  (dispatch-initialize)
  (reagent.dom/render [view]
                      (js/document.getElementById "root")))

(defn init []
  (js/console.clear)
  (println "hello world")
  (render)
  ;;
  )

(defn start []
  (js/console.log "start")
  (init))

(comment
  ;; on reload lifecycle hooks can be set in the configuration (shadow-cljs.edn)
  ;; or like below, directly as function metadata
  (defn ^:dev/before-load before-reload []
    (js/console.log "stop"))


  (defn ^:dev/after-load after-reload []
    (js/console.clear)
    (init)))