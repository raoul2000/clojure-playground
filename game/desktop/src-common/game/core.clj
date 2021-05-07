(ns game.core
  (:require [play-clj.core :as core]
            [play-clj.g2d :as g2d ]))

(g2d/defscreen main-screen
  :on-show
  (fn [screen entities]
    (update! screen :renderer (stage))
    (label "Hello world!" (color :white)))
  
  :on-render
  (fn [screen entities]
    (clear!)
    (render! screen entities)))

(defgame game-game
  :on-create
  (fn [this]
    (set-screen! this main-screen)))
