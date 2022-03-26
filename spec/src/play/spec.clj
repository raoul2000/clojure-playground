(ns play.spec
  (:require [clojure.spec.alpha :as s]
            [clojure.string :refer [blank?]])
  (:gen-class))

(defn greet
  "Callable entry point to the application."
  [data]
  (println (str "Hello, " (or (:name data) "World") "!")))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (greet {:name (first args)}))


;; let's start playing
;; s/valid? returns the same value as the predicate

(s/valid? even? 1000)
(s/valid? even? 1001)
(s/valid? #(> % 4) 5) ;; true
(s/valid? #{:blue :green} :blue) ;; true
(s/valid? #{:blue :green} :red)  ;; false

;; Tips : to include nil as a valid value, use s/nilable
(s/valid? (s/nilable string?) nil) ;;

;; How to deal with Collections ? use s/coll-of --------------

(s/valid? (s/coll-of int?) [1 2 3])      ;; true
(s/valid? (s/coll-of int?) [1 2 :three]) ;; false

;; s/coll-of accept some options
(s/valid? (s/coll-of pos-int?
                     :kind list?
                     :min-count 1
                     :max-count 10
                     :distinct true)'(1 2 3))

;; use :into to ask s/conform to convert the collection type
;; below, a vector is coerced to a set
(s/conform (s/coll-of pos-int?
                      :kind vector?
                      :into #{}) [2 4 6]) ;; #{4 6 2}

;; For tuple use s/tuple
(s/valid? (s/tuple pos-int? string? keyword?) 
          [1 "blue" :apple]) ;; true
(s/valid? (s/tuple pos-int? string? keyword?) 
          [1 2 3]) ;; false

;; for homegenous map where all keys have same type and all 
;; values have same type, use s/map-of
(s/valid? (s/map-of keyword? pos-int?)
          {:a 1, :b 2}) ;; true

(s/valid? (s/map-of keyword? pos-int?)
          {:a true, :b 2}) ;; false

;; compose predicates ----------------------------------------
;; with s/and
(s/valid? (s/and even? #(> % 5)) 6)  ;; true
(s/valid? (s/and even? #(> % 5)) 11) ;; false

;; with s/or
;; each alternative must be identified by a key which is useful
;; to identify an invalid spec explanation
(def even-or>10 (s/or :is-even         even?
                      :greater-than-10 #(> % 10)))
(s/valid? even-or>10 11) ;; true
(s/valid? even-or>10 2)  ;; true
(s/valid? even-or>10 3)  ;; false

;; use s/conform to get a value conformed to a given spec -------------------
;; or 'invalid' when the value is not valid

(s/conform even? 3) ;; :clojure.spec.alpha/invalid
(s/conform even? 2) ;; 2

;; spec registry --------------------------------------------------------
;; instead of using spec directly like we did before, they can be
;; stored in a central registry and be use anywhere
;; a spec is registered via s/def

(s/def :person/age  pos-int?)
(s/def :person/name  (s/and string? (comp not blank?)))
(s/def :person/status #{:single :married})

(s/valid? :person/age 12)         ;; true
(s/valid? :person/name "Bob")     ;; true
(s/valid? :person/name "")        ;; false
(s/valid? :person/status :single) ;; true
(s/valid? :person/status :widow)  ;; false

;; of course we can compose those registered specs
(s/def :person/name-or-age (s/or :name :person/name
                                 :age  :person/age))
(s/valid? :person/name-or-age 12) ;; true

;; use s/explain to get a message describing why a value
;; is not valid given a spec
(s/explain :person/age -1)

;; explain me this one !
(s/explain :person/name-or-age :single)

;; to get the explications as string
(s/explain-str :person/age -1)
;; .. or as data
(s/explain-data :person/age -1)

;; let's see how to defin spec for ... maps !! ---------------------------
;; I mean heteregenous!  First define spec for the map keys
(s/def :pilot/name string?)
(s/def :pilot/nickname string?)
(s/def :pilot/age  pos-int?)

;; now assemble keys spec. Some are REQuired, and one it OPTional
(s/def :f1/pilot (s/keys :req [:pilot/name :pilot/age]
                         :opt [:pilot/nickname]))
;; to work with unqualified keys, use
;; - :req-un
;; - :opt-un

;; let's test
(s/explain :f1/pilot [])                    ;; [] - failed: map? spec: :f1/pilot
(s/explain :f1/pilot {:pilot/name "Prost"}) ;; :pilot{:name "Prost"} - failed: (contains? % :pilot/age) spec: :f1/pilot
(s/explain :f1/pilot {:pilot/name "Prost"
                      :pilot/age  "young"}) ;; "young" - failed: pos-int? in: [:pilot/age] at: [:pilot/age] spec: :pilot/age
(s/explain :f1/pilot {:pilot/name "Prost"
                      :pilot/age   55})       ;; Success !!

(s/explain :f1/pilot {:pilot/name     "Prost"
                      :pilot/age      55
                      :pilot/nickname "the Rabit"}) ;; Success !!

;; adding a key that is not in the spec will not cause any issue
(s/explain :f1/pilot {:pilot/name     "Prost"
                      :pilot/age      55
                      :pilot/nickname "the Rabit"
                      :pilot/team     "Renaut"})        ;; Success !!

;; to deal with map entries provided as arrays use s/keys*

(s/def :color/name string?)
(s/def :color/note #{:nice :medium :ugly})
(s/def :color/object (s/keys* :req [:color/name :color/note]))

(def serialized-map-entries [:color/name "blue"
                             :color/note :nice])
(s/explain :color/object serialized-map-entries) ;; Success !

(s/explain :color/object [:color/name "blue"
                          :color/note 10]) ;;10 - failed: #{:ugly :medium :nice} in: [:color/note] at: [:color/note] spec: :color/note

;; Sequences ----------------------------------------


