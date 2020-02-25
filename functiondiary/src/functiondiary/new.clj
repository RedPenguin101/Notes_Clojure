(ns functiondiary.new)

(merge-with into
            {:Lisp ["Common Lisp" "Clojure"]
             :ML ["Caml" "Objective Caml"]}
            {:Lisp ["Scheme"]
             :ML ["Standard ML"]})

(def to-test [1 2 3 4 5 -6])

(some neg? to-test)
(not (not-any? neg? to-test))