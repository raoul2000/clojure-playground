<h1>Clojure Cheatsheet</h1>

- [Data Structure](#data-structure)
  - [vector](#vector)
  - [map](#map)
  - [list](#list)
  - [set](#set)
- [Sequence](#sequence)
  - [first, rest](#first-rest)
  - [take, drop](#take-drop)
  - [cons](#cons)
- [Collections](#collections)
  - [into](#into)
  - [conj](#conj)
- [Function Functions](#function-functions)
  - [apply](#apply)

## Data Structure

### vector
- create a vector from elements with **vector**
```clojure
(vector a b c)
=> [a b c]
```
- get element from vector with **get**
```clojure
(get [ \a \b \c] 0)
=> \a
(get [ \a \b \c] 5)
=> nil
```

### map
- create a map from elements with **hash-map**
```clojure
(hash-map :name "bob" :age 12)
=> {:age 12, :name "bob"}
```
- get value from key with **get** (and via *keyword*)
```clojure
(get  {:age 12 :name "bob"} :name)
=> "bob"
; using keywords
(:name {:age 12 :name "bob"})
=> "bob"
({:age 12 :name "bob"} :name)
=> "bob"
({:age 12 :name "bob"} :city)
=> nil
```
- get value from nested map with **get-in**
```clojure
(get-in {:age 12 :name "bob" :city {:code 88798}} [:city :code])
=> 88798
```

### list
- create a list from elements with **list**
```clojure
(list 1 "a" 2/3)
=> (list 1 "a" 2/3)
```
- get value from list with **nth**
```clojure
(nth '(1 2 3) 0)
=> 1
(nth '(1 2 3) 5 "not found")
=> "not found"
```

### set
- create set from elements with **hash-set**
```clojure
(hash-set 1 2 3)
=> #{1 3 2}
(hash-set 1 2 3 1 2 3)
=> #{1 3 2}
```
- get value with **get** (and via *keywords*)
```clojure
(get #{10 20 30} 20)
=> 20
(get #{1 2 3} "a")
=> nil
; using keywords
(:name #{:city :name})
=> :name
```
- get value presence with **contains?**
```clojure
(contains? #{10 20 30} 20)
=> true
(contains? #{1 2 3} "a")
=> false
```

## Sequence
> sequence (*seq*) is an abstraction over data structures. Whenever Clojure expects a sequence—for example, when you call map, first, rest, or cons—it calls the seq function on the data structure in question to obtain a data structure that allows for first, rest, and cons
>  [ref](https://www.braveclojure.com/core-functions-in-depth/#Abstraction_Through_Indirection)


- create from data structure with **seq**
```clojure
; from a vector
(seq [1 2 "b"])
=> (1 2 "b")
; from a list
(seq '(\a \b \c))
=> (\a \b \c)
; from a set
(seq #{"a" "b" "c"})
=> ("a" "b" "c")
; from a map
(seq {:fruit "apple" :price 2})
=> ([:fruit "apple"] [:price 2])
```
- create data structure from seq with **into** (preview)
```clojure
(into [] (seq '(3 4 5)))
=> [3 4 5]
(into {} (seq {:planet "mars" :color "red"} ))
=> {:planet "mars", :color "red"}
```
### first, rest
- access : **first** and **rest**
```clojure
; data structure is first converted to a seq
(first  [ 1 2 3 ])
=> 1
(first {:fruit "apple" :price 2})
=> [:fruit "apple"]
(rest [\x \y \z])
=> (\y \z)
```
### take, drop
- access with **take** 
```clojure
(take 3 [ 1 2 3 4])
=> (1 2 3)
; can't take more than what's there
(take 3 [ 1 2])
=> (1 2)
; rmember a map is converted into a seq of key/value pairs
(take 3 {:a 1 :b 2 :c 3 :d 4})
=> ([:a 1] [:b 2] [:c 3])
```
- access with **drop**
```clojure
; drop is the complement of take
(drop 3 {:a 1 :b 2 :c 3 :d 4})
=> ([:d 4])
; drop too much !
(drop 4 [1 2 3])
=> ()
```
### cons
- add element to begining of sequence **cons**
```clojure
; same as explicit call of *seq* (cons 1 (seq [1 2]) )
(cons 1  [1 2])
=> (1 1 2)
```

## Collections
> The collection abstraction is closely related to the sequence abstraction

The sequence abstraction is about operating on members individually, whereas the collection abstraction is about the data structure as a whole. For example, the collection functions count, empty?, and every? aren’t about any individual element; they’re about the whole:

### into
- copy a collection **into** another
```clojure
(into ["first"] [1 2])
=> ["first" 1 2]
; duplicates are removed
(into #{} [1 2 2 2 ])
=> #{1 2}
(into {:first "a"} [[:a 1]])
=> {:first "a", :a 1}
(into {:first "a"} [[:a 1 :first "b"]])
; Execution error (IllegalArgumentException) at clock/eval7391 (form-init4833341512224151523.clj:163).
; Vector arg to map conj must be a pair

; key/value is updated
(into {:first "a"} [[:a 1]  [:first "updated"]])
{:first "updated", :a 1}
```

### conj
- add element to end of collection **conj**
```clojure
(conj [1 2] 3)
=> [1 2 3]
(conj #{\a \b} \c \d)
=> #{\a \b \c \d}
; for map, provide a key/value pair
(conj {:a 1} [:b 2])
=> {:a 1, :b 2}
; existing key is updated
(conj {:a 1} [:b 2] [:c "color"] [:a "A"])
=>{:a "A", :b 2, :c "color"}
```

## Function Functions
### apply
- explode args
```clojure
; same as (max 12 2 44 5)
(apply max [12 2 33 4])
=> 33
; without apply we should have written (conj [] 1 2)
(apply conj [] [1 2])
=> [1 2]
```