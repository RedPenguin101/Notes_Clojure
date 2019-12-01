# Spec

(mostly from https://clojure.org/guides/spec)

To use it, require it

```clj
(require '[clojure.spec.alpha :as s)

;; or

(ns my.ns
	(:require [clojure.spec.alpha :as s]))
```

The Spec library is for specifying the 'shape' of data. Once you have the shape of the data you want, you can use the spec to validate data you receive, conform it to the spec, or generate data based on the spec.

A spec describes what values it allows, using normal Clojure predicate functions (they have to be 1-arity).

## Direct use of spec

We can check that a value conforms to a spec with the `conform` functon, which takes a spec and the thing to be tested. The below implicitly turns the `even?` into a spec

```clj
(s/conform even? 1000)
;;=> 1000

(s/conform even? 1001)
;;=> :clojure.spec.alpha/invalid
```

`valid?` just returns a boolean.

You can use any predicate you want, and you can use a set as a predicate function - will return true if the argument is a member of the set.


## Spec registry

The registry is where you declare a reusable spec. You associate a namespaced keywork with a spec, with def, then use it wherever your want.

```clj
(s/def ::suit #{:club :diamond :heart :spade})

(s/valid? ::suit :club)
;;=> true
```

Once registered you can look it up int the REPL with `(doc ::suit)`

## Building up specs by composing predicates

You can use `and` and `or`

```clj
(s/def ::big-even (s/and int? even? #(> % 1000)))
(s/def ::name-or-id (s/or :name string? :id int?))
```

Note that with the `or` you have to name the branches. This is so it can be used downstream. It's used in the `conform` return val:

```clj
(s/conform ::name-or-id "abc")
;;=> [:name "abc"]
```

Many predicates won't allow a `nil` to pass (e.g. `string?`). To make it accept a nil, use `(s/nilable string?)`

`explain` will print to `out` the reason a value failed it's spec-check

## Entity maps

To create a spec on a map you define the specs on the values and then define the spec for a map containing those values

```clj
(s/def ::accit int?)
(s/def ::first-name string?)
(s/def ::last-name string?)
(s/def ::email ::email-type) ;; where email type was `s/def`d further up

(s/def ::person (s/keys :req-un [::first-name ::last-name ::email]
                        :opt-un [::phone]))
```

(Note the `-un` on the end of req are to allow for the conformer to check for un-namespaced keywords, i.e. single-colon keywords.)

When you call conform on 
