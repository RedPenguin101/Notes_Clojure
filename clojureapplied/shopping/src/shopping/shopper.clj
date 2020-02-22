(ns shopping.shopper
  (:require [shopping.store :as store]))

(def shopping-list (ref #{}))
(def shopping-cart (ref #{}))

(defn init [items]
  (dosync
   (ref-set shopping-list items)
   (ref-set shopping-cart #{})))

(defn shop-for-item [item]
  (when (store/grab item)
    (dosync (alter shopping-list disj item)
            (alter shopping-cart conj item))))

(defn go-shopping [items]
  (init (set items))
  (map shop-for-item @shopping-list))

(comment
  (store/init {:bacon 10 :eggs 10 :sausage 10})
  (go-shopping #{:bacon :eggs :sausage})
  shopping-cart
  ;; => #<Ref@62726392: #{:sausage :bacon :eggs}>

  shopping-list
  ;; => #<Ref@145a76a1: #{}>
)
