# Notes on clojure

## Lein
* `lein new app clojure-noob`
* `lein run`
* `lein uberjar` builds the project. Execute it with `java -jar target/uberjar/.../...jar`
* `lein repl` exit with C+d

## repl
* `(doc fn-name)` view docstring

## forms / expressions
* every form gets evaluated to produce a value
* two types: Literal representations (numbers, strings, maps, vectors) and operations.

## Literals and datatypes
* `true` `false` `nil` (nil is falsy, everything else, even 0, is truthy). Operators `(nil?)` `(true?)` `(false?)` are literal, don't pick up truthy or falsy.
* numbers: integers, floats, ratios: `92 1.2 1/5`
* strings: delineate with "" only, escape with `\`. No string interpolation, only concatination
* keywords: start with `:`. 
* maps (dictionary): 
	* create with `{:first-name "Charlie" :last-name "Mac"}`. Can be nested map. Keys and values can be any types.
	* get with `(get {:a 0 :b 1} :b) => 1`. Optional 3rd arg for failover return value
	* or `(:d {:a 1 :b 2 :c 3} "failover") => "failover"`
	* or treat the map like a function `({:name "joe"} :name) => "joe"`. revering arg order gets same result
	* get-in for nested `(get-in {:a 0 :b {:c "jello"}} [:b :c]) => "jello"`
* vector: array, 0 indexed collection. can contain different types `[3 2 1]`. 
	* `(get [3 2 1] 0) => 3`
	* conjoins to end `(conj [1 2 3] 4) => [1 2 3 4]`
* list literal (tuple). can contain different types. `'(1 2 3 4)`. 
	* `(nth '(:a :b :c) 0) => :a`
	* conjoins to start `(conj '(1 2 3) 4) => (4 1 2 3)`
* sets, or hash set: `#{"hello" 20 :icicle}`. elements must be unique. `(set [3 3 3 4 4]) => #{3, 4}`. 
	* `(contains #{:a :b} :a) => true`
	* get or keyword fetching work like maps.


## operations
* operations: `(operator operand1 operand2...)`
* `str` concatinates strings
* + - *
* `(= 1 1) => true` - works on all types
* `inc` increments by 1


## control flow
```clj
(if boolean-form
	then-form
	optional-else-form)
```

```clj
(if true
	(do (println "Success!")
		"By Zeus's hammer")
	(do (println ("Failure!")
		"By Aquaman's Trident!"))
```

`when` is like an if with do, but no else

```clj
(when true
	(println "Success!")
	"abra cadabra")
```

`or` returns first truthy value or `false`

```clj
(or false nil :one :two)
; => :one

(or (= 0 1) (= "yes" "no"))
; => false
```

`and` returns first falsy value or last value

```clj
(and :one :two) => :two
(and :one nil false :two) => nil
```

## definition / binding
* `(def names ["name1" "name2" "name3"])`

## Functions
* higher order functions: functions that take other functions as arguments
* `(map inc [0 1 2 3]) => (1 2 3 4)`
* expression types: function calls, macro calls, special forms
* 'special forms' don't always evaluate all operands. `if` is example: the else branch isn't evaluated. They can't be passed as arguments. Same with macros

* define functions like `(defn fn-name opt-doc-string [params] (body))`
* number of params is 'arity'. 0-arity, 4-arity etc.
* can arity-overload `(defn fn ([a b c] (body)) ([a b] (body)))`
* use ao for default values `(defn fn ([a b] (body)) ([a] (fn a "default")))`
* rest parameter `&` says 'put the rest of arguments in a list with the following name' `(defn fn [name & things] (body))`. must come last
* destructuring: binding names to values within a collection that's being passed as an argument. `(defn fn [[one two & rest]] (str "first is" one " second is " two " and rest are " (clojure.string/join ", " rest)))`
* works with vectors and maps, sets or vectors. 
* maps like
	* `(defn fn [{lat :lat lon :lon}] (...))` you can refer to lat in body and it will find the value of the key `:lat`
	* more concise: `(defn fn [{:keys [lat lon]} :as location] (...))`. the `:as` also gives you access to the full object.

* functions return their last form evaluated.

## Anonymous functions
* `(fn [params] body)` or `#(operator % operand)` where `%` indicates parameter. If using more than one, use `%1` `%2` etc. `%&` is rest param
* You can return an anonymous function from another function - the returned fuction is called a _closure_.

```clj
(def inc-maker
	"Create a custom incrementor"
	[inc-by]
	#(+ % inc-by))

(def inc3 (inc-maker 3))

(inc3 7)
;=> 10
```

## string functions and regex
* `(clojure.string/replace input #"^left-" "right-")`
* `(clojure.string/join ", " rest)))`
* `(re-find #"^left-" "left-eye") ; => "left-"`
* `(re-find #"^left-" "cleft-chin") ; => nil`
* `(re-find #"^left-" "chin") ; => nil`

## let
* binds names to values.
* provide clarity by letting you name things
* allow you to evaluate an expression only once and reuse the result (good for expensive calls)
* `(let [x 3] x) ;=> 3`
* `def` is global, `let` is local
```clj
(def x 0)
(let [x 1] x) ; => 1
x
; => 0
```

## Loop
```clj
(loop [iteration 0]
	(println (str "Iteration " iteration))
	(if (> iteration 3)
		(println "Goodbye")
		(recur (inc iteration))))
; => Iteration 0
; => Iteration 1
; => Iteration 2
; => Iteration 3
; => Iteration 4
; => Goodbye
```
* `loop` is like an anonymous function with a parameter iteration, that is called by `recur` from within itself.


## symmetrize example
```clj
(def matching-part
	[part]
	{:name (clojure.string/replace (:name part) #"^left-" "right-")
	:size (:size part)})

(def symmetrize-body-parts
	"Expects an array of maps that have a :name and :size"
	[asym-body-parts]
	(loop [remaining-asym-parts asym-body-parts
		   final-body-parts []]
		(if (empty? remaining-asym-parts)
			final-body-parts
			(let [[part & remaining] remaining-asym-parts]
				(recur remaining
					(into final-body-parts
					; add set into vector fbp
						(set [part (matching-part part)])))))))
						; create a set of part and matching part (set so only take uniques)
```

* this represents a common pattern: a sequence is split into a _head_ and _tail_ (rest). The head is processed, added to a result (final-body parts) and the operation is recursed on the tail.
* the loop binds rap to the input value, and the result sequence fpb to an empty vector.
* if rap is empty, return final body parts
* otherwise split the head and tail
* recur with the tail as rap and the fpb as the part and its match pushed into what you started with

## reduce
* the 'process each element and build a result' is aka as _reduce_
* `(reduce + [1 2 3 4]) ; => 10`
* with an optional initial value: `(reduce + 15 [1 2 3 4]) ; => 25`
* generically `(reduce <function> <init> <sequence>)`

```clj
(defn symmetrize
	[parts]
	(reduce 
		(fn [final-parts part]
			(into 
				final-parts 
				(set [part (matching-part part)])))
		[]
		parts))
```

* reduce is less code, and is still more expressive than using a loop

## hit example
```clj
(defn hit
	[parts]

	(let [sym-parts (symmetrize parts)
		  total-size-of-parts (reduce + (map :size sym-parts))
		  target (rand total-size-of-parts)]
		(loop [[part & remaining] sym-parts
			   accumulated-size (:size part)]
			(if (> accumulated-size target)
				part
				(recur 
					remaining 
					(+ accumulated-size (:size (first remaining))))))))
```