https://learning.oreilly.com/library/view/getting-clojure/9781680506082/

# Leiningin
* `$ lein new app blottsbooks`
* `$ lein run`


# REPL
* Get docstrings for function with `(doc average)`


# Namespaces
* In VM, clojure creates a namespace `user` and makes it current
* Create new namespace with `(ns pricing)`. This makes it the current namespace.
* Access things in another namespace by fully qualifying with `pricing/discount-price`
* Load external libraries into your JVM instance with `(require 'clojure.data)`
* Namespace must match folder structure. So namespace `blottsbooks.core` must be found in the file _src/blottsbooks/core.clj_ to be properly referenced (dashes in namespace should be converted to underscores in filename)
* Fold `require` into your namespace definitions like

```clj
(ns blottsbooks.core
    (:require blottsbooks.pricing) ; note different syntax
    (:gen-class))
```

* Shortcut full-qualification with alias `(require '[blottsbooks.pricing :as p])`
* Or just avoid it by pulling in defs directly `(require '[blottsbooks.pricing :refer [discount-price]])` - though be careful with this, as you risk namespace collision


# Data primatives
* Keyword `:keyword`
* String "string"
* Integer `12`
* Float `12.0`
* Ratio `12/5`
* Boolean `true` `false`
* `nil`
* Symbols literals (first class) `'author` (equality is string comparison)
* Var is binding between symbols and value. Access with `#'author`. Mutable (hence re-`def`). Access value and symbol associations with respectively.
  * `(.get the-var)`
  * `(.-sym the-var)`

## Truthy and Falsy
* The only falsy things are `false` and `nil`
* Everything else is truthy, even empty strings and collections, and 0

## Arithmetic functions
* `+ * - /`
* `(quot 8 3) ;=> 2`
* Force floats with decimals if needed
* Do exponents with `(defn ** [x n] (reduce * (repeat n x)))`

## String functions
* `str` concats strings with space
* `.toUpperCase`
* `(clojure.string/replace input find-reg replace)`
* `(clojure.string/join ", " sequence)`
* `(re-find regex string)` - find substring
* `(re-seq regex string)` - returns lazy sequence of regex matches
* `(clojure.string/trim text)` cleans whitespace from start and end


# Collections

## Vectors
* A stack-assigned array implementation
* An ordered collection
* Can be hetero types
* Contruct with `[1 2 3]` or `(vector 1 2 3)`
* `(conj novels "Carrie")` adds Carrie to __end__, 
* assoc to 'replace' elements in a vector `(assoc [:title :by :published] 1 :author) ;=> [:title :author :published]`

## List
* A linked list implementation
* Indicate literal with`'(1 2 3)`
* Or `(list 1 2 3)`
* Stack conj `(conj novels "Carrie")` adds Carrie to __start__

## (Hash-)maps
* Key, value pairs
* Convention is keywords for keys, but can be anything
* Constructed `{:key value}` or `(hash-map :key1 value1 :key2 value2)`

### Getting stuff from maps
* `keys` and `vals` functions
* use `first` and `second` as a way to pull out the key and value a map pair
* Get value with `(get book :title)` 
* Or use the map as a function, with a key as argument `(book :title)`
* _Or_ (this is most common) use the _keyword_ as a function with map as arg `(title :book)`
* `get-in` is the nested version of `get`, pass in arg as a vec `[:b :c]` will pull out the value of `:c` in the map `{:a 0 :b {:c "value"}}`
* All of these get methods can take a failover as a third argument.
* Use `map` on a vector of maps to pull out all the attributes you want `(map :my-key vec-of-maps)`

