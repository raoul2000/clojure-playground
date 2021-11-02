(ns interest-is-interesting)


(defn abs [n]
  (max n (- n)))


(defn interest-rate
  "return interest rate for balance "
  [balance]
  (cond
    (neg? balance)       -3.213
    (< balance 1000)     0.5
    (< 999 balance 5000) 1.621
    :else                2.475))

(comment

  (interest-rate -11) ;; Double
  (/ (* -2 5) 5)
  ;;
  )

(defn compute-interest [balance]
  (/ (*  (bigdec (interest-rate balance)) (abs balance)) 100M))

(comment
  (type (*  (interest-rate 1000) (abs 1000)))
  (type (bigdec (*  (interest-rate 1000) (abs 1000))))
  (type (/ (*  (interest-rate 1000) (abs 1000)) 100M))
  (type (/ (bigdec (*  (interest-rate 1000) (abs 1000))) 100M))
  (type (compute-interest 100))
  (compute-interest -100)
  ;;
  )



(defn annual-balance-update-1
  "return balance plus interest for one year.
   Interest can be negative when balance is negative."
  [balance]
  (let [interest          (/ (bigdec (interest-rate balance)) 100M)
        interest-amount  (* (abs balance) interest)]
    (+  balance interest-amount)))

(defn annual-balance-update
  "return BigDec balance plus interest for one year.
   Interest can be negative when balance is negative."
  [balance]
  (bigdec (+ balance (compute-interest balance))))


(defn amount-to-donate
  "return the amount to donate when balance is positive
   and for the given tax free percentage. Otherwise returns 0"
  [balance tax-free-percentage]
  (let [updated-balance (annual-balance-update balance)]
    (if (pos? updated-balance)
      (int (* 2 (* updated-balance  (/ (bigdec tax-free-percentage) 100M))))
      0)))

(comment
  (annual-balance-update  1000.0001M)
  (type (bigdec (/ 0.99 100))
        )
  (amount-to-donate 1000.0001M 0.99)
  
  ;;
  )