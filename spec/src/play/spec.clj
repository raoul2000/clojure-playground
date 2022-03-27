(ns play.spec
  (:require [clojure.spec.alpha :as s]
            [clojure.string :refer [blank?]]
            [clojure.spec.gen.alpha :as gen])
  (:gen-class))

(defn greet
  "Callable entry point to the application."
  [data]
  (println (str "Hello, " (or (:name data) "World") "!")))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (greet {:name (first args)}))

;; ################################################################################
;;
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
                     :distinct true) '(1 2 3))

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
(defn >5? [n] (> n 5))
(s/valid? (s/and even? >5?) 6)  ;; true
(s/valid? (s/and even? >5?) 11) ;; false

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

;; let's see how to define spec for ... maps !! ---------------------------
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

;; s/conform on a valid data will returns a Map
(s/conform :color/object serialized-map-entries) ;; {:name "blue", :note :nice}

;; Sequences ---------------------------------------------------------------
;;
;; specs for sequences are built using so called regular expression operators
;; first is s/cat
;; it accepts tagged items

(s/valid? (s/cat :age int? :name string?) [12 "bob"]) ;; true

;; tags are used to identify what was matched
(s/explain (s/cat :age int? :name string?) [12 33]) ;; 33 - failed: string? in: [1] at: [:name]

;; ... but also with s/conform, that returns a map where tags are keys
(s/conform (s/cat :color #{"green" "red" "blue"} :quantity pos-int?)
           '("red" 12)) ;; {:color "red", :quantity 12}

;; next operator is s/* which mean zero or n
(s/valid? (s/* int?) '(1 -1 5)) ;; true
(s/valid? (s/* int?) '(1 -1 "five")) ;; false

;; s/+ means 1 or n and s/? means 0 or 1
;; combining with s/cat
;; a seq of 0 or n ibtegers followed by 0 or 1 boolean
(s/conform (s/cat :points     (s/* pos-int?)
                  :registered (s/? boolean?))
           [1 2 3 true]) ;; {:points [1 2 3], :registered true}

(s/conform (s/+ (s/cat :name string?
                       :age  (s/and pos-int? #(> % 5))))
           ["bob" 12 "bill" 55]) ;; [{:name "bob", :age 12} {:name "bill", :age 55}]

;; another operator is s/alt to describe an alternative. 
;; Each alternative must be tagged

(s/conform (s/* (s/alt :fruit #{:banana :orange :apple} :quantity pos-int?))
           [:banana :orange 12 45]) ;; [[:fruit :banana] [:fruit :orange] [:quantity 12] [:quantity 45]]

;; it is possible to mix an operator with a constraint using s/&
;; for example, we want a sequence of more than 2 strings
(s/valid? (s/& (s/+ string?)      ;; a seq of 0 ro n strings
               #(> (count %) 2)   ;; seq must contain more than 2 items
               )
          ["a" "b" "s"]) ;; true

(s/valid? (s/& (s/+ string?)
               #(> (count %) 2))
          ["a"]) ;; false

;; Tips : nested spec 
;; enclose into a new spec context with s/spec
;; for example, let's spec a sequence of sequences of strings
(s/conform (s/* (s/cat :colors (s/spec (s/coll-of string?))))
           [["blue" "green" "red"] ["yello"]]) ;; [{:colors ["blue" "green" "red"]} {:colors ["yello"]}]

(s/explain (s/* (s/cat :colors (s/spec (s/coll-of string?))))
           [["blue" "green" 1]]) ;; 1 - failed: string? in: [0 2] at: [:colors]

;; spec function ---------------------------------------
;; you can write a spec for a function
;; see https://clojure.org/guides/spec#_specing_functions

;; let's consider a function
(defn add-and-multiply [a b]
  (+ a b))

;; ... and create a spec for that function. Spec name is the same as function name
(s/fdef add-and-multiply
  :args (s/cat :first number? :second number?)
  :ret number?)

;; now that the function is speced, we can use s/exercise to test it with random generated
;; values. This requires [clojure.spec.gen.alpha :as gen]
(s/exercise-fn 'add-and-multiply)


;; validation ------------------------------------------
;; to validate a spec we can use s/valid? but that's nbot all. We can also
;; use spec to validate a function contracts : input args and returned value

;; use the :pre and :post conditions already built in defn
(s/def ::game-name (s/and string? (comp not blank?)))

(defn game-name-to-upper [name]
  {:pre [(s/valid? ::game-name name)]
   :post [(s/valid? string? %)]}
  name)

(game-name-to-upper 45) ;; Assert failed: (s/valid? :play.spec/game-name name)
(game-name-to-upper "bob") ;; bob

;; inside the function it is also possible to call s/assert to test a data is
;; valid for a spec. Note that by default assertion check is OFF 

;; Another option is to use s/conform inside the function and use the returned value
;; which can be the conformed value if the data is valid, or a ::s/invalid value
;; describing the error

;; For example: spec a list of paramName, paramValue 
(s/def ::config (s/*
                 (s/cat :prop string?
                        :val  (s/alt :s string? :b boolean?))))

(s/valid? ::config [])
(s/conform ::config ["host" "127.0.0.1" "username" "bob" "secure" false])
;; => [{:prop "host", :val [:s "127.0.0.1"]} {:prop "username", :val [:s "bob"]} {:prop "secure", :val [:b false]}]

(defn set-config [conf]
  (let [param (s/conform ::config conf)] ;; like parsing the conf
    (if (s/invalid? param)
      ;; print error message with validation failure description
      ;; we ciould also throw
      ;; (throw (ex-info "Invalid input" (s/explain-data :ex/config input)))
      (printf "invalid params : %s" (s/explain-str ::config param))

      ;; use the parsed data 
      (doseq [entry param]
        (print (:prop entry) (:val entry))))))

(s/conform ::config ["server" "localhost"])
(set-config ["server" "localhost"])

