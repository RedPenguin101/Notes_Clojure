(ns shopping.shopper
  (:require [shopping.store :as store]
            [clojure.core.async :refer [<!! >!! <! >! chan go go-loop]]))

(def shopping-list (ref #{}))
(def shopping-cart (ref #{}))
(def assignments (ref {}))
(def my-kids #{:alice :bobby :cindi})

(defn init [items]
  (dosync
   (ref-set shopping-list items)
   (ref-set shopping-cart #{})
   (ref-set assignments {})))

(defn assignment [child]
  (get @assignments child))

(defn dawdle []
  (let [t (rand-int 5000)]
    (Thread/sleep t)))

(defn assign-item-to-child [child]
  (dosync 
   (let [item (first @shopping-list)]
     (alter shopping-list disj item)
     (alter assignments assoc child item)
     item)))

(defn collect-item-from-child [child]
  (dosync (let [item (assignment child)]
            (alter assignments dissoc child)
            (alter shopping-cart conj item)
            item)))

(defn send-child-for-item [child item queue]
  (println child "is searching for" item)
  (dawdle)
  (store/grab item)
  (collect-item-from-child child)
  (>!! queue child))

(defn go-shopping [items]
  (init (set items))
  (let [kids (chan 10)]
    (doseq [k my-kids]
      (>!! kids k))
    (go-loop [kid (<! kids)]
      (if (seq @shopping-list)
        (do (go (send-child-for-item kid (assign-item-to-child kid) kids))
            (recur (<! kids)))
        (println "done shopping")))))

(comment
  (store/init {:bacon 10 :eggs 10 :sausage 10})
  (go-shopping #{:bacon :eggs :sausage})
  shopping-cart
  ;; => #<Ref@62726392: #{:sausage :bacon :eggs}>
  shopping-list
  ;; => #<Ref@145a76a1: #{}>
  assignments
  ;; => #ref[{:status :ready, :val {}} 0x2650f708]

  @store/inventory
  ;; => {:bacon 9, :eggs 9, :sausage 9}

  )
