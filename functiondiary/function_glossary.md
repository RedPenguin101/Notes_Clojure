# Function Glossary

## TODO

* fnil (C4 of clojure applied)
* intersection, union and difference for set operations. combo/permutations
* clojure.string/split, join, trim
* using `(map vector v1 v2)` or `(map hashmap v1 v2)` to zip up vectors into 2tuples or maps (brave and bold vampire exmaple) (maybe `zipmap`? from divine cheese, not sure on distinction)
* Recursion patterns
  * if-recur
  * lazy set recursion from pegthing
  * iterate from fuel calculator
* assoc, conj, cons, assoc-in
* update, update-in
* get, get-in
* into
* if-let
* partial
* map, reduce deep dives
* mapcat = (apply concat (map f1 [1 2 3])), map and flatten
* mapv
* map-keys, map-vals
* clojure.walk/keywordize-keys (just walk in general)
* Math / numbers
  * mod, rem
  * min, max, apply max
  * `>` and `<` to check if something is sorted
* in sorting
  * reverse

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

## Snips to integrate

From Scenic: get a nice string rep of an inst with `(str (.toInstant my-inst))`


## Destructuring

## Manipulating collections

### Accessing things in collections

* `(:keyword map)` returns the value of that key
* `(map key)` does the same
* Get and get-in access things by index or key. get-in is the version for nested collections. You can also provide a 3rd argument as a default return value
* `(vector index)` returns the element at that index
* `(nth vector index default)` does the same, but has the same default behaviour as get
* `(select-keys map [key1 key2 ...])` returns the values at those keys. Also works with vectors and indexes, but kind of weirdly, so careful
* `some` can be used for sequential search of a collection, especially with sets, as described elsewhere

#### Common accessors

* first, second, ffirst, last, butlast, rest

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

### Changing the kv pairs in a hashmap

#### Assoc and Assoc-in

`(assoc map key value <key value>)`. Will replace anything that already has that key.

#### `update` and `update-in`

```clojure
(update map key function x y z ...)
````

updates a value with the specified key, by applying a function to it. x, y, z in the above are the other arguments to the function, if any.

The value will be the first argument passed, so it will look like

`(function (key map) x y z ...)`

```clojure
user=> (update {:name "James" :age 26} :age - 10)
{:name "James" :age 16}
```

update in is the version for nested maps

```clojure
(def users [{:name "James" :age 26}  {:name "John" :age 43}])

