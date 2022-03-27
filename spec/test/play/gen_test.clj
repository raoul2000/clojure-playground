(ns play.gen-test
  (:require [clojure.spec.gen.alpha :as gen]
            [clojure.spec.alpha :as s]))


(gen/generate (s/gen int?))