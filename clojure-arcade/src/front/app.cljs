(ns app
  (:require [reagent.dom :as rdom]
            [goog.string :as gstr]
            [day8.re-frame.http-fx]
            [maze :as maze]
            [re-frame.core :as rf]))

;; subs ---------------------------------------------
(defn create-initial-state []
  {:maze [[:clear :clear :clear]
          [:clear :clear :clear]
          [:clear :clear :clear]]
   :pen   :wall})

(def pen-coll {:wall   {:symbol "🟦"    :label "Wall"}
               :origin {:symbol "🔰"    :label "origin"}
               :target {:symbol "🏁"    :label "target"}
               :clear  {:symbol "."      :label "clear"}})

(defn pen-symbol [k]
  (get-in pen-coll [k :symbol]))

(defn pen-label [k]
  (get-in pen-coll [k :label]))

(rf/reg-sub
 :maze
 (fn [db _]
   (:maze db)))

(defn <maze []
  @(rf/subscribe [:maze]))

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
  (update db :maze maze/set-at-position pos (:pen db)))

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
      (map-indexed render-row grid)]]))

(defn select-pen []
  (let [pen (<pen)]
    [:div.level
     [:div.level-left
      (map (fn [[k _]]
             [:div.level-item {:key k}
              [:button.button.is-info {:on-click #(>update-pen k)
                                       :class (when (= pen k) "is-danger")}
               (str (pen-symbol k) " " (pen-label k))]]) pen-coll)]]))

;; main -------------------------------------------------


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