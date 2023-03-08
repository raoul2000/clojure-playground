(ns play-xml
  (:require [clojure.xml :as x]))

(x/parse (java.io.ByteArrayInputStream. (.getBytes "<root>data</root>")))

(comment
  ;; parse returns a map that represents the XML document
  {:tag :root,
   :attrs nil,
   :content ["data"]}
  ;;
  )

(x/parse (java.io.ByteArrayInputStream. (.getBytes "<products>data<price discount=\"false\">12</price></products>")))

(comment
  ;; note that element names are turned into keywoards
  {:tag :products,
   :attrs nil,
   :content ["data"
             {:tag :price,
              :attrs {:discount "false"},
              :content ["12"]}]})

(x/parse (java.io.ByteArrayInputStream. (.getBytes "<products>before<![CDATA[<some middle>]]>after</products>")))
(comment
  ;; with CDATA as text content
  {:tag :products, 
   :attrs nil, 
   :content ["before<some middle>after"]}  
  ;;
  )