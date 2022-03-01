(ns todo.subs
  (:require [re-frame.core :as rf]))

;; Layer 2 ----------------------------------------------------------
(rf/reg-sub
 :todos
 (fn [db _]
   (:todos db)))

(rf/reg-sub
 :form-text
 (fn [db _]
   (:form db)))


;; Layer 3  ------------------------------------------------------