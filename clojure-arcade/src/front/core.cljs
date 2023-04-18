(ns core
  (:require
   [re-frame.core :as rf]
   [app :as app]))


(defn run []
  (app/render "root"))

(def debug? ^boolean goog.DEBUG)

(defn dev-setup []
  (when debug?
    (enable-console-print!)
    (js/console.log "%c -- dev mode --" "background: #2196f3; color: white")))

;;  Lifecycle Hooks =================================


(defn ^:dev/before-load stop []
  (js/console.log "/before-load"))

(defn ^:dev/after-load start []
  (js/console.log "after-load")
  (dev-setup)
  (rf/clear-subscription-cache!)
  (run))

(defn ^:dev/before-load-async async-stop [done]
  (js/console.log "stop")
  (js/setTimeout
   (fn []
     (js/console.log "stop complete")
     (done))))

(defn ^:dev/after-load-async async-start [done]
  (js/console.log "start")
  (js/setTimeout
   (fn []
     (js/console.log "start complete")
     (done))))