(ns mypegthing.core
  (require [clojure.set :as set])
  (:gen-class))

(defn tri*
  "generates lazy sequence of triangular numbers"
  ([] (tri* 0 1))
  ([sum num]
   (let [new-sum (+ sum num)]
     (cons new-sum (lazy-seq (tri* new-sum (inc num)))))))

(def tri (tri*))

(defn tri?
  "Is the number triangular?"
  [n]
  (= n (last (take-while #(>= n %) tri))))

(defn row-tri
  "The triangular number at the end of row n"
  [n]
  (last (take n tri)))

(defn row-num
  "returns the row number the position belongs to"
  [pos]
  (inc (count (take-while #(> pos %) tri))))

(defn in-bounds?
  "Is every position less than or equal the max position?"
  [max-pos & positions]
  (>= max-pos (apply max positions)))

(defn connect
  "form mutual connections between two positions"
  [board max-pos pos neighbour destination]
  (if (in-bounds? max-pos pos neighbour destination)
    (reduce (fn
              [new-board [p1 p2]]
              (assoc-in new-board [p1 :connections p2] neighbour))
            board
            [[pos destination] [destination pos]])
    board))
