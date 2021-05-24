(ns task-runner.future-play)

;; future ----------------------------------
;; future is run in another thread. It does not block the current
;; thread unless the current thread explicitly waits for the future to end.
;; This is done be dereferencing the future, which returns its value

(defn fut1 []
  (let [fut (future (Thread/sleep 2000)
                    (println "done")
                    42)]
    (if (not (realized? fut))
      (println "still not done")
      (println "done"))
    (println @fut)))

;; to deref a future with a timeout and a fallback value use :
;;    (deref (future (Thread/sleep 1000) 0) 10 5)
;;    where 10 is the timeout in ms
;;    and 5 is the value returned on timeout

(defn fut-with-timeout []
  (let [fut (future (Thread/sleep 3000)
                    (println "done after 3s")
                    42)]
    (deref fut 2000 0)))

;; delay -------------------------------------
;; delays's body is garanteed to be executed only once. This happens when
;; it is dereferenced (using 'deref' or '@'). Subsequent calls will return
;; the same value.
;; Use it to perform an operation only once.

(def hello-delay (delay (do
                          (println "delay process starting ...")
                          (Thread/sleep 2000)
                          (println "delay done")
                          42)))

;; promises -------------------------------------
;; Promise are expression of an expected result in the future. 
;; deliver a promise value with 'deliver'
;; getting the value of a promise is done by dereferencing it. It is blocking
;; promise value is only delivered once

(defn promise-1 []
  (let [result-promise (promise)
        fut (future (Thread/sleep 2000)
                    (deliver result-promise "surprise !"))]
    (println "waiting for my surprise")
    (println @result-promise)))

;; create 5 future with random sleep time and deliver sleep time.
;; wait for the promise to be delivered : the first one (with min sleep time) wins!

(defn rand-sleep-duration [cnt]
  (take cnt (repeatedly (partial rand-int 3000))))

(defn promise-2 []
  (let [result-promise (promise)]
    (doseq [ts (rand-sleep-duration 10)]
      (future 
        (println "fut:" ts)
        (Thread/sleep ts) 
        (deliver result-promise ts)))
    (println "waiting for my surprise ...")
    (println "my surprise is : " @result-promise)))