(ns mac-back-api.pacman.game-engine
  (:require
   [mac-back-api.server :refer [mac-incoming mac-outgoing run-server stop-server]]
   [clojure.core.async :refer [>! go chan go-loop alts!!]]))

(def loop-stopper (chan))

(defn move [direction]
  (go (>! mac-outgoing {:action :step :direction direction})))

(defn process-incoming [level-play]
  (println "processing incomming ...")
  (go-loop []
    (let [[data the-chan] (alts!! [mac-incoming loop-stopper])]
      (if (= loop-stopper the-chan)
        (println "Process incoming go loop terminated")
        (do
          (println "playing move")
          (level-play data)
          (recur))))))

(def start-engine run-server)
(def stop-engine  stop-server)

(defn start-action []
  (go (>! mac-outgoing {:action :start})))

(defn restart-action []
  (go (>! mac-outgoing {:action :restart})))

(defn start-loop [level-play]
  (process-incoming level-play))

(defn stop-loop []
  (go (>! loop-stopper "Exterminate!!")))

