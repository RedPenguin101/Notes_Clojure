(ns functiondiary.core)

;; ================= 2019-12-01 ==========================

(comment
  "The number of times the number is wholly divisible
   by the divisor"
  (quot 10 3) ;; => 3
  )

;; ---------- Iterate -------------

(comment 
  "Returns a lazy sequence of x, f(x) f(f(x)) etc."
  "use when you have a function in which the input of the next
  step is the result of the preceding one"
  "Example from the AOC day 1 problem")

(defn fuel-one-level [mass]
  (max 0 (- (quot mass 3) 2)))

(defn fuel-required [mass]
  (reduce + (rest (take-while pos? (iterate fuel-one-level mass)))))

(fuel-required 849874)
;; => 424897

;; ---------- Pos? -------------

(comment "returns true if the number is positive")

(pos? 4)
;; => true
(pos? 0)
;; => false
(pos? -6)
;; => false

;; ================= 2019-12-02 ==========================

;; ---------- parseInt -------------

(comment "best way to get an integer from a string")

(Integer/parseInt "987346")
;; => 987346

;; ----------- vec vs. vector -------------

(comment "use `vec` on a collection, vector on elements")

(= (vec [1 2 3 4]) (vector 1 2 3 4))
;; => true

;; ------------------ thread -> and ->> -------------

 
(-> "foo" 
    (str "bar") 
    (str "baz"))
;; => "foobarbaz"

(->> "foo"
     (str "bar")
     (str "baz"))
;; => "bazbarfoo"

;; -------------------- mapv vs. map ----------------------

(comment "mapv returns a vector, where map returns a sequence"
         "Like map, the function passed to mapv must have the"
         "same arity as the number of collections it's given"
         (mapv f & colls))

;; --------------------- mapcat -----------------------------

(comment "like (concat (map f & colls))"
         "Use if you want to use a map on a collection of"
         "collections, then bundle the results of the maps into"
         "one long collections")

;; ================= 2019-12-03 ==========================

;; --------------------- sub -----------------------------

(comment "substring"
         (subs string start)
         (subs string start end))

(subs "hello world" 4)
;; => "o world"

(subs "hello world" 4 7)
;; => "o w"

;; --------------------- Math/abs -----------------------------

(comment "Returns the absolute value")

(Math/abs -5)
;; => 5

;; ================= 2019-12-04 ==========================

;; --------------------- > < etc-----------------------

(comment 
  "These aren't limited to two values, so you can"
  "use them to test whether x is between two values"
  (< 3 6 8)
         ;; => true
  
  "Or whether a series of numbers is sorted"
  (> 6 5 4 3 2)
         ;; => true
  
  "You can use them as a comparator for the sort function"
  (sort > [6 3 7 9 2])
         ;; => (9 7 6 3 2)
  )

;; --------------- clojure.set/intersection ---------------

(comment
  "returns a sequence of all elements of sets1 and 2 which"
  "occur in both"
  "Used it AOC2019 to find matching pairs of coordinates in"
  "two sequences of points")

(clojure.set/intersection 
 (set [[0 2] [1 2] [3 4]]) 
 (set [[4 3] [1 4] [1 2]]))
;; => #{[1 2]}

;; ------------------ partition-by -----------------------

(comment
  "Outputs a sequence of sequences grouped according to function"
  "output (i.e. element i is grouped with i+1 if f(xi) = f(xi+1))"
  "Otherwise a new group is created")

(partition-by pos? [-1 2 4 5 6 -4 -3 3 4 5])
;; => ((-1) (2 4 5 6) (-4 -3) (3 4 5))

(comment
  "someone used this on a (sorted) sequence of integers to see"
  "if any of those integers occured more than once in the seq")
 
