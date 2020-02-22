(ns clojureapplied.chapter2
  (:require [medley.core :refer [map-keys map-vals]]
            [clojure.walk]))

(comment "Chapter 2: Collect and organise your data
         * The right collection
         * Updating collections
         * Accessing collections
         * Custom collections")

(comment
  "list, vectors, sets and maps.
  
  Which one to choose? Depends on the data. Use cases for map and set
  are pretty obvious. The sequential collections (list and vec), less so
  
  To choose between them:
  * where do you want data to be added? Front? list. Back? vector
  * do you need to look up by index in the sequence? if yes, vec
  * stack? list. you get peek, pop, and push (cons)
  
  Note clojure maps and set are unsorted, but they come in sorted
  flavours too, based on the result of a comparator.
  a map comparator applies to keys, and a sorted set will remove
  one of two elements whose comparator returns the same value
  
  You can super-cleverly use the juxt function to build up complicated
  comparators
  
  see here for more on comparators
  https://github.com/jafingerhut/thalia/blob/master/doc/other-topics/comparators.md")

(defn compare-author [s1 s2]
  (letfn [(project-author [author]
          ((juxt :lname :fname) author))]
    (compare (project-author s1) (project-author s2))))

(sorted-set-by compare-author
               {:fname "Jeff" :lname "Smith"}
               {:fname "Bill" :lname "Smith"})


(comment "Updating collections")

(comment
  "Not literal update because immutable. But term is used for convenience
  Immutability leads to separation of domain logic from state management.
  
  FIFO processing and queues")

(defn new-orders [] [])

(defn add-order [order orders]
  (conj orders order))

(defn cook [order]
  ,,,)

(defn cook-order [orders]
  (cook (first orders)) (rest orders))

(comment
  "add order is efficient for a vector, but rest is not. If we were to
  use a list instead the opposite would be true: rest would be efficient
  and add-order (now using `(concat orders (list order))` would be 
  inefficient
  
  We can use an actual queue to suit our needs")

(def new-orders clojure.lang.PersistentQueue/EMPTY)

(defn cook-order [orders]
  (cook (peek orders))
  (pop orders))

(comment
  "Bulk import
  
  When you have a bunch of stuff you want to put in a collection, just
  conjing things one at a time is fairly inefficient (though the persistent
  data structures do help with this). You can use controlled mutability
  to import data in bulk, with transient versions of base collections.")

;; no mutability
(defn import-catalog [data]
  (reduce #(conj %1 %2) [] data))

;; with mutability - persistent! here 'locks' the transient collection 
;; into immutability
(defn import-catalog-fast [data]
  (persistent!
    (reduce #(conj! %1 %2) (transient []) data)))

(comment
  "Elements in maps are often updated. The basic tools are assoc and
  dissoc, as well as update, which applies a function to a value")

(def earth {:name "Earth"
            :moons 1
            ; other stuff from before
            })

(update earth :moons inc)

(comment
  "Clojure doesn't have a lot of core functions for updating multiple
  things (at least when this book was written, believe .walk has
  introduced some). We'll use Medley here - map-keys to update all keys
  map-vals to update all values")

(def earth-json {"name" "Earth"
                 "moons" 1
                 "volume" 1.08e12
                 "mass" 5.972e24
                 "aphelion" 152
                 "perihelion" 147})

(defn keywordize-entity [entity]
  (map-keys keyword entity))

(keywordize-entity earth-json)

;; note you can do this specific example with walk now

(clojure.walk/keywordize-keys earth-json)

(defn compute-calories [recipe]
  100)

(defn update-calories [recipe]
  (assoc recipe :calories (compute-calories recipe)))

(defn include-calories [recipes]
  (map-vals update-calories recipes))

(comment
  "Medley also has filter-keys, filter-vals, remove-keys, remove-vals")

(comment "Accessing collections")

(comment
  "Maps and vectors you can get stuff by index - the key and position
  respectively.
  
  You can use get, use the collection as a function with arg index
  or if the index is a keyword, use the keyword as a function with arg
  collection")

(get earth :name) ;; may be clearer in some cases
(earth :name)
(:name earth) ;; preferred method - works on records too.

(comment
  "select keys gets the vals of several keys and returns a map")

(select-keys earth [:name :moons])

(comment "Sequential Search")

(comment 
  "Commonly, people use some function to search")

(def units [:lb :oz :kg])

(some #{:oz} units)
;; returns :oz, the first logically true element found

(comment "building custom collections")

(comment
  "editorialising, but I think don't do actually do this. Interesting
  stuff about collection traits
  
  Collections implement traits. There are predicate functions that detect
  whether a collection implments a trait, like:
    counted? sequential? associative? reversible? sorted?
  These correspond to whether the underlying collection implements
  a sepcific java interface.")
