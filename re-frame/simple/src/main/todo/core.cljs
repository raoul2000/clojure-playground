(ns todo.core
  (:require [reagent.dom]
            [re-frame.core :as rf]
            [todo.views :as views]
            [todo.events]
            [todo.subs]))

;; -- Entry Point ----------------------------------

(defn render
  []
  (reagent.dom/render [views/ui]
                      (js/document.getElementById "app")))

(defn ^:dev/after-load clear-cache-and-render!
  []
  ;; The `:dev/after-load` metadata causes this function to be called
  ;; after shadow-cljs hot-reloads code. We force a UI update by clearing
  ;; the Reframe subscription cache.
  (rf/clear-subscription-cache!)
  (render))

(defn run
  []
  (render))                         ;; mount the application's ui into '<div id="app" />'


