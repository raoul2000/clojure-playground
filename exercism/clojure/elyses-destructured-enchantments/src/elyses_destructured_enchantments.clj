(ns elyses-destructured-enchantments)

(defn first-card
  "Returns the first card from deck."
  [[first _]]
  first)


(defn second-card
  "Returns the second card from deck."
  [[_ second _]]
  second)

(defn swap-top-two-cards
  "Returns the deck with first two items reversed."
  [[first second & remaining]]
  (conj remaining first second))

(defn discard-top-card
  "Returns a vector containing the first card and
   a vector of the remaining cards in the deck."
  [[first & remaining]]
  [first remaining])

(def face-cards
  ["jack" "queen" "king"])

(defn insert-face-cards
  "Returns the deck with face cards between its head and tail."
  [[first & remaining]]
  (remove nil? (flatten [first face-cards  remaining])))
