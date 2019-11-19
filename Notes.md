# Notes on clojure

## Lein
* `lein new app clojure-noob`
* `lein run`
* `lein uberjar` builds the project. Execute it with `java -jar target/uberjar/.../...jar`
* `lein repl` exit with C+d

## repl
* `(doc fn-name)` view docstring

## forms / expressions
* A form is a chunk of code
* every form gets evaluated to produce a value
* two types two types of forms: Literal representations (numbers, strings, maps, vectors) and operations.

## Literals and datatypes
* `true` `false` `nil` (nil is falsy, everything else, even 0, is truthy). Operators `(nil?)` `(true?)` `(false?)` are literal, don't pick up truthy or falsy.
* numbers: integers, floats, ratios: `92 1.2 1/5`
* strings: delineate with "" only, escape with `\`. No string interpolation, only concatination
* keywords: start with `:`. 

### Maps (dictionaries)
* create with `{:first-name "Charlie" :last-name "Mac"}`. 
* Can be nested map. 
* Keys and values can be any types.

#### getting elements from a map
* `get`: `(get {:a 0 :b 1} :b) => 1`. 
* just use a key like an operator on a map. 
* or treat the map like a function `({:name "joe"} :name) => "joe"`. 
* reversing the argument order `(:name {:name "joe"})` is the same thing
* Optional 3rd arg for failover return value `(:d {:a 1 :b 2 :c 3} "failover") => "failover"`
* get-in for nested using a key sequence `(get-in {:a 0 :b {:c "jello"}} [:b :c]) => "jello"`

### Vectors (arrays)
* 0 indexed collection
* square brackets `[3 2 1]`. 
* can contain different types
* index with get and numbers `(get [3 2 1] 0) => 3`
* conjoins to end `(conj [1 2 3] 4) => [1 2 3 4]`

### List literal - tuple
* `'(1 2 3 4)`. 
* can contain different types. 
* index with nth and numbers `(nth '(:a :b :c) 0) => :a`
* conjoins to start `(conj '(1 2 3) 4) => (4 1 2 3)`

### sets / hash sets
* `#{"hello" 20 :icicle}`. 
* elements must be unique. `(set [3 3 3 4 4]) => #{3, 4}`. 
* `(contains? #{:a :b} :a) => true`
* get with get
* coerce a list or vector into a set with `set`. Note this will strip out any duplicates


## operations
* operations: `(operator operand1 operand2...)`
* `str` concatinates strings
* + - *
* `(= 1 1) => true` - works on all types
* `inc` increments by 1
* `(mod 10 6)` = 4

## Combining sequences

### cons
* returns a new list with an element appended to the given list
* `(cons 0 '(2 4 6)) ; => (0 2 4 6)`

### into

* takes two collections and adds all elemts from the second into the first
* `(into structure1 structure2)`
* use it to coerce the list literal you get back from most functions into the type of datastructure you want
 
### conj
* takes a _target_ sequence and one or more scalar _additions_ and adds the scalars to the sequence
* simlar to into

### Concat

* appends the members of one sequence into another

`(concat [1 2] [3 4])`

## Collection operators

* `(empty? [])`
* `(count [1 2 3])`


## control flow

### do

* lets you wrap up forms in parentheses and run them
* `(do (thing 1) (thing 2))`

### if and when

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

### or and and

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
* binds a name to a value
* `(def names ["name1" "name2" "name3"])`

## Functions concepts

* define functions like `(defn fn-name opt-doc-string [params] (body))`
* `(map inc [0 1 2 3]) => (1 2 3 4)` Note it doesn't return a vector, even though we gave it one
* expression types: function calls, macro calls, special forms
* 'special forms' don't always evaluate all operands. `if` is example: the else branch isn't evaluated. They can't be passed as arguments. Same with macros
* functions return their last form evaluated.


### Arity and Overloading

* number of params is 'arity'. 0-arity, 4-arity etc.
* can arity-overload `(defn fn ([a b c] (body)) ([a b] (body)))`
* use ao for default values `(defn fn ([a b] (body)) ([a] (fn a "default")))`

```clj
(defn x-chop 
	([name chop-type]
		(str "I " chop-type " chop " name "!"))
	([name]
		(x-chop name "karate")))
```

### Rest & Destructuring
* rest parameter `&` says 'put the rest of arguments in a list with the following name' `(defn fn [name & things] (body))`. must come last
* destructuring: binding names to values within a collection that's being passed as an argument. 

```clj
(defn fn 
	[[one two & rest]]  ; notice we are implying that a vector is being passed in and what is in it
	(str "first is" one " second is " two " and rest are " (clojure.string/join ", " rest)))
```

* works with vectors and maps, sets or vectors. 
* maps like
* `(defn fn [{lat :lat lon :lon}] (...))` you can refer to lat in body and it will find the value of the key `:lat`
* or more concise: `(defn fn [{:keys [lat lon]} :as location] (...))`. the `:as` also gives you access to the full object.


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

## specific functions
### string functions and regex
* `(clojure.string/replace input #"^left-" "right-")`
* `(clojure.string/join ", " sequence)))`
* `(re-find #"^left-" "left-eye") ; => "left-"`
* `(re-find #"^left-" "cleft-chin") ; => nil`
* `(re-find #"^left-" "chin") ; => nil`
* `^` means will only match at beginning of string
* `(clojure.string/trim text)` cleans off whitespace

