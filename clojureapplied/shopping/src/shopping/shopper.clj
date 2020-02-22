(ns shopping.shopper)

(defn go-shopping
  [shopping-list]
  (loop [[item & items] shopping-list
         cart []]
    (if item
      (recur items (conj cart item))
      cart)))

(go-shopping [:bacon :eggs :sausage])