(some #(= (count %) 2) (partition-by identity (seq "112333345")))
;; => true

;; ================= 2019-12-07 ==========================

;; ------------------ split lines -----------------------

(comment "splits a string into a seq on \\n or \\r\\n")

(clojure.string/split-lines "hello\nworld\r\nnew line")
;; => ["hello" "world" "new line"]

;; ================= 2019-12-18 ==========================

;; ------------------ Frequencies  -----------------------

(comment
  "from https://rosettacode.org/wiki/Conway's_Game_of_Life#Clojure"
  "Returns a map from distinct items in coll to the number of "
  "times they appear.")

(frequencies [:a :b :a :b :a :d])
;; => {:a 3, :b 2, :d 1}

(comment
  "cf the partition-by use case above"
  (some #(= (count %) 2) (partition-by identity (seq "112333345")))
  "The following is shorter, and has the advantage that the"
  "sequence doesn't have to be ordered (assuming that's what"
  "you're looking for)"
  "it's also more expressive of the problem statement")

(some #(> % 1) (vals (frequencies (seq "112333345"))))
;; => true

;; -------- checking if an element is in a sequence -------------

(comment
  "Initially I thought that you should use some"
  (some #(= "set" %) #{:hello :world 42 "set"})
  ;; => true

  "But actually I think you can just use a set as a function")

(if (#{:hello :world 42 "set"} "set") "In" "Not In")
;; => "In"

(if (#{:hello :world 42 "set"} "Nope") "In" "Not In")
;; => "Not In"


;; ================= 2019-12-21 ==========================

;; -------- comp for smart list filtering -------------

(comment
  "from https://lambdaisland.com/blog/2019-12-06-advent-of-parens-6-a-small-idiom"
  "Use a key-word's functionability to get something from"
  "a map, and a sets functionability for filtering"
  
  "this would be instead of"
  (filter #(= :oolong (:type %)) teas))

(def teas [{:name "Dongding" :type :oolong}
           {:name "Longjing" :type :green}
           {:name "Baozhong" :type :oolong}
           {:name "Taiwan no. 18" :type :black}
           {:name "Dayuling" :type :oolong}
           {:name "Biluochun" :type :green}])

(filter (comp #{:oolong} :type) teas)
;; => ({:name "Dongding", :type :oolong} 
;;     {:name "Baozhong", :type :oolong} 
;;     {:name "Dayuling", :type :oolong})

(filter (comp (complement #{:oolong}) :type) teas)
;; => ({:name "Longjing", :type :green} 
;;     {:name "Taiwan no. 18", :type :black} 
;;     {:name "Biluochun", :type :green})

;; -------- flattening a sequence -------------

(comment
  "Ways to flatten sequences"
  "These three are almost identical"
  )

(def nested-vec [[1 2 3] [:a :b :c]])

(apply concat nested-vec)
;; => (1 2 3 :a :b :c)

(mapcat seq nested-vec)
;; => (1 2 3 :a :b :c)

(mapcat identity nested-vec)
;; => (1 2 3 :a :b :c)

(comment "careful with flatten though, it's like the bulldozer"
         "version, it just smashes everything it finds into an"
         "un-nested seq")

(def multi-nested-vec [[:a :b :c] ["a" "b" [1 2]]])
(apply concat multi-nested-vec)
;; => (:a :b :c "a" "b" [1 2])


(flatten multi-nested-vec)
;; => (:a :b :c "a" "b" 1 2)

;; ================= 2019-12-02 ==========================
;; ------------------Merge -------------------------------

(comment "takes two maps and smooshes them together"
         "If there's a key clash, the rightmost map wins")

(merge {:a 1 :b 2 :c 3} {:b 4 :d 5})
;; => {:a 1, :b 4, :c 3, :d 5}

(comment "you can use it for over-riding default values in"
         "a configuration like scenario")

(def default-config {:setting :off :value 150})
(def set {:setting :on})
(merge default-config set)
;; => {:setting :on, :value 150}

;; ----------------------- separating out digits from an integer--------------

; turn into a string
(str 1234)
; turn into a seq of chars
(seq (str 1234))
;use Character/digit to turn a char into an integer
(Character/digit \2 10)

(map #(Character/digit % 10) (str 1234))
;; => (1 2 3 4)

; note seq cast is implicit
