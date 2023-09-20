(ns user
  (:require
   [portal.api :as portal]))


(comment
  (def p (portal/open {:launcher :vs-code}))
  (add-tap #'portal/submit)
   ; Start tapping out values
  (tap> :hello)
  
  ;;
  )