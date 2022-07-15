(ns bank-account)


(defn open-account [] (atom  0))
(defn close-account [account] (reset! account nil))
(def get-balance deref )
(defn update-balance [account amount]  (swap! account (partial + amount)))

