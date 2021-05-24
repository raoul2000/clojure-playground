(ns task-runner.async-play
  (:require [clojure.core.async
             :as async
             :refer [>! <! >!! <!! go chan buffer close! thread
                     alts! alts!! timeout]]
            [clojure.string :as string]))

;; create a channel
(def echo-chan (chan))

;; take from this channel.
;; Taking from a channel is blocking but as it is executed in a go block
;; it runs on a separated process
(go (println (<! echo-chan)))

;; put "ketchup" into this channel
(>!! echo-chan "ketchup")

;; buffer ---------------------------------
;; create a channel with 2 places buffer
(def buf-chan (chan 2))
;; put 2 strings in the channel
(>!! buf-chan "Hello")
(>!! buf-chan "world")
;; If we would put a third thing in the channel, the main thread would be
;; blocked as there is still no process taking from this channel and this
;; channel is full

;; take from this channel
(go (println (<! buf-chan)))

;; create a channel --------------------------
(def hi-chan (chan))

;; in another process, put 10 strings into this channel. As channel is not buffered
;; this process will be parked after the first put, until it is taken
(doseq [n (range 10)]
  (go
    (>! hi-chan n)
    (println "put " n)))

;; in another process, take 10 things from this channel. Again, as channel is
;; not buffered, process is parked until someting is put
(doseq [n (range 10)]
  (go
    (println "take " (<! hi-chan))))

;; chaining -------------------------------
;; out channel of one process is taken and put to in channel of next process

(let [c1 (chan)
      c2 (chan)
      c3 (chan)]
  (go (>! c2 (string/upper-case (<! c1))))
  (go (>! c3 (string/reverse (<! c2))))
  (go (println (<! c3)))
  (>!! c1 "redrum"))

;; alts!! -----------------------------------
;; alts!! take from the first available channel (ie the channel with something 
;; to take)

;; after random sleep put tone to channel
(defn ring-bell
  [note c]
  (go
    (Thread/sleep (+ 1000 (rand-int 2000)))
    (>! c note)))

(let [c1 (chan)
      c2 (chan)
      c3 (chan)]
  (ring-bell "A" c1)
  (ring-bell "B" c2)
  (ring-bell "C" c3)
  ;; alts!! among those 3 channels plus a timeout channel that will put nil
  ;; after a given amount of time (in ms)
  (println "tone = " (alts!! [c1 c2 c3 (timeout 500)])))

;; alts!! can also be used to put to channel
;; (alts!! [c1 c2 [c3 "thing to put"]])
