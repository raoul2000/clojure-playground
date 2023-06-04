(ns dev.xpath
  (:require [clj-xpath.core :as xp]))

;; this module is for testing XPath query 

(comment
  ;; eval a simple xpath
  (def dom1 (xp/xml->doc "<a><b>1</b></a>"))

  ;; select eh root node, and take element name
  (= "a" (-> (xp/$x "/*" dom1)
             first
             :tag
             name))
  ;; ...shorter
  (= :a (xp/$x:tag "/*" dom1))

  (xp/$x "/a/b/text()" dom1)

  ;; select ALL nodes
  (= 2 (count (xp/$x "//*" dom1)))

  ;; eval and get the text node
  (xp/$x:text "/a/b" dom1)

  (def dom2 "<root>
             <item id=\"1\">
                <child>c1</child>
                <child>c2</child>
             </item>
             <item id=\"2\">
                
                <child>c2</child>
             </item>
             </root>")

  ;; select all items's id having child c2
  (xp/$x "//child[text()='c2']/ancestor::item/@id" dom2)
  (xp/$x "//child[text()='c2']/ancestor::item/@id | /root" dom2)

  ;; and what about relative xPath evaluation
  (->> (xp/$x "//child[text()='c2']" dom2)
       (map #(xp/$x "ancestor::item/@id" %))) ;; good !

  ;;
  )

