(ns todo.subs
  (:require [re-frame.core :as rf]))

;; Layer 2 ----------------------------------------------------------
(rf/reg-sub
 :todos
 (fn [db _]
   (:todos db)))

;; Layer 3  ------------------------------------------------------

(rf/reg-sub
 :todos-count

 (fn [_]
   [(rf/subscribe [:todos])])

 (fn [[todos] _]
   (count todos)))