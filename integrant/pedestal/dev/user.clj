(ns user
  (:require [integrant.repl :as ig-repl]
            [server.system :as system]
            [portal.api :as portal]))

(ig-repl/set-prep! (fn [] (assoc-in system/config  [:app/config :polite?] true)))

(def go ig-repl/go)
(def halt ig-repl/halt)
(def reset ig-repl/reset)
(def reset-all ig-repl/reset-all)

(comment

  ;; see https://github.com/weavejester/integrant-repl
  (go)
  
  (halt)
  (reset)
  (reset-all)
  ;;
  )


(comment
  (def p (portal/open {:launcher :vs-code}))
  (add-tap #'portal/submit)
   ; Start tapping out values
  (tap> :hello)
  (tap> system/config)
  ;;
  )