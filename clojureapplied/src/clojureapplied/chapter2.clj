(ns clojureapplied.chapter2)

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
