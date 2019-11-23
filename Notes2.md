https://learning.oreilly.com/library/view/getting-clojure/9781680506082/

# Leiningin
* `$ lein new app blottsbooks`
* `$ lein run`

# REPL
* get docstrings for function with `(doc average)`

# Data primatives
* keyword `:keyword`
* string "string"
* integer
* float `12.0`
* ratio `12/5`
* boolean `true` `false`
* `nil`
* Symbols literals (first class) `'author` (equality is string comparison)
* var is binding between symbols and value. access with `#'author`. Mulatble (hence re-`def`). Access value and symbol associations with respectively.
  * `(.get the-var)`
  * `(.-sym the-var)`

## truthy and falsy
* the only falsy things are `false` and `nil`
* everything else is truthy, even empty strings and collections, and 0

# Arithmetic functions

* `+ * - /`
* `(quot 8 3) ;=> 2`
* force floats with decimals if needed

# String functions

* `str` concats strings with space
* `.toUpperCase`


# Basic collections

## Vectors
* a stack-assigned array implementation
* an ordered collection
* can be hetero types
* contruct with `[1 2 3]` or `(vector 1 2 3)`
* `(conj novels "Carrie")` adds Carrie to __end__, 
* `(assoc [:title :by :published] 1 :author) ;=> [:title :author :published]`


## list
* A linked list implementation
* indicate literal with`'(1 2 3)`
* or `(list 1 2 3)`
* Stack conj `(conj novels "Carrie")` adds Carrie to __start__

