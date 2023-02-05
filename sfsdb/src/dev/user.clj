(ns dev.user
  (:require
   [portal.api :as p]
   [ring.adapter.jetty :refer [run-jetty]]))

;;(def portal (p/open))
(def p (p/open {:launcher :vs-code}))

(add-tap #'p/submit)

(tap> :hello) ; Start tapping out values