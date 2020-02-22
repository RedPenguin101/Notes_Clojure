(ns shopping.core
  (:require [shopping.shopper :as shopper]
            [shopping.store :as store]))

(store/init {:bacon 10 :eggs 10 :sausage 10})
(shopper/go-shopping [:bacon :sausage :eggs])
