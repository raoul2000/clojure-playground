(ns mac-back-api.core
  (:require
   [mac-back-api.server :refer [mac-incoming mac-outgoing run-server stop-server]]
   [clojure.core.async :refer [>! go chan go-loop alts!!]]))

(def loop-stopper (chan))

(defn move [direction]
  (go (>! mac-outgoing {:action :step :direction direction})))

(defn level-0 [data]
  (when (drop-while #(not (= "." %)) (first (:current data)))
    (move :right)))

(defn process-incoming []
  (go-loop []
    (let [[data the-chan] (alts!! [mac-incoming loop-stopper])]
      (if (= loop-stopper the-chan)
        (println "Process incoming go loop terminated")
        (do
          (clojure.pprint/pprint data)
          
          (level-0 data)

          (println "Deeze Moves")
          (recur))))))
(comment


  (def m  [["■" "■" "■" "■" "■" "■" "■"]
           ["■" "P" "." "." "." "." "■"]
           ["■" "■" "■" "■" "■" "■" "■"]])

  (second m)
  (drop-while #(not (= "." %)) (first m))
  (when (drop-while #(not (= "." %)) (first m))
    (>! mac-outgoing {:action :step :direction :right})))

(comment
  ;; === Step 1
  ;; Start the websocket server
  (run-server)
  (stop-server)
  ;; === Step 2
  ;; In the browser click the WS button in the upper left corner. It will be red initially then green when connected.
  ;; === Step 3 (Optional)
  ;; Although websocket data is sent and received via core async channels. There is no need to add additional async code.
  ;; The `process-incoming` function is provided as a starting place for putting move data and taking game state data from
  ;; the channels.
  (process-incoming)
  ;; === Step 3
  ;; Start the game
  (go (>! mac-outgoing {:action :start}))
  ;; === Step 4
  ;; Move the player
  ;; Valid :direction values are :up :down :left :right
  (go (>! mac-outgoing {:action :step :direction :right}))

  ;; == Other functions
  ;; Iterate and improve how you are processing where to move the player.
  ;; Stop the process-incoming go-loop.
  (go (>! loop-stopper "Exterminate!!"))

  ;; Update your move calculating logic then restart to loop
  (process-incoming)

  ;; Move, laugh, cry, get ended by ghosts and then start all over
  (go (>! mac-outgoing {:action :restart}))
  
  ;;
  )