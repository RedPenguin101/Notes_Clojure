(ns shopping.shopper
  (:require [shopping.store :as store]))

(defn shop-for-item [cart item]
  (if (store/grab item)
    (conj cart item)
    cart))

(defn go-shopping [shopping-list]
  (reduce shop-for-item [] shopping-list))

(comment 
  (store/init {:bacon 10 :eggs 10 :sausage 10})
  (go-shopping [:bacon :eggs :sausage]))
