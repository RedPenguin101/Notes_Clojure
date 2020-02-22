(ns shopping.core
  (:require [shopping.shopper :as shopper]
            [shopping.store :as store]))

(store/init {:bacon 10 :eggs 10 :sausage 10})
(shopper/go-shopping [:bacon :sausage :eggs])
store/inventory
;; => #<Atom@48fa28fb: {:bacon 9, :eggs 9, :sausage 9}>
store/sold-items
;; => #<Atom@f1dc666: {:bacon 1, :sausage 1, :eggs 1}>

shopper/shopping-cart
;; => #<Ref@5512a5c6: #{:sausage :bacon :eggs}>
shopper/shopping-list
;; => #<Ref@29e05dc7: #{}>

(store/restock-all)
store/inventory
;; => #<Atom@7658c469: {:bacon 10, :eggs 10, :sausage 10}>

store/sold-items
;; => #<Atom@f1dc666: {}>
