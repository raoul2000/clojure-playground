# vector
- create a vector from elements **vector**
```clojure
(vector a b c)
=> [a b c]
```
- get element from vector **get**
```clojure
(get [ \a \b \c] 0)
=> \a
```


# map
- create a map from elements **hash-map**
```clojure
(hash-map :name "bob" :age 12)
=> {:age 12, :name "bob"}
```
- get value from key **get**
```clojure
; same as (get  {:age 12 :name "bob"} :name)
; same as (:name {:age 12 :name "bob"})
({:age 12 :name "bob"} :name)
=> "bob"
({:age 12 :name "bob"} :city)
=> nil
```
- get value from nested map **get-in**
```clojure
(get-in {:age 12 :name "bob" :city {:code 88798}} [:city :code])
=> 88798
```