### Adding values to existing map
* Add kv pairs to a map with `(assoc book :page-count 234)`
* Remove them with `(dissoc book :published)` (dissocing a key that isn't in the map returns the original map)
* You can 'dig into' a nested map with `assoc-in`

```clj
(def foo {:user {:bar "baz"}})
(assoc-in foo [:user :id] "some-id")
;=> {:user {:bar "baz" :id "some-id"}}

```
* A vector can be used as a map with integer keys

### Other Map stuff
#### Zipmap
```clj
(zipmap [:a :b] [1 2])
; => {:a 1 :b 2}
```
* Takes two seqs, returns a map where the first seqs are the keys and the second the values.

#### Merge-with
```clj
(merge-with fn map1 map2)
```
* Returns a map that performs the function stated on all matching elements of the provided maps.

## Sets
* In mathematical sense
* Construct `#{:a :b :c}` or `(set [:a :b :c])`
* Elements forced unique
* Used as a function with value as arg, returns arg if in set, else nil
* A keyword as a function passed a set will act the same
* `conj` adds to a set (and returns same set if element is already in it)
* `(disj authors "King")` removes King from authors

## General seq functions
### Getting
* `first`
* `second`
* `rest` (returns empty collection on 1 or 0 length seq)
* `butlast` (returns all but the last element of a collection)
* (Don't use these on maps generally, they are unordered)
* `count`
* `(nth books 2)` direct index, but the same as `(books 2)`
* `contains?` (note: use this rather than `(book :fish)` to check if :fish is in book, since the latter returns nil both if the kw is not in the map, and if it is in the map with value nil. `contains?` returns true or false
* `clojure.data/diff` acts on two sequences and returns 3: first with uniques in first, 2nd with uniques in second, third with matches `[nil nil "this matches]`


#### Take(-while), Drop(-while)
```clj
(take number collection)
(drop number collection)
(take-while predicate collection)
(drop-while predicate collection)
```
* Take-while stops taking as soon as it hits a falsey evaluation.
* Drop-while _starts_ taking as soon as it hits a _truthy_ evaluation.

### Setting and combining
* `(cons "Carrie" novels)` adds Carrie to __start__, 
* `(into to-seq from-seq)` puts the elements from one seq into another
* `into` can be used to type-coerce `(into [] my-list)`
* `(concat seq1 seq2)` takes all elements of 2 or more seqs and combines them into one. (treats strings as seqs of chars)
* `interleave` takes 2 or more collections and interleaves them
* `interpose` is like interleave, but with a scalar as one of the args
* `mapcat` is a combination of concat and map, concatenating the results of a map. Useful for flattening nested structures


# Conditional

## Predicates
* `(= 1 1) ;=> true`
* `(not= 1 1) :=> false`
* `> < >= <=`. Move symbol to middle of args to get infix equivalent
* Type checking `number?` `string?` `keyword?` `map?` etc.
* `and` returns first falsy value or last argument if all truthy
* `or` returns first truthy argument or last value if all falsy
* `and` `or` use short circuit evaluation
* Generally use predicates to test for truthyness or falsiness, not true or false explicity

## If
* General syntax

```clj
(if predicate (exec if-true) (exec if-false))
```

* If else clause left out, returns `nil`
* Short expressions on a single
* Also short circuit evaluation - unfollowed branch never evaled

## If-not

## Do
* `do` executes multiple expressions in sequence

```clj
(do
    (println "hello")
    (println "world")
    (println "!")
    44)
```

* Can be used to make ifs with multipart legs

## When 
* Sn if statement with no falsy leg but implicit `do`
* `when-not` is also a thing which exists

## Cond
* Pattern matching 

```clj
(cond
    preferred-customer
    (< order-amount 50.0) 5.0
    (< order-amount 100.0) 10.0
    :else (* 0.1 order-amount))
```

* Note else isn't part of the function, it's just something that will be evaluated as truthy. It could be literally anything else except false or nil

## Case
* Similar to cond, pattern matching on single value

```clj
(case status
   :gold "gold member"
   :preferred "preferred member"
   "pleb")
```

* Constants (string here) have to be literals, there is no evaluation allowed
* The catch-all at the end is optional, not enforced and will error out if not included and there are no matching branches.


## Predicates on Sequences

### Some
```clj
(some predicate collection)
```
* Returns when the predicate function finds a true (Note: if you want to actually get the value of the evaluation back, your predicate function needs to do that)

### Every-pred
* `every-pred` takes one or more predicate-functions and returns a function f that returns true if all predicate functions return true.

### Other simple stuff
* `empty?` returns true is seq is empty
* (note: to test for not-empty, use `(seq x)` not `(not (empty? x))`
* `not-empty` returns nil if empty, collection if not empty
    

# Exceptions try-catch throw

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


# Functions

## Arity
* Functions can have different _arity_, meaning they can be passed different numbers of arguments, and have different branches based on different arities.
* Idomatic way to use them is to use low arity branches to call higher arity versions with default values

```clj
(defn greet
    ([to-whom] (greet "Welcome to Blotts Books" to-whom))
    ([message to-whom] (println message to-whom)))
```

## Varargs or variadic functions with &
* Use `&` to denote 'everything else' in an argument list

```clj
(defn print-any-things [thing & args]
    (println "My" thing "s are" args))
```

## Multimethods
* Used for function dispatch
* A generalised of type-based polymorphism found in OO languages

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
* Use in combination with a cond for powerful condition based dispatch
* A dispatch method with value `:default` will cover _everything else_
* Dispatch based on keyword 'enum' values and you have easy extensible dispactch

```clj
(defmulti book-description :genre)
(defmethod book-description :romance [book] ;;stuff)
```

## Checking inputs and outputs with pre and post
* Avoids a lot of null checking etc.

```clj
(defn publish-book [book]
    {:pre [(:title book)] ; check title is in book
     :post [(boolean? %)]}
    (ship-book book))
```

* The pre is the key in a map, and the value is a vector of expressions which get evaluated.
* a falsy pre gives runtime exception
* post checks the return value (which is denoted by %)


# Function producing functions

## Apply
* `(apply function arg-seq)` is like `(function arg0 arg1 arg2 ...)`

## Partial
* `partial` allows you to pass some arguments to a function, but not execute it, then you can pass it the rest of the arguments later and execute it

```clj
(defn cheaper-than [max-price book]
    (when (<= (:price book) max-price)
        book))

(def real-cheap? (partial cheaper-than 1.0))
(def quite-cheap? (partial cheaper-than 1.99))
(def slightly-cheap? (partial cheaper-than 5.99))
```

## Complement
* `complement` produces a function that returns the negation of another function `(def not-adventure? (complement adventure?))`


# Anonymous functions / function literals
* `#()`
* Denote arguments with `%1` (or just `%` if only one), `%2` etc.
* Or `(fn [] stuff)`


# Recursion

## Loops
* All looping done through recursion: calling the function within the function
* Should very rarely have to use loop explicitly, there are higher level functions that implement it
* But syntax is:

```clj
(loop [books books
       total 0]
   (if (empty? books)
       total
       (recur
           (rest books) ; first argument of loop
           (+ total (:copies-sold (first books)))))) ; second arg of loop
```

## Map
```clj
(map function collection)
```
* Apply a function to each element of a collection in turn.
* When given more than one collections, they will be applied pairwise.
* It can be used 'backwards', and be passed a collections of functions as arguments, and apply all of those functions in turn.

## Reduce
```clj
(reduce function target source)
```
* Process each element of a collection and build a result


# Let
* Creates a scope where you can temporarily name things.
* Use for clarity

```clj
(let [discount (* amount discount-percent)
      discounted-amount (- amount discount)]
      ;function body)
```

* Notice multiple assignments in one [] block
* Notice also the 2nd assigment can use the outcome of the first in it's definition
* Inside a `let` it's sequential evaluation, so you can have multiple expressions evaluating in order (the last is the return value)
* Combining let with fn allows you to create very clear anonymous functions (that are also efficient).

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

* An if and a let rolled into one
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

# Bindings

* Allows _scope limited, temporary_ mutation of state
* Variables to be bound must be pre-declared with `^:dynamic` metadata
* Convention is to wrap symbol with `*`
* Syntactically like a let
* Has memory overhead. Don't overuse it.

```clj
(def ^:dynamic *debug-enabled* false)

(binding [*debug-enabled* true]
    (debug "calling the function")
    (some-troublesome-function)
    (debug "back from the function"))
```
