(ns dev.xml
  (:require [clojure.xml :as x]))

;; explore XML and zipper

(comment
  ;; parse a string
  (x/parse (java.io.ByteArrayInputStream. (.getBytes "<root>
                                                      <node1 attrColor=\"green\">text content</node1>
                                                      <node2><![CDATA[<hello & bye>]]></node2>
                                                      </root>")))

  ;; with xml/emit-element, create a document ? .. not really. The CDATA value is emitted as is  
  ;; it will invalidate XML
  (x/emit-element {:tag :root,
                   :attrs nil,
                   :content
                   [{:tag :node1, :attrs {:attrColor "green"}, :content ["text content"]}
                    {:tag :node2, :attrs nil, :content ["<hello & bye>"]}]})

  ;; let's try with s/emit
  ;; same result, the node2 content is not protected
  ;; this may be solved by https://github.com/weissjeffm/clojure.prxml/blob/master/clojure/prxml.clj
  (x/emit {:tag :root,
           :attrs nil,
           :content
           [{:tag :node1, :attrs {:attrColor "green"}, :content ["text content"]}
            {:tag :node2, :attrs nil, :content ["<hello & bye>"]}]})

  ;; probably use clojure.data.xml lib
  
  ;;
  )