## (Hash-)maps
* key, value pairs
* convention is keywords for keys, but can be anything
* constructed `{:key value}` or `(hash-map :key1 value1 :key2 value2)`
* `keys` and `vals` functions
* get value with `(get book :title)` 
* or use the map as a function, with a key as argument `(book :title)`
* _or_ (this is most common) use the _keyword_ as a function with map as arg `(title :book)`
* add kv pairs to a map with `(assoc book :page-count 234)`
* remove them with `(dissoc book :published)` (dissocing a key that isn't in the map returns the original map)
* a vector can be used as a map with integer keys

## Sets
* in mathematical sense
* construct `#{:a :b :c}` or `(set [:a :b :c])`
* elements forced unique
* used as a function with value as arg, returns arg if in set, else nil
* a keyword as a function passed a set will act the same
* `conj` adds to a set (and returns same set if element is already in it)
* `(disj authors "King")` removes King from authors


## General seq functions
* `first`
* `second`
* `rest` (returns empty collection on 1 or 0 length seq)
* (don't use these on maps generally, they are unordered)
* `count`
* `(nth books 2)` direct index, but the same as `(books 2)`
* `(cons "Carrie" novels)` adds Carrie to __start__, 
* `contains?` (note: use this rather than `(book :fish)` to check if :fish is in book, since the latter returns nil both if the kw is not in the map, and if it is in the map with value nil. `contains?` returns true or false

# Conditional

## predicates
* `(= 1 1) ;=> true`
* `(not= 1 1) :=> false`
* `> < >= <=`. Move symbol to middle of args to get infix equivalent
* type checking `number?` `string?` `keyword?` `map?` etc.
* `and` returns first falsy value or last argument if all truthy
* `or` returns first truthy argument or last value if all falsy
* `and` `or` use short circuit evaluation
* generally use predicates to test for truthyness or falsiness, not true or false explicity

## If
* general syntax

```clj
(if predicate (exec if-true) (exec if-false))
```

* if else clause left out, returns `nil`
* short expressions on a single
* also short circuit evaluation - unfollowed branch never evaled

## do
* `do` executes multiple expressions in sequence

```clj
(do
    (println "hello")
    (println "world")
    (println "!")
    44)
```

* can be used to make ifs with multipart legs

## when 
* an if statement with no falsy leg but implicit `do`
* `when-not` is also a thing which exists

## cond
* pattern matching 

```clj
(cond
    preferred-customer
    (< order-amount 50.0) 5.0
    (< order-amount 100.0) 10.0
    :else (* 0.1 order-amount))
```

* note else isn't part of the function, it's just something that will be evaluated as truthy. It could be literally anything else except false or nil

## case
* similar to cond, pattern matching on single value

```clj
(case status
   :gold "gold member"
   :preferred "preferred member"
   "pleb")
```

* constants (string here) have to be literals, there is no evaluation allowed
* the catch all is optional, not enforced and will error out if not included and there are no matching branches.

# Execeptions try-catch throw

```clj
(try
    (publish-book book)
    (catch ArithmeticException e (print "Math problem"))
    (catch StackOverflowError e (print "Can't publish")))
    
(when (not (:title book))
    (throw
        (ex-info "a book needs a title!" {:book book})))
```

* `ex-info` takes a string description and a map containing info and creates an exception object of type `closure.lang.ExceptionInfo` (which can be caught)


# functions

## arity
* functions can have different _arity_, meaning they can be passed different numbers of arguments, and have different branches based on different arities.
* idomatic way to use them is to use low arity branches to call higher arity versions with default values

```clj
(defn greet
    ([to-whom] (greet "Welcome to Blotts Books" to-whom))
    ([message to-whom] (println message to-whom)))
```

## varargs or variadic functions with &
* use `&` to denote 'everything else' in an argument list

```clj
(defn print-any-things [thing & args]
    (println "My" thing "s are" args))
```

## multimethods
* used for function dispatch
* a generalised of type-based polymorphism found in OO languages

```clj
(defmulti normalize-book dispatch-book-format)

(defmethod normalize-book :vector-book [book]
    {:title (first book) :author (second book)})

(defmethod normalize-book :standard-map [book]
    book)
    
(defmethod normalize-book :alternative-map [book]
    {:title (:book book) :author (:by book)})
    
(normalize-book {:title "War and Peace" :author "Tolstoy"})
(normalize-book {:book "War and Peace" :by "Tolstoy"})
(normalize-book ["War and Peace" "Tolstoy"])
```
* use in combination with a cond for powerful condition based dispatch
* a dispatch method with value `:default` will cover _everything else_
* dispatch based on keyword 'enum' values and you have easy extensible dispactch

```clj
(defmulti book-description :genre)
(defmethod book-description :romance [book] ;;stuff)
```

## checking inputs and outputs with pre and post
* avoids a lot of null checking etc.

```clj
(defn publish-book [book]
    {:pre [(:title book)] ; check title is in book
     :post [(boolean? %)]}
    (ship-book book))
```

* the pre is the key in a map, and the value is a vector of expressions which get evaluated.
* a falsy pre gives runtime exception
* post checks the return value (which is denoted by %)

## function producing functions

* `(apply function arg-seq)` is like `(function arg0 arg1 arg2 ...)`
* `partial` allows you to pass some arguments to a function, but not execute it, then you can pass it the rest of the arguments later and execute it

```clj
(defn cheaper-than [max-price book]
    (when (<= (:price book) max-price)
        book))

(def real-cheap? (partial cheaper-than 1.0))
(def quite-cheap? (partial cheaper-than 1.99))
(def slightly-cheap? (partial cheaper-than 5.99))
```

* `complement` produces a function that returns the negation of another function `(def not-adventure? (complement adventure?))`
* `every-pred` ands multiple predicate functions together
    
# Anonymous functions / function literals
* `#()`
* denote arguments with `%1` (or just `%` if only one), `%2` etc.
* or `(fn [] stuff)`

# Recursion

## loops
* all looping done through recursion: calling the function within the function
* should very rarely have to use it, there are higher level functions
* but syntax is:

```clj
(loop [books books
       total 0]
   (if (empty? books)
       total
       (recur
           (rest books) ; first argument of loop
           (+ total (:copies-sold (first books)))))) ; second arg of loop
```

# let
* creates a scope where you can temporarily name things.
* use for clarity

```clj
(let [discount (* amount discount-percent)
      discounted-amount (- amount discount)]
      ;function body)
```

* notice multiple assignments in one [] block
* notice also the 2nd assigment can use the outcome of the first in it's definition
* inside a `let` it's sequential evaluation, so you can have multiple expressions evaluating in order (the last is the return value)
* combining let with fn allows you to create very clear anonymous functions (that are also efficient).

```clj
(defn mk-discount-price-f
    [user-name user-discounts min-charge]
    (let [discount-percent (user-discounts user-name)]
      (fn [amount]
          (let [discount (* amount discount-percent)
                discounted-amount (- amount discount)]
            (if (> discounted-amount min-charge)
                discounted-amount
                min-charge)))))
                
(def compute-felicia-price (mk-discount-price-f "Felicia" user-discounts 10.0))

;...

(compute-felicia-price 20.0)
```

## If-let and when-let

* an if and a let rolled into one
```clj
; before
(defn upper-case-author [book]
    (let [author (:author book)]
        (if author 
            (.toUpperCase author)
             "ANONYMOUS")))
             
; after
(defn upper-case-author [book]
    (if-let [author (:author book)]
        (.toUpperCase author))
        "ANONYMOUS")
```

* `when-let` is to `if-let` as `when` is to `if`

