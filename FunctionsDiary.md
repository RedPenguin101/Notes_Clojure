# New Functions as I learn the language

## 2019-12-01

### quot
`(quot number divisor)`

Quotient: the number of times the number is wholly divisible by the divisor. cf. `mod`

```clj
(quot 10 3)
;=> 3
```

### iterate
`(iterate f x)`

Returns a lazy sequence of x, f(x), f(f(x)) etc.

__f has to take and return one argument__

Use to build up a result where the following step depends on the output of the previous one

```clj
(defn- fuel-one-level [mass]
  (max 0 (- (quot mass 3) 2)))

(defn fuel-required [mass]
  (reduce + (rest (take-while pos? (iterate fuel-one-level mass)))))
```

### pos?
`(pos? number)`

Returns true if number is positive

## 2019-12-02

### parseInt
`(Integer/parseInt string)`

Best way to get an integer from a string

### vec vs. vector
`(vec collection)` and `(vector a b & rest)`

Basically use `vec` when you want to act on a collection, `vector` when you
 want to create. Assume `(apply vec ...` would be similar to `vector`

### -> and ->> (thread first and thread last)
`(-> x & forms)` 

Inserts x as the _first_ argument (i.e. right after the function) of the
 first form - then same for the next forms
 
```clj
(-> "foo" (str "bar") (str "baz")) ;=> "foobarbaz"
;; expands out to
(str (str "foo" "bar") "baz")
```

`(->> x & forms)`

Inserts x as the _last_ argument (i.e. right before the bracket) of the
 first form - then same for the next forms
 
```clj
(->> "foo" (str "bar") (str "baz")) ;=> "bazbarfoo"
;; expands out to
(str "baz" (str "bar" "foo"))
```

###  mapv vs. map
`(mapv f & colls)`

`mapv` returns a vector where normal `map` returns a sequence.

`f` has to have an arity of the number of collections

### mapcat vs. map
`(mapcat f & colls)`

Same as `(concat (map f & colls))`

Use if you want to use map on a collection of collections, then bundle the
 results of the maps to one long collection

## 2019-12-03
### subs
`(subs string start)` `(subs string start end)`

Substring

### Math/abs

Absolute value

## 2019-12-04
### > < etc.
Not limited to two arguments here, you can test whether a series of numbers is in decreasing order

`(> 6 5 4 3 2) ;=> true`

And use the same functionality to test whether a number is between two other numbers

`(> 100 a 160)`

You can also use them as a comparator for a `sort` function

`(sort > [6 3 7 9 2]) ;=> (9 7 6 3 2)`

### clojure.set/intersection
`(clojure.set/intersection set1 set2)`

Does what it says on the tin: returns a sequence of all elements set1 and set2 have in common.

Used it as part of Advent of Code Day 3 to find matching pairs of coordinates in two sequences of points.

### partition-by

`(partition-by f coll)`

Outputs a sequence of sequences grouped according to function output. So if the function applied to the elements of the collection are the same, those elements get grouped together.

Used it in AoC Day 4 with `identity` when I wanted to count the occurances of digits in an integer:

```clj
(some #(= (count %) 2) (partition-by identity (seq code-str)))
;; turns the code-string (of numbers) into a seq of chars, partitions those into groups of the same char.
;; the some and lambda function test whether the length of any of the groups is equal to 2, implying there is a 'double'
;; integer int the code-string
```

## 2019-12-07

### Split lines
`(clojure.string/split-lines seq)`
Pretty simple, splits a string into a seq on `\n` or `\r\n`

## 2019-12-18

### Frequencies
in context of Moore Neighbourhood
https://rosettacode.org/wiki/Conway%27s_Game_of_Life#Clojure

### Some for checking if an element is in a sequence
```clj
(defn in? [collection element]
  (some #(= element %) collection))
```
### Math/abs
What you would expect




## TODO
* reduced
* condp
* concat
* sort
* .indexOf
* merge - marxama's day 6 solution
* mapcat, flatten (https://stuartsierra.com/2019/09/25/sequences-in-flatland)