(update-in users [1 :age] inc)
;;=> [{:name "James", :age 26} {:name "John", :age 44}]
```


#### `select-keys` to take only specified keys from a map

```clojure
(select-keys map [:key1 :key2])
```
### Combining and joining collections

#### `zipmap` combines two arrays into a hashmap

```clojure
user=> (zipmap [:a :b :c :d :e] [1 2 3 4 5])
{:a 1, :b 2, :c 3, :d 4, :e 5}
```

#### `concat`

```clojure
(concat x y & zs)
```

Returns a lazy seq representing the concatenation of the elements in the supplied colls.

```clojure
user=> (concat [:a :b] [:c :d])
(:a :b :c :d)
```

Doesn't really care about what types of colls, but avoid maps.

A trick for de-nesting a collection one level. See also 'flattening a sequence'.

```clojure
user=> (apply concat '(([1 2]) ([3 4] [5 6]) ([7 8])))
([1 2] [3 4] [5 6] [7 8])
```

#### `merge`

`merge` smooshes two maps together. If there's a key-clash, rightmost wins

```clojure
(merge {:a 1 :b 2 :c 3} {:b 4 :d 5})
;; => {:a 1, :b 4, :c 3, :d 5}
```

A typical usecase is for overriding default values in configs, etc.

```clojure
;; not the right example!
```

#### `merge-with` is good for merging two maps

lets you merge, with a function describing how the values for the same keys get processed.

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
* `(keep function coll)` combines filter and map; applies the function to each element of the collection and keeps anything that is non-nil (including false).
* `(​dissoc​ hashmap key)` opposite of assoc, removes a key from a map
* `(disj set & vals)` removes things from sets
* `(distinct coll)` removes duplicates, but note that it can be memory intensive.
* `(dedupe coll)` is a less costly operation, but only removes _consecutive_ dupes (so will be equivalent to distinct only for sorted collections)

#### `pop` and `peek`

LIFO operations. Be aware they operate differently ont vecs and lists: Vecs they look to the _end_, lists to the _start_ (in line with how things are consed onto the structures).

`pop` returns a new collection with the 'last in' element removed.

`peek` returns just the 'last in' value.

```clojure
user=> (peek [1 2 3])
3
user=> (pop [1 2 3])
[1 2]
user=> (peek '(1 2 3))
1
user=> (pop '(1 2 3))
(2 3)
```

### Sorting collections

* `(sort comparator coll)` sorts a collection based on the results of a compartor function provided
* `(sort coll)` assumes `compare` as the comparator
* `(sort-by keyfn comparator map)` sorts the collection by comparing `(keyfn item)`

```clojure
user=> (sort-by :rank [{:rank 2} {:rank 3} {:rank 1}])
({:rank 1} {:rank 2} {:rank 3})
```

See 'sorted-set-by' for patterns on creating comparators with `juxt`.

### Grouping elements in a collection

* `(group-by f coll)`

```clojure
;; group strings by their length
(group-by count ["a" "as" "asd" "aa" "asdf" "qwer"])
;;=> {1 ["a"], 2 ["as" "aa"], 3 ["asd"], 4 ["asdf" "qwer"]}

;; group integers based a predicate - common use case
(group-by odd? (range 10))
;;=> {false [0 2 4 6 8], true [1 3 5 7 9]}

;;group by a key in a map
(group-by :user-id [{:user-id 1 :uri "baz"} {:user-id 2 :uri "foo"} {:user-id 1 :uri "bar"}])
;;=> {1 [{:user-id 1 :uri "baz"}{:user-id 1 :uri "bar"}] 2 [{:user-id 2 :uri "foo"}]}
```

#### Grouping by elements by their frequency

Given a collection of elements, `frequencies` returns a map with the number of times each element (the map keys) appears (the value).

```clojure
(frequencies [:a :b :a :b :a :d])
;; => {:a 3, :b 2, :d 1}
```

### Splitting up or partitioning a collection

* `(split-at n coll)` will return two collections split at the nth element (i.e the first will have have n elements). Sugar of `[(take n coll) (drop n coll)]`
* `(split-with pred coll)` = `[(take-while pred coll) (drop-while pred coll)]`

Partials are a help here:

```clojure
user=> (split-with (partial >= 3) [1 2 3 4 5])
[(1 2 3) (4 5)]

user=> (split-with (partial > 3) [1 2 3 2 1])
[(1 2) (3 2 1)]

user=> (split-with (partial > 10) [1 2 3 2 1])
[(1 2 3 2 1) ()]
```

* `(partition-by identity coll)` will group like elements in a collection, 

```clojure
user=> (partition-by identity [1 1 2 1 1 1 3 3])
[[1 1] [2] [1 1 1] [3 3]
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

### Into
```clojure
(into to from)
(into to transducer from)
```

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

### Check if a string includes a substring with `includes?`

`(includes? string subs)`

## Numbers and Math (calling Java's Math/fns)

* `(Math/abs x)` absolute value
* `Math/Pi` is Pi
* `Math/sqrt`
* `Math/pow`

## Number fiddles

### separating a number into a collection of its digits

```clojure
(map #(Character/digit % 10) (str 1234))
```

## For Loops

Use: when you want to do a for loop!

```clojure
(for seq-exprs body-expr)
```

List comprehension. Takes a vector of one or more binding-form/collection-expr pairs, each followed by zero or more  modifiers, and yields a lazy sequence of evaluations of expr.

Supports `:let`, `:while` (which halts execution, and is faster), `:when` (which doesn't halt execution) as part of seq-exprs.

```clojure
;; prepare a seq of the even values
;; from the first six multiples of three
(for [x [0 1 2 3 4 5]
      :let [y (* x 3)]
      :when (even? y)]
  y)
;;=> (0 6 12)
```

## Set operations

* `(disj set & vals)` removes things from sets

### Creating a sorted set with a comparator

Use `compare` function (which compares x and y and returns a -1, 0 or 1 when when x is logically 'less than', 'equal to', or 'greater than' y), to create a comparator function, then use `sorted-set-by`

```clojure
user> (sorted-set-by > 3 5 8 2 1)
#{8 5 3 2 1}
```

```clojure
;; from clojure applied C2
(defn compare-author [s1 s2]
  (letfn [(project-author [author]
            ((juxt :lname :fname) author))]
    (compare (project-author s1) (project-author s2))))

(sorted-set-by compare-author x y z)
```

## Higher order functions

### Comp

```clojure
(comp f g ...)

((comp f g) x)
;equivalent to
(g (f x))
```

### Juxt

Takes a set of functions and returns a fn that is the juxtaposition
of those fns. Calling the juxted function returns a vector containing the result of applying each fn to the args (left-to-right).

`((juxt a b c) x) => [(a x) (b x) (c x)]`

```clojure
;; from clojure applied C2
(defn compare-author [s1 s2]
  (letfn [(project-author [author]
            ((juxt :lname :fname) author))]
    (compare (project-author s1) (project-author s2))))
```

This juxt produces the function equivalent to:

`[(:lname author) (:fname author)]`

This demonstrates a useful technique for creating custom entity comparators that avoid the problem of underspecification.

### Partial

## Misc

* `(time ,,,)` times the execution of an operation.

