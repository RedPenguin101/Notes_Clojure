# Function Glossary

## Libraries

### Core
* Core async `[clojure.core.async]`
  * leiningen: `[org.clojure/core.async ""]`
  * [github](https://github.com/clojure/core.async)


### Data and IO
* `[clojure.data.json]`
* Cheshire: Fast and featureful JSON en/decoding
  * `[cheshire.core :refer :all]`
  * leiningen `[cheshire "5.10.0"]`
  * [github](https://github.com/dakrone/cheshire)


### Utilities

* Math Combinatorics `[clojure.math.combinatorics]`

### App design

* __Component__: framework for managing the lifecycle and dependencies of software components which have runtime state.
  * `[com.stuartsierra.component :as component]`
  * leiningen: `[com.stuartsierra/component ""]`
  * [github](https://github.com/stuartsierra/component)

* __Environ__: library for managing environment settings from a number of different sources.
  * `[environ.core :refer [env]]`
  * leiningen `[[environ "1.1.0"]]`
  * [github](https://github.com/weavejester/environ)

* __Immuconf__:  library for explicitly managing configuration files
  * `?`
  * leiningen `[levand/immuconf "0.1.0"]`
  * [github](https://github.com/levand/immuconf)

## TODO

* merge-with (from Chapter 4 of Clojure Applied)
* every, some? every? 
* not-any? (C4 of clojure applied)
* for
* fnil (C4 of clojure applied)
* disj(oin) (C4 of clojure applied)

## Migrate from function diary

* iterate
* pos? neg? zero?
* parseInt
* vec and vector
* thread
* mapv
* mapcat
* subs
* Math/abs
* > and <
* set/intersection and union
* partition-by
* some
* destructuring
* split-lines
* frequencies
* using sets to check whether an element is in a sequence
* Using comp for smart list filtering
* flattening sequences with apply concat, mapcat seq, flatten and mapcat identity
* merge
* separating out digits from an integer
* remove
* rem vs. mod
* re-matches
* compare

## Manipulating collections

### Checking if an element is in a collection

Sets are your friend here

`((apply set my-coll) element)` will return the element if it's in my-coll, otherwise nil.

`(some #{element} my-coll)` will do the same.

To check if a key is in a map, if it's a KW use `(:key map}` or if not `(map "key")`

To check if a value is in a map, just pull out the values.

`(some #{"fish"} (vals {:a "a" :b "b" "key" "fish"}))`

### Checking the frequency of an element in a collection

Given a collection of elements, `frequencies` returns a map with the number of times each element (the map keys) appears (the value).

`(frequencies [:a :b :a :b :a :d])`
`;; => {:a 3, :b 2, :d 1}`

### Flattening a sequence

* `(apply concat nested-vec)` and `(mapcat identity nested-vec)` do basically the same thing: removes ONE level of nesting.
* `(flatten nested-vec)` is more aggressive, removes ALL nesting.

```clojure
(apply concat [[1 2 3] [:x [:y :z]]])
;; => (1 2 3 :x [:y :z])

(flatten [[1 2 3] [:x [:y :z]]])
;; => (1 2 3 :x :y :z)
```

### Combining maps

`merge` smooshes two maps together. If there's a key-clash, rightmost wins

```clojure
(apply concat [[1 2 3] [:x [:y :z]]])
;; => (1 2 3 :x [:y :z])

(flatten [[1 2 3] [:x [:y :z]]])
;; => (1 2 3 :x :y :z)
```

A typical usecase is for overriding default values in configs, etc.

```clojure
(apply concat [[1 2 3] [:x [:y :z]]])
;; => (1 2 3 :x [:y :z])

(flatten [[1 2 3] [:x [:y :z]]])
;; => (1 2 3 :x :y :z)
```

### Removing things from collections

`(drop n coll)` drops the first n values from a collection

`(drop-last n coll)` drops the last n values

`(drop-while pred coll)` iterates through the collection, dropping everythin until it hits something that causes the predicate to fail.

`(remove pred coll)` is the opposite of filter, it runs through the collection and drops anyhing that meets the predicate

## Simple predicates

* `pos?`
* `neg?`
* `zero?`
* `true?`
* `nil?`
* `false?`
* `string?` `keyword?` `number?` `int?` `pos-int?` `neg-int?` `nat-int?` `float?`

## IO

parse integer: `(Integer/parseInt string)`

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

## Number fiddles

### separating a number into a collection of its digits

```clojure
(map #(Character/digit % 10) (str 1234))
```
