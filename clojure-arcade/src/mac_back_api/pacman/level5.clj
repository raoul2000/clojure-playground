(ns mac-back-api.pacman.level5
  (:require [mac-back-api.pacman.game-engine :as game]))


(defn level-0 [data]
  (clojure.pprint/pprint data)
  (game/move :right))

(comment
  (game/start-engine)
  (game/start-loop level-0)
  (game/start-action)

  (game/stop-loop)
  ;; eval level if needed
  (game/start-loop level-0)
  (game/restart-action)


  (game/stop-engine)
  ;;
  )


