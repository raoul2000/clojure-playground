(ns beer-song)

(defn verse
  "Returns the nth verse of the song."
  [num]
  (cond
    (= num 0) (str "No more bottles of beer on the wall, no more bottles of beer.\nGo to the store and buy some more, 99 bottles of beer on the wall.\n")
    (= num 1) (str "1 bottle of beer on the wall, 1 bottle of beer.\nTake it down and pass it around, no more bottles of beer on the wall.\n")
    :else  (str num " bottles of beer on the wall, " num " bottles of beer.\nTake one down and pass it around, "
                (if (= 1 (dec num))
                  (str "1 bottle")
                  (str (dec num) " bottles"))
                " of beer on the wall.\n")))

(defn has-remaining-verse 
  [start end]
   (>= start end))

(defn last-verse 
  [start end]
  (= start end))

(defn sing
  "Given a start and an optional end, returns all verses in this interval. If
  end is not given, the whole song from start is sung."
  ([start]
   (sing start 0))
  ([start end]
   (if (has-remaining-verse start end)
     (str
      (verse start)
      (if (not (last-verse start end )) "\n" "")
      (sing (dec start) end)
      ))))

