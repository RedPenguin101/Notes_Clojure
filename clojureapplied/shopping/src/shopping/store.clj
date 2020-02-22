(ns shopping.store)

(def inventory (atom {}))
(def sold-items (atom {}))

(defn no-negative-values? [m]
  (not-any? neg? (vals m)))

(defn in-stock? [item]
  (and (pos? (item @inventory))))

(defn grab [item]
  (when (in-stock? item)
    (swap! inventory update-in [item] dec)))

 (defn restock-order [k r ov nv]
   (doseq [item (for [kw (keys ov)
                      :when (not= (kw ov) (kw nv))]
                  kw)]
     (swap! sold-items update-in [item] (fnil inc 0))
     (println "need to restock" item)))

(defn init [m]
  (reset! inventory m)
  (reset! sold-items {})
  (set-validator! inventory no-negative-values?)
  (add-watch inventory :restock restock-order))

(defn restock-all []
  (swap! inventory #(merge-with + % @sold-items))
  (reset! sold-items {}))

(comment
  (init {:apples 1 :bacon 3 :milk 2})
  (grab :bacon)
  (grab :bacon)
  (grab :milk)
  sold-items
  (restock-all)
  inventory
  sold-items
  )