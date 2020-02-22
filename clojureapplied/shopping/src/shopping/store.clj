(ns shopping.store)

(def inventory (atom {}))

(defn no-negative-values? [m]
  (not-any? neg? (vals m)))

(defn in-stock? [item]
  (and (pos? (item @inventory))))

(defn init [items]
  (reset! inventory items))

(defn grab [item]
  (when (in-stock? item)
    (swap! inventory update-in [item] dec)))
