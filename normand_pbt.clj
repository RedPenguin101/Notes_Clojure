(ns normand-pbt
  (:require [clojure.test :refer :all]
            [clojure.string :as str]
            [clojure.test.check.clojure-test :refer [defspec]]
            [clojure.test.check.properties :as prop]
            [clojure.test.check.generators :as gen]))

(str/upper-case "abcd")

(defspec length-doesnt-change 1000
  (prop/for-all
   [s gen/string-ascii]
   (= (count s) (count (str/upper-case s)))))

(defspec all-chars-uppercase
  (prop/for-all
   [s gen/string-ascii]
   (every? #(if (Character/isLetter %) 
              (Character/isUpperCase %)
              true) (str/upper-case s))))

(defspec idempotency
  (prop/for-all
   [s gen/string-ascii]
   (= (str/upper-case s)
      (str/upper-case (str/upper-case s)))))

(comment

  (defspec always-passes 
    100
    (prop/for-all [] true))

  (defspec always-fails 100
    (prop/for-all [] false))
  
  (remove-ns 'normand-pbt))

;; Matrix

(gen/sample (gen/bind (gen/tuple (gen/fmap inc gen/nat)
                                 (gen/fmap inc gen/nat))
                      (fn [[x y]] (gen/vector 
                                   (gen/vector gen/nat x)
                                   y))))

(gen/sample
 (gen/let
  [[m n] (gen/tuple
          (gen/fmap inc gen/nat)
          (gen/fmap inc gen/nat))]
   (gen/vector
    (gen/vector gen/nat m)
    n)))


(defn merge-en [l1 l2]
  (lazy-seq
   (cond
     (empty? l1)
     l2

     (empty? l2)
     l1

     (< (first l1) (first l2))
     (cons (first l1) (merge (rest l1) l2))

     :else
     (cons (first l2) (merge l1 (rest l2))))))

(defn mergesort* [v]
  (case (count v)
    0 ()
    1 (seq v)

    ;; else
    (let [half (quot (count v) 2)]
      (merge-en (mergesort* (subvec v 0 half))
                (mergesort* (subvec v half))))))

(defn mergesort [ls]
  (seq (mergesort* (vec ls))))

#_(defspec sort-with-model 100
  (prop/for-all
   [numbers (gen/vector gen/large-integer)]
   (= (sort numbers) (mergesort numbers))))

;; fails with the empty list - mergesort returns nil

(defn mergesort [ls]
  (mergesort* (vec ls)))

(defspec distinct-number-of-items
  (prop/for-all
   [xs (gen/vector (gen/choose 0 4))]
   (= (count (distinct xs)) (count (set xs)))))

(defspec merge-commutative
  (prop/for-all
   [n1 (gen/map gen/keyword (gen/choose 0 9))
    n2 (gen/map gen/keyword (gen/choose 0 9))]
   (if (empty? (clojure.set/intersection (set (keys n1))
                                         (set (keys n2))))
     (= (merge n1 n2)
        (merge n1 n2))
     true)))
