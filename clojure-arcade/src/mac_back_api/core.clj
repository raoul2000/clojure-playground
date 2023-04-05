(ns mac-back-api.core
  (:require
   [mac-back-api.server :refer [mac-incoming mac-outgoing run-server stop-server]]
   [clojure.core.async :refer [>! go chan go-loop alts!!]]))

(def loop-stopper (chan))

(defn move [direction]
  (go (>! mac-outgoing {:action :step :direction direction})))


;; Level 0 ---------------------------------------------------

(defn level-0 [data]
  (when (drop-while #(not (= "." %)) (first (:current data)))
    (move :right)))

;; level 1 ----------------------------------------------------

(defn at-position [x y matrix]
  (-> matrix
      (nth y)
      (nth x)))

(defn pacman-position [matrix]
  (let [index (->> matrix
                   flatten
                   (keep-indexed #(when (= %2 "P") %1))
                   first)
        col-count (count (first matrix))]
    [(mod index col-count) (quot index col-count)]))

(defn at [[x y] matrix direction]
  (let [dy (case direction
             :up   (dec y)
             :down (inc y)
             y)
        dx (case direction
             :left  (dec x)
             :right (inc x)
             x)]
    (at-position dx dy matrix)))

(defn possible-steps [matrix]
  (let [current-pos (pacman-position matrix)
        item-at     (partial at current-pos matrix)
        result (filter #(= "." (item-at %)) [:up :down :left :right])]
    (print result)
    result))


(defn level-1 [data]
  (->> data
       :current
       possible-steps
       first
       move))

;; Game Engine -----------------------------------------------------------

;; "P"  = pacman
;; "."  = food
;; " "  = nothing (food eaten)
;; "GB" = ghost Blue (can be eaten)
;; "GI" = ghost invicible


(defn process-incoming []
  (go-loop []
    (let [[data the-chan] (alts!! [mac-incoming loop-stopper])]
      (if (= loop-stopper the-chan)
        (println "Process incoming go loop terminated")
        (do
          (clojure.pprint/pprint data)

          #_(level-1 data)

          (recur))))))
(comment
(def pos1 {:previous nil,
           :current
           [["■" "■" "■" "■" "■" "■" "■"]
            ["■" " " "." "." "." "." "■"]
            ["■" "P" "■" "■" "■" "." "■"]
            ["■" "." "." "." "." "." "■"]
            ["■" "■" "■" "■" "■" "■" "■"]]})
  
  (level-1 pos1)
  (->> pos1
       :current
       possible-steps
       ;;first
       )
  
  (def pos2 )

  ;;
  )

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
  (go (>! mac-outgoing {:action :step :direction :down}))
  (move :down)

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