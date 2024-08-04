(ns yaml.core
  (:require [clj-yaml.core :as yaml]
            [clojure.string :as s])
  (:gen-class))

(defn greet
  "Callable entry point to the application."
  [data]
  (println (str "Hello, " (or (:name data) "World") "!")))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (greet {:name (first args)}))

(comment


  (yaml/parse-string "
- name : bob
  age : 12
  address : 
    street : Bouron Street")

  (yaml/parse-string "
- name : bob
  age : 12
  address : 
    street : Bouron Street
  friends : 
    - name : bill
    - name : Tom
    - name : John")

  ;; multi line string : use |-
  ;; see https://yaml-multiline.info/
  
  (yaml/parse-string (yaml/generate-string {:name "bob\nMarley"} :dumper-options {:flow-style :block}))
  (def f ["name: |-"
          "  bob"
          "  is"
          "  cool"])
  (yaml/parse-string (s/join "\n" f))

  (yaml/parse-string "example: >-\n  string\n  other \"quotes string\"\n\n")
  (yaml/parse-string "example: >\n  string\n  other \"quotes string\"\n\n")
  (yaml/parse-string "example: >+\n  string\n  other \"quotes string\"\n\n")
  ;;
  )


(comment

  (yaml/generate-string {:name "bob"} :dumper-options {:flow-style :block})
  (yaml/generate-string {:name "bob\nMarley"} :dumper-options {:flow-style :block})

  (yaml/generate-string {:name "bob"
                         :address {:street "Bourbon streer"}}
                        :dumper-options {:flow-style :block})

  (yaml/generate-string {:name "bob"
                         :colors ["green" "blue" "red"]}
                        :dumper-options {:flow-style :block})

  (print (yaml/generate-string {:name "bob"
                                :friends [{:name "bill"
                                           :age 35}
                                          {:name "John"
                                           :age 38}
                                          {:name "Tom"
                                           :age 20
                                           :color ["blue" 1 true]
                                           :address {:street "bourbon"
                                                     :zip 34000}}]
                                :code "import lib;\nprintf \"hello %s\" name;\n  if true\n    echo no#end\n"}
                               :dumper-options {:flow-style :block}))



  ;;
  )
