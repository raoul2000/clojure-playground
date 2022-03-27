(ns play.gen
  "playing with generator functions"
  (:require [clojure.spec.alpha :as s]
            [clojure.string :refer [blank?]]
            [clojure.spec.gen.alpha :as gen]
            [clojure.spec.test.alpha :as stest]
            [clojure.pprint :as pp]))

;; generator are able to create sample data compliant with specs
;; these data can be used to perform tests
;;
;; let's define some simple specs 

(s/def :user/name (s/and string? (comp not blank?)))
(s/def :user/age pos-int?)

(s/def :user/model (s/keys :req [:user/name :user/age]))

;; using s/gen we create a generator function for a given spec
(def username-generator (s/gen :user/name))

;; ... and with s/generate, invoke the generator function to get a value
;; that matches the spec

(gen/generate username-generator)  ;; 19H8Y4G5W0FlC (for example)
(gen/generate (s/gen :user/age))   ;; 4 (for example)
(gen/generate (s/gen :user/model)) ;; {:name "08l74", :age 11100} (for example)

;; with s/sample we create several (by default 10) values compliant with the spec

(gen/sample (s/gen :user/name))    ;; ex: ("7" "K" "n" "A" "3XhG2" "wC" "Uu7" "Ns5s3" "C358043T" "WQ2")

;; cool right ?
;; let's try with some other spec to describe a race
(s/def :race/final-grid (s/coll-of pos-int?
                                   :min-count 1
                                   :distinct  true))

(s/def :race/name (s/and string? (comp not blank?)))
(s/def :race/desc string?)
(s/def :race/completed boolean?)

(s/def :race/model (s/keys :req [:race/name
                                 :race/final-grid
                                 :race/completed]
                           :opt [:race/desc]))

(def race-model-generator (s/gen :race/model))

(gen/generate race-model-generator)
(gen/sample race-model-generator)

;; using s/exercise
;; s/exercise is like s/generate except that it produces both a valid random
;; value and also its conformed counter part.

;; for example, let's create a spec for a seq where the first item is a keyword
;; and the rest are 1 or more integers
(s/def ::my-list-of-stuff (s/cat :key keyword? :values (s/+ int?)))

;; let's test 
(s/valid? ::my-list-of-stuff [:color 1 2 3]) ;; true
;; what does conform returns ?
(s/conform ::my-list-of-stuff [:color 1 2 3]) ;; {:key :color, :values [1 2 3]}
;; and now with s/exercise. We just want 2 values
(s/exercise ::my-list-of-stuff 2)
;; (
;;   [(:?/q 0)      {:key :?/q, :values [0]}     ]   
;;   [(:c!/w -1 -1) {:key :c!/w, :values [-1 -1]}]
;; )

;; function -------------------------------------------
;; we can test automatically a function that has spec
(defn add-and-multiply [a b]
  (if (> a b)                  ;; returns a result that breaks the :fn spec (see below)
    0
    (* (+ a b) a b)))

(add-and-multiply 1 3) ;; 12
;; ... and create a spec for that function. Spec name is the same as function name
(s/fdef add-and-multiply
  :args (s/cat :first number? :second number?)         ;; args are 2 numbers
  :ret number?                                         ;; function returns a number
  :fn #(> (:ret %) (+ (get-in % [:args :first])        ;; the returned value should be
                      (get-in % [:args :second]))))    ;; greater that the sum of 2 args

;; Tips : use (clojure.repl/doc add-and-multiply) in the REPL to
;; show function doc and spec

;; with s/exercise, the spec'ed function is invoked 10 times (default) with generated
;; args and the pair args, result is returned.
;; Note that the :ret and :fn specs are not validated, juste generate args and call function
(s/exercise-fn 'play.gen/add-and-multiply)

;; instrument the function to validate the :args spec of the function 
;; and only !! (not the :ret nor :fn)
;; Note that instrumenting a function has a cost in terms of performance (use it
;; in DEV only)
(stest/instrument 'play.gen/add-and-multiply)

(add-and-multiply 1 2) ;; 6
(add-and-multiply 2 true) ;; Execution error ...  true - failed: number? at: [:second]
(add-and-multiply 10 5) ;; 0

;; un-instrument a function
(stest/unstrument 'play.gen/add-and-multiply)
(add-and-multiply 2 true) ;; Execution error (ClassCastException) java.lang.Boolean cannot be cast to java.lang.Number


;; Auto Test ---------------------------------------------
;; with stest/check you generate args, call the function and validatre :ret and :fn
;; this is called 'generative test'
(pp/pprint
 (stest/check 'play.gen/add-and-multiply))

(stest/abbrev-result (first (stest/check 'play.gen/add-and-multiply)))

;; combine instrument and check to a deeper code coverage
;; and also to stub functions
;; This is still to investigate
;; see https://clojure.org/guides/spec#_combining_check_and_instrument



