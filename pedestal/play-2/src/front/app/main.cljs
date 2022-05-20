(ns app.main
  (:require [goog.dom :as gdom]))

(defn init []
  (js/console.log "hello World !!"))

(comment
  (gdom/getDocument)
  (gdom/getHTMLElement "root")

  (let [p-element (gdom/createElement "p")]
    (gdom/setTextContent p-element "hello world")
    (gdom/appendChild (gdom/getHTMLElement "root") p-element))  
  )

(defn my-init
  "executed only the first time the module loads
   and NOT on *hot reload*"
  []
  (js/console.log "my-init ...")
  (js/fetch "http://localhost:8890/greet"))

(defn after-reload
  "executed after each *hot reload*"
  []

  (js/console.log "after-reload")
  (js/fetch "http://localhost:8890/greet"))