### let
* binds names to values.
* provide clarity by letting you name things
* allow you to evaluate an expression only once and reuse the result (good for expensive calls)
* syntax is `(let [x-be this-input and-y-be this-input] (use-them-here x-be and-y-be))`
* `(let [x 3] x) ;=> 3`
* `def` is global, `let` is local

```clj
(def x 0)
(let [x 1] x) ; => 1
x
; => 0
```
### Loop
* `loop` is like an anonymous function with a parameter iteration, that is called by `recur` from within itself.
* `recur` is better than literally calling the function for speed reasons
* general syntax 

```clj
(loop [thing initial-setting] 
	(do-something) 
	(if (condition met-on thing)
		(terminate stuff)
		(recur (with-this-as-thing))))
```

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

## Sequence function
### Map
* applies a function to one or more collections
* It can take multiple collections as arguments, with the contents of the collections being passed in pair-wise

```clj
(map str ["a" "b" "c"] ["A" "B" "C"])
; => ("aA" "bB" "cC"), like (str "a" "A") (str "b" "B") etc.
```

* It can take a collection of _functions_ as an argument, and applies each function in turn to an input value.

```clj
(def sum #(reduce + %))
(def avg #(/ (sum % (count %))))
(defn stats
    [numbers]
    (map #(% numbers) [sum count avg]))

(stats [3 4 10])
; => (17 3 17/3)
```

* A common pattern: using keywords as the mapping function, to get values associated with a key from a collection

```clj
(def identities
    [{:alias "Batman" :real "Bruce Wayne"}
     {:alias "Spiderman" :real "Peter Parker"}])
     
(map :real identities)
; => ("Bruce Wayne" "Peter Parker")
```

### reduce
* 'process each element and build a result' 
* whenever you want to derive a new value from a seqable data structure
* `(reduce + [1 2 3 4]) ; => 10`
* with an optional initial value: `(reduce + 15 [1 2 3 4]) ; => 25`
* generically `(reduce <function> <target-struct> <sequence to iterate over>)`

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
* 'update' a maps values

```clj
(reduce (fn [new-map [key val]]
            (assoc new-map key (inc val)))
            
            {}
            {:max 30 :min 10}) ; effectively treated as a seq of vectors
; => {:max 31, :min 11}
```

* filter our keys from a map based on value

```clj
(reduce (fn [new-map [key val]]
            (if (> val 4)
                (assoc new-map key val)
                new-map))
            {}
            {:human 4.1 :critter 3.9})
; => {:human 4.1}
```
### take(-while) drop(-while)
* take and drop both take a number and a sequence. `take` gets elements from the sequences and `drop` 'deletes' them

`(take 3 [1 2 3 4 5]) ; => (1 2 3)`
`(drop 3 [1 2 3 4 5]) ; => (4 5)`

* take-while and drop-while take a predicate function to decide when it should _stop_ taking or dropping. When take while hits a false it stops processing, and when drop-while hits a _true_ it stops dropping. 

`(take-while #(< (:month %) 3) food-journal)`

* you can use them to get the middle elements

```clj
(take-while #(< (:month %) 4) 
    (drop-while #(< (:month %) 2) food-journal))
```

### Filter 
* filter can end up being less efficient than take-while because it will look through every element in the sequence - sometimes you don't need it to.

### Some
* returns the first truthy value that tests true
* your 'predicate' function has to return the value if you want to actually get it.

`(some #(and (> (:critter %) 3) %) food-journal)`
* here the and operates on the predicate tester and the input value, and returns the input value if the predicate is true.

## IO
*`(slurp filename)` grabs contents of that file

## Time
* `(time (other stuff))` prints the elapsed time and the value of form
* `(Thread/sleep 1000)` sleeps for 1000 miliseconds

## infinate and lazy sequences
* `(repeat "na")` creates an infinite of the given element
* `(repeatedly #(rand-int 10))` calls a function infinitely and assigns return vals to a list
* force an evaluation to be lazy with `lazy-seq`

```clj
(defn even-numbers
    ([] (even numbers 0))
    ([n] (cons n (lazy-seq (even-numbers (+ n 2))))))
    
(take 10 (even-numbers))
```

## Function Functions

### Apply
* both accepts and returns a function
* explodes a sequence so it can be passed to a function that expects a rest parameter
* `(apply max [1 2 3]) ;=> 3`
* what is returned is `(max 1 2 3)`
* `(apply max 1 [2 3 4]) ;=> 4`

### Partial
* takes a function and any number of args, returns a function
* that returned function can be called with new args, and then the 'inner' function is called with the initial and new args.

`((partial + 10) 5) ;=> 15`

* use partials to keep your code clean, no repeat yourself.

### Comp
* compose functions from other functions
* `((comp inc *) 2 3) ;=> 7`
* creates a g(x) like f1(f2(x))
* you can use it to get values from nested maps: `(comp :strength :attributes)`
* all functions except the first applied (the one on the right of the comp form) have to take only one arguement
* you can get around that to some extent by wrapping anonymous functions

```clj
(defn spell-slots
    [char]
    (int (inc (/ (c-int char) 2))))
    
; int inc c-int all take one arg, but / takes two.
    
(def spell-slots-comm (comp int inc #(/ % 2) c-int))
```
