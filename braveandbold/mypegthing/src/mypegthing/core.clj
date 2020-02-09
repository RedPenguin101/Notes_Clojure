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

(defn connect-right
  [board max-pos pos]
  (let [neighbour (inc pos)
        destination (inc neighbour)]
    (if-not (or (tri? pos) (tri? neighbour))
      (connect board max-pos pos neighbour destination)
      board)))

(defn connect-down-left
  [board max-pos pos]
  (let [row (row-num pos)
        neighbour (+ pos row)
        destination (+ pos row)]
    (connect board max-pos pos neighbour destination)))

(defn connect-down-right
  [board max-pos pos]
  (let [row (row-num pos)
        neighbour (+ 1 pos row)
        destination (+ 1 pos row)]
    (connect board max-pos pos neighbour destination)))

(defn add-pos
  "Pegs the position and performs connections"
  [board max-pos pos]
  (let [pegged-board (assoc-in board [pos :pegged] true)]
    (reduce (fn [new-board connector] (connector new-board max-pos pos))
            pegged-board
            [connect-right connect-down-left connect-down-right])))

(defn new-board [rows]
  (let [initial-board {:rows rows}
        max-pos (row-tri rows)]
    (reduce (fn [board pos] (add-pos board max-pos pos))
            initial-board
            (range 1 (inc max-pos)))))

(defn pegged?
  [board pos]
  (get-in board [pos :pegged]))

(defn valid-moves
  "Returns a maps of all valid moves to pos, where key is destination
  and value is jumped positions"
  [board pos]
  (into {}
        (filter (fn [[destination jumped]]
                  (and (not (pegged? board destination))
                       (pegged? board jumped)))
                (get-in board [pos :connections]))))

(defn valid-move?
  "return jumped pos if move from p1 to p2 is valid"
  [board p1 p2]
  (get (valid-moves board p1) p2))

(defn remove-peg
  [board pos]
  (assoc-in board [pos :pegged] false))

(defn place-peg
  [board pos]
  (assoc-in board [pos :pegged] true))

(defn move-peg
  [board p1 p2]
  (place-peg (remove-peg board p1) p2))

(defn make-move
  [board p1 p2]
  (if-let [jumped (valid-move? board p1 p2)]
    (move-peg (remove-peg board jumped) p1 p2)))

(defn can-move?
  [board]
  (some
   (comp not-empty (partial valid-moves board))
   (map first (filter #(get (second %) :pegged) board))))

; filter 

;; board struct looks like
;; {1: {:pegged true :connections {6 3, 4 2}}
;;  :rows 5}
