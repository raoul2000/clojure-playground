(ns play-zip
  (:require [clojure.zip :as z]
            [clojure.xml :as xml]))

;; playing with clojure zipper
;; tree structure
;;    .
;;  / | \
;; 1  2  .
;;     / | \
;;    3  4  .
;;          |
;;          5

(def data-1 [1
             2
             [3
              4
              [5]]])

;; create the zipper for this tree data structure based on 
;; nested vectors
(def my-zipper (z/vector-zip data-1))

;; starting from the root (location = root)
;; get the node
(-> my-zipper
    (z/node))

;; going down from the root and return the node
;;
;;    .
;;  / | \
;; 1* 2  .
;;     / | \
;;    3  4  .
;;          |
;;          5

(-> my-zipper
    (z/down)
    (z/node))
;; => 1

;; going right
;;
;;    .
;;  / | \
;; 1  2* .
;;     / | \
;;    3  4  .
;;          |
;;          5

(-> my-zipper
    (z/down)
    (z/right)
    (z/node))
;; => 2

;; going right again
;;
;;    .
;;  / | \
;; 1  2  .*
;;     / | \
;;    3  4  .
;;          |
;;          5

(-> my-zipper
    (z/down)
    (z/right)
    (z/right)
    (z/node))
;; => [3 4 [5]]

;; throws when forcing down on a node with no children 
(-> my-zipper
    (z/down)
    (z/down)
    (z/node))
;; Execution error (NullPointerException) at play-zip/eval7775 (REPL:80) .

;; by using z/next, depth-first navigate
(-> my-zipper
    z/next
    z/next
    z/next
    z/next
    z/node)

;; use (z/end? zipper) to test when z/next has finished
;; depth first navigation 
(loop [z (z/vector-zip [1 2])]
  (if (z/end? z)
    (do
      (println "end reached !")
      (println (z/node (z/next z)))
      (z/node (z/next z))
      (z/node (z/next z)))
    (do
      (println (z/node z))
      (recur (z/next z)))))
;; [1 2] -> the root node (and its 2 children)
;; 1     -> child 1
;; 2     -> child 2
;; end reached !
;; [1 2] -> once end is reached calling next once more, returns the root
;; [1 2]    and z/next more, don't change location : returns root always

;; let try to create a zipper from XML data

(def xml-str
  "<products version=\"1\">
      data before
      <price discount=\"false\">
         12
      </price>
      data after
      <empty/>
   </products>")

;; this is what the parse XML looks like. It can be used to create a zipper 
;; via the function z/xml-zip
(def parse-xml {:tag     :products,
                :attrs   {:version "1"},
                :content ["\n      data before\n      "
                          {:tag     :price,
                           :attrs   {:discount "false"},
                           :content ["\n         12\n      "]}
                          "\n      data after\n   "
                          {:tag     :empty,
                           :attrs nil,
                           :content nil}]})

;; create the zipper
(def xml-zip (z/xml-zip (xml/parse (java.io.ByteArrayInputStream. (.getBytes xml-str)))))

;; we can now use the zipper API to work on this data structure
;; read root node attributes 'version'
(:version (:attrs (z/node xml-zip)))
;; => "1"

;; read first child of the root node : a text node
(-> xml-zip
    z/down
    z/node)

;; => "\n      data before\n      "
(-> xml-zip
    z/down
    z/node)

;; lets find names of all element with an attribute
(loop [z xml-zip
       result []]
  (if (z/end? z)
    result
    (recur (z/next z) (let [node (z/node z)]
                        (if (and (map? node)
                                 (:attrs node))
                          (conj result (:tag node))
                          result)))))

;; path based navigation
;; Given a path as a ordered list of tag names, move location
;; to the targeted node
;; example : "/folder-1/folder-1-1/file-1-1-1.xml" 

(def xml-str-2
  "<d name=\"/\">
      <f name=\"file1.txt\">this is the file content</f>
      <f name=\"file2.txt\">this is the file content</f>
      <d name=\"folder-1\">
         <f name=\"file11.xml\"><![CDATA[<root>data</root>]]></f>   
         <d name=\"folder-1-1\">
             <f name=\"file-1-1-1.xml\">hello</f>   
         </d>
      </d>
   </d>")

(def xml-zip2 (z/xml-zip (xml/parse (java.io.ByteArrayInputStream. (.getBytes xml-str-2)))))


(->> xml-zip2
     z/children
     (filter #(and (= :d (:tag %))
                   (= "folder-1" (:name (:attrs %)))))
     first
     )



