(defn mapset [function sequence]
    (set (map function sequence)))

(defn radiate-parts
    [part number]
    (loop [final-parts (vector part)
           iteration 2]
        (if (> iteration number)
            final-parts
            (recur 
                (into final-parts 
                    [{:name (clojure.string/replace (:name part) #"1$" (str iteration)) 
                    :size (:size part)}])
                (inc iteration)))))

(radiate-parts "head1" 5)