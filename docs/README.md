<h1>Clojure Cheatsheet</h1>

- [Data Structure](#data-structure)
  - [vector](#vector)
  - [map](#map)
  - [list](#list)
  - [set](#set)
- [Sequence](#sequence)
  - [first, rest](#first-rest)
  - [cons](#cons)
- [Collections](#collections)
  - [take, take-while, take-last, take-nth](#take-take-while-take-last-take-nth)
  - [drop, drop-while, drop-last](#drop-drop-while-drop-last)
  - [into](#into)
  - [conj](#conj)
  - [some](#some)
  - [dedupe](#dedupe)
- [Function Functions](#function-functions)
  - [create](#create)
  - [apply](#apply)

## Data Structure

### vector
create a vector from elements with **vector**

```clojure
(vector a b c)
=> [a b c]
```

create a vector from collection with **vec**

```clojure
(vec '(a b c))
=> [a b c]
```

get element from vector with **get**

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
- create set from collection with **set**
```clojure
(set '(1 2 3))
=> #{1 3 2}
```
- merge 2 sets using **clojure.set/union**
```clojure
(clojure.set/union #{1 2 3} #{3 4 5})
=> #{1 4 3 2 5}
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

### take, take-while, take-last, take-nth
-  **take** n from coll begin
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
- **take-last** n from coll end
```clojure
(take-last 2 [1 2 3])
=> (2 3)
```
- **take-nth** every *nth* item from coll
```clojure
(take-nth 2 [1 2 3 4 5])
=> (1 3 5)
```
- **take-while** predicate is TRUE
```clojure
(take-while even? [2 4 6 7])
=> (2 4 6)
```
### drop, drop-while, drop-last
- **drop** *n* items from coll begin
```clojure
; drop is the complement of take
(drop 3 {:a 1 :b 2 :c 3 :d 4})
=> ([:d 4])

; drop too much !
(drop 4 [1 2 3])
=> ()
```
- **drop-while** pred is TRUE
```clojure
; drop-while is the complement of take-while
(drop-while even? [ 2 4 5])
=> (5)
```
- **drop-last** *nth* items from the coll end
```clojure
; default is n=1 : drop last item
(drop-last [1 2 3])
=> (1 2)

(drop-last 2 [1 2 3])
=> (1)
```


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
=> {:first "updated", :a 1}
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

### some
- explore coll with **some**
```clojure
; (some pred coll)
; stops after first true predicate (here 2)
(some even? '(1 2 3 4))
=> true

(some #(= 5 %) [1 2 3 4 5])
=> true

; would return nil if coll doesn't contain even number
(some #(when (even? %) %) '(1 2 3 4))
=> 2

; returns the first value for the key present in the coll
; and in the map
(some {:a 1 :b 2} '(:c :b))
=> 2

; here we see sets being used as a predicate
; the first member of the collection that appears in the set is returned
(some #{2} (range 0 10))
=> 2
```
### dedupe

## Function Functions
### create
- various ways to create a function
```clojure
; named function
(fn add-five [x] (+ x 5))
; anonymous functions
(fn [x] (+ x 5))
#(+ % 5)
(partial + 5)
```
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