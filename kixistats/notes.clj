(ns notes
  (:require [kixi.stats.core :as kixi]
            [clojure.set :as set]
            [kixi.stats.distribution :as distr]
            [clojure.math.combinatorics :as combo]))

"Notes on usage of the kixi.stats library"

(distr/draw (distr/binomial {:n 100 :p 0.5}))
;; => 58

(distr/sample 10 (distr/binomial {:n 100 :p 0.5}))
;; => (50 43 41 43 50 48 44 52 45 53)

(distr/sample-summary 1000 (distr/bernoulli {:p 0.3}))
;; => {true 285, false 715}

"Exercises and Examples from Baysian Statistics the Fun Way"

"Chapter 2: What is the probablity of rolling 2 six-sided dice and getting a value greater than seven?"

"What are the parameters of a Bin distr for the prob of rolling either a 1 or a 20 on a 20 sided die if we roll 12 times? X ~ B(1, 12, 2/20)"

(defn factorial [n]
  (if (= n 1)
    1
    (* n (factorial (- n 1)))))

(factorial 5)

(defn choose [n k]
  (combo/count-combinations (range n) k))

(choose 3 2)

(defn binomial-pmf [p n k]
  (* (choose n k) (Math/pow p k) (Math/pow (- 1 p) (- n k))))

"prob of getting k heads in 10 flips of a coin"
(map (partial binomial-pmf 0.5 10) (range 0 11))

"Prob of getting a 6 when rolling a 6 sided die 10 times"

(map (partial binomial-pmf 1/6 10) (range 0 11))

(binomial-pmf 0.2 12 4)

(defn binomial-cpf [p n k]
  (reduce + (map (partial binomial-pmf p n) (range 0 (inc k)))))

(binomial-cpf 0.2 12 4)

(- 1 (binomial-cpf 0.0072 100 0))

(defn beta-pdf [p alpha beta]
  (/ (* (Math/pow p (- alpha 1)) (Math/pow (- 1 p) (- beta 1)))
     (/ (+ alpha beta) (* alpha beta))))

(defn )
"p - probablity, CDF (cdf)
 q - quantile, reverse cdf (quantile)
 d - density PDF
 r - random, sample from distribution (draw, or sample)"

(distr/cdf (distr/normal {:mu 50 :sd 20}) 27.4)
;; => 0.1292381122400178

(distr/quantile (distr/normal {:mu 50 :sd 20}) 0.129238)
;; => 27.39998934525403
