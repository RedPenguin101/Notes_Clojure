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

f has to take and return one argument

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

### -> and ->> (thread first and thread last)

### mapv vs. map

### reduced vs. reduce

## 2019-12-03

### clojure.string/split-lines

### subs

### condp

### concat

### sort

### clojure.set/intersection

### .indexOf
