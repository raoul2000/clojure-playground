(ns play.gen
  "playing with generator functions"
  (:require [clojure.spec.alpha :as s]
            [clojure.string :refer [blank?]]
            [clojure.spec.gen.alpha :as gen]))

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

