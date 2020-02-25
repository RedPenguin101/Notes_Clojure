# Function Glossary

## TODO

* for
* fnil (C4 of clojure applied)
* intersection, union and difference for set operations. sorted-set-by
* clojure.string/split, join
* using `(map vector v1 v2)` or `(map hashmap v1 v2)` to zip up vectors into 2tuples or maps (brave and bold vampire exmaple) (maybe `zipmap`? from divine cheese, not sure on distinction)
* lazy set recursion from pegthing
* assoc, conj, cons, assoc-in
* update, update-in
* get, get-in
* into
* if-let
* partial
* first, second, ffirst, last, butlast, rest
* map, reduce deep dives
* juxt
* map-keys, map-vals
* clojure.walk/keywordize-keys (just walk in general)

## Migrate from function diary

* iterate
* vec and vector
* mapv
* mapcat
* \> and <
* partition-by
* destructuring
* Using comp for smart list filtering
* rem vs. mod
* compare

## Destructuring

## Manipulating collections

### Checking if an element is in a collection

Sets are your friend here

```clojure
((apply set my-coll) element)
``` 

will return the element if it's in my-coll, otherwise nil.

```clojure
(some #{element} my-coll)
``` 
will do the same.

To check if a key is in a map, if it's a KW use `(:key map}` or if not `(map "key")`

To check if a value is in a map, just pull out the values.

`(some #{"fish"} (vals {:a "a" :b "b" "key" "fish"}))`

### Checking the frequency of an element in a collection

Given a collection of elements, `frequencies` returns a map with the number of times each element (the map keys) appears (the value).

```clojure
(frequencies [:a :b :a :b :a :d])
;; => {:a 3, :b 2, :d 1}
```

### Flattening a sequence

```clojure
(apply concat nested-vec)
(mapcat identity nested-vec)
```
do basically the same thing: removes ONE level of nesting.

```clojure
(flatten nested-vec)
``` 
is more aggressive, removes ALL nesting.

```clojure
(apply concat [[1 2 3] [:x [:y :z]]])
;; => (1 2 3 :x [:y :z])

(flatten [[1 2 3] [:x [:y :z]]])
;; => (1 2 3 :x :y :z)
```

### Combining maps

`merge` smooshes two maps together. If there's a key-clash, rightmost wins

```clojure
(merge {:a 1 :b 2 :c 3} {:b 4 :d 5})
;; => {:a 1, :b 4, :c 3, :d 5}
```

A typical usecase is for overriding default values in configs, etc.

```clojure
(apply concat [[1 2 3] [:x [:y :z]]])
;; => (1 2 3 :x [:y :z])

(flatten [[1 2 3] [:x [:y :z]]])
;; => (1 2 3 :x :y :z)
```

`merge-with` lets you merge, with a function describing how the values for the same keys get processed.

```clojure
(merge-with into
            {:Lisp ["Common Lisp" "Clojure"]
             :ML ["Caml" "Objective Caml"]}
            {:Lisp ["Scheme"]
             :ML ["Standard ML"]})
;; => {:Lisp ["Common Lisp" "Clojure" "Scheme"], 
;;     :ML ["Caml" "Objective Caml" "Standard ML"]}

```

Example from Clojure Applied Chapter 4:
```clojure
 (​swap!​ inventory #(​merge-with​ ​+​ % @sold-items))
```

Takes an inventory atom, which is a hashmap of stock-types to quantities, and merges in the from the sold-items atom, adding the quantities from that map.

### Removing things from collections

* `(drop n coll)` drops the first n values from a collection
* `(drop-last n coll)` drops the last n values
* `(drop-while pred coll)` iterates through the collection, dropping everythin until it hits something that causes the predicate to fail.
* `(remove pred coll)` is the opposite of filter, it runs through the collection and drops anyhing that meets the predicate
* `(​dissoc​ hashmap key)` opposite of assoc, removes a key from a map
* `(disj set & vals)` removes things from sets

## Predicates

* `pos?`
* `neg?`
* `zero?`
* `true?`
* `nil?`
* `false?`
* Type checks: `string?` `keyword?` `number?` `int?` `pos-int?` `neg-int?` `nat-int?` `float?`
* `(some? x)` returns true if x is not nil, false otherwise (don't confuse with `some`)
* `(every? pred coll)` returns true if every value in the collection  meets the predicate, else false.
* `(some pred coll)` returns the first value in the collection that meets the predicate, or nil if nothing does. (Don't confuse this with `any?`)

`(not-any? pred coll)` Returns false if (pred x) is logical true for any x in coll, else true. I _think_ this is the logical complement to `some`, but not 100%.

Example from Clojure Applied Chapter 4:

```clojure
(not-any? neg? (vals m))
```
which returns false if there are any negative values in the collection m.

### Functions that compose predicates

`every-pred` is a higher-order function which takes two or more predicates, and returns a function which returns true if every predicate is met. 

```clojure
user=> ((every-pred number? odd?) 3 9 11)
true
```

`some-fn` is like the if version of it
```clojure
user=> ((some-fn even?) 1)
false
user=> ((some-fn even?) 2)
true
user=> ((some-fn even?) 1 2)
true
```

It's handy if you want to use `some` to check multiple things.

```clojure
(or (some even? [1 2 3])
    (some #(< % 10) [1 2 3]))

;; equivalent to

((some-fn even? #(< % 10)) 1 2 3)
```

## IO

parse integer: `(Integer/parseInt string)`

(can also do `(Integer. string)`)

### slurp / spit

`(slurp filename & args)`

Only args I've seen are `(slurp filename :encoding "ISO-8859-1")`

`(spit filename content & args)`

Especially `(spit filename content :append true)`

## Strings

* substring: `(sub string start & end)`
* split lines `(clojure.string/split-lines "hello\nworld\r\nnew line")`

`(re-matches pattern string)` is the regex finder. Returns the substring that matches the pattern. If the pattern has variable definitions, it will return a sequence of the substring, and the variables.

```clojure
(def input-pattern #"<x=(-?\d+), y=(-?\d+), z=(-?\d+)>")

(re-matches input-pattern "<x=5, y=-10, z=-123>")
```

returns a vector of `[(orig str) 5 -10 -123]`

## Numbers and Math

* `(Math/abs x)` absolute value

## Number fiddles

### separating a number into a collection of its digits

```clojure
(map #(Character/digit % 10) (str 1234))
```

## For Loops

## Set operations

* `(disj set & vals)` removes things from sets
