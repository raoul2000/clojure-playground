(ns app
  (:require [reagent.dom :as rdom]
            [goog.string :as gstr]
            [day8.re-frame.http-fx]
            [maze :as maze]
            [re-frame.core :as rf]))

;; subs ---------------------------------------------
(defn create-initial-state []
  (merge (maze/init-state [[:origin :clear :clear]
                           [:clear :clear :clear]
                           [:clear :clear :target]]
                          [0 0]
                          [2 2])
         {:pen :wall}))

(def pen-coll {:wall   {:symbol "🟦"    :label "Wall"}
               :origin {:symbol "🔰"    :label "origin"}
               :target {:symbol "🏁"    :label "target"}
               :clear  {:symbol "."      :label "clear"}})

(defn pen-symbol [k]
  (get-in pen-coll [k :symbol]))

(defn pen-label [k]
  (get-in pen-coll [k :label]))

(rf/reg-sub
 :grid
 (fn [db _]
   (:grid db)))

(defn <maze []
  @(rf/subscribe [:grid]))

(rf/reg-sub
 :pen
 (fn [db _]
   (:pen db)))

(defn <pen []
  @(rf/subscribe [:pen]))

;; events ----------------------------------------------

(rf/reg-event-db
 :initialize-state
 (fn [_ _]
   (create-initial-state)))

(defn >initialize-state []
  (rf/dispatch-sync [:initialize-state]))


(defn set-at-pos-handler [db [_ pos]]
  (update db :grid maze/set-at-position pos (:pen db)))

(rf/reg-event-db
 :set-at-pos
 set-at-pos-handler)

(defn >set-at-pos [pos]
  (rf/dispatch [:set-at-pos pos]))

(rf/reg-event-db
 :update-pen
 (fn [db [_ new-pen]]
   (assoc db :pen new-pen)))

(defn >update-pen [new-pen]
  (rf/dispatch [:update-pen new-pen]))


(rf/reg-event-db
 :reset
 (fn [db _]
   (merge db (create-initial-state))))

(defn >reset []
  (rf/dispatch [:reset]))


(defn set-adjacent-pos [grid pos]
  (if pos
    (reduce (fn [acc pos]
              (maze/set-at-position acc pos :target))
            grid
            (maze/free-adjacent-positions pos #(= :clear (maze/get-at-position grid %))))
    grid))

(rf/reg-event-db
 :select-adjacent
 (fn [db _]
   (update db :grid set-adjacent-pos (maze/find-in-grid (:grid db) :origin))))

(defn >select-adjacent []
  (rf/dispatch [:select-adjacent]))

;; view ------------------------------------------------

(defn render-col [y x col]
  [:td.is-clickable
   {:key     x
    :data-x  x
    :data-y  y
    :style {:background-color "yello"
            :border  "2 px solid green"}
    :on-click (fn [e]
                (let [dataset (-> e .-target .-dataset)
                      pos [(js/parseInt (.-x dataset))
                           (js/parseInt (.-y dataset))]]
                  (>set-at-pos pos)))}
   (pen-symbol col)])

(defn render-row [y row]
  [:tr
   {:key y}
   (map-indexed (partial render-col y) row)])

(defn maze []
  (let [grid (<maze)]
    [:div
     [:table.table.is-bordered
      [:tbody
       (map-indexed render-row grid)]]]))

(defn select-pen []
  (let [pen (<pen)]
    [:div.level
     [:div.level-left
      (map (fn [[k _]]
             [:div.level-item {:key k}
              [:button.button.is-info {:on-click #(>update-pen k)
                                       :class (when (= pen k) "is-danger")}
               (str (pen-symbol k) " " (pen-label k))]]) pen-coll)]]))

(defn action-bar []
  [:div
   [:button.button {:on-click #(>select-adjacent)} "Adjacent"]
   [:button.button.is-danger {:on-click #(>reset)} "Reset "]])

;; main -------------------------------------------------


(defn app-page []
  [:div
   [:div.box
    [maze]]
   [:div.box
    [action-bar]]
   [:div.box
    [select-pen]]])

(defn render [element-id]
  (js/console.log "render")
  (>initialize-state)
  (rdom/render [app-page] (js/document.getElementById element-id)))