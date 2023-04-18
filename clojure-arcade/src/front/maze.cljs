(ns maze
  (:require [reagent.dom :as rdom]
            [goog.string :as gstr]
            [day8.re-frame.http-fx]
            [re-frame.core :as rf]))

;; subs ---------------------------------------------
(defn create-initial-state []
  {:maze [[1 0 0]
          [0 0 0]
          [0 0 0]]
   :pen   "Y"})

(rf/reg-sub :maze
            (fn [db _]
              (:maze db)))

(defn <maze []
  @(rf/subscribe [:maze]))

;; events ----------------------------------------------

(defn initialize-state-handler [_ _]
  (create-initial-state))

(rf/reg-event-db
 :initialize-state
 initialize-state-handler)

(defn >initialize-state []
  (rf/dispatch-sync [:initialize-state]))

(defn set-position [grid [x y] s]
  (update grid y #(update % x (constantly s))))

(comment
  (def g [[1 2 3]
          [4 5 6]
          [7 8 9]])

  (set-position g [1 2] "E")
  (set-position g [0 2] "E")
  (set-position g [0 3] "E") ;; throws
  ;;
  )

(defn set-at-pos-handler [db [_ pos s]]
  (update db :maze set-position pos (:pen db)))

(rf/reg-event-db
 :set-at-pos
 set-at-pos-handler)

(defn >set-at-pos [pos s]
  (js/console.log (str "pos=" pos))
  (js/console.log (str "s=" s))
  (rf/dispatch [:set-at-pos pos s]))

(rf/reg-event-db
 :update-pen
 (fn [db [_ new-pen]]
   (assoc db :pen new-pen)))

(defn >update-pen [new-pen]
  (rf/dispatch [:update-pen new-pen]))


;; view ------------------------------------------------

(defn render-col [y x col]
  [:td.is-clickable
   {:key     x
    :data-x  x
    :data-y  y
    :on-click (fn [e]
                (let [dataset (-> e .-target .-dataset)
                      pos [(js/parseInt (.-x dataset))
                           (js/parseInt (.-y dataset))]]
                  (>set-at-pos pos "X")))}
   (case col
     0 "."
     1 "."
     col)])

(defn render-row [y row]
  [:tr
   {:key y}
   (map-indexed (partial render-col y) row)])

(defn maze []
  (let [grid (<maze)]
    [:div
     [:table.table.is-bordered
      (map-indexed render-row grid)]]))

(defn select-pen []
  [:div.level
   [:div.level-left
    [:div.level-item
     [:button.button.is-info {:on-click #(>update-pen "🟦")} "🟦 wall"]]
    [:div.level-item
     [:button.button.is-info {:on-click #(>update-pen "🔰")} "🔰 origin"]]
    [:div.level-item
     [:button.button.is-info {:on-click #(>update-pen "🏁")} "🏁 target"]]
    [:div.level-item
     [:button.button.is-info {:on-click #(>update-pen " ")} "clear"]]]])

;; main -------------------------------------------------
;; 👻 😐 🏁 🔰

(defn app-page []
  [:div
   [:div.box
    [maze]]
   [:div.box
    [select-pen]]])

(defn render [element-id]
  (js/console.log "render")
  (>initialize-state)
  (rdom/render [app-page] (js/document.getElementById element-id)))