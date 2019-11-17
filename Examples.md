## symmetrize example
```clj
(def matching-part
	[part]
	{:name (clojure.string/replace (:name part) #"^left-" "right-")
	:size (:size part)})

(def symmetrize-body-parts
	"Expects an array of maps that have a :name and :size"
	[asym-body-parts]
	(loop [remaining-asym-parts asym-body-parts
		   final-body-parts []]
		(if (empty? remaining-asym-parts)
			final-body-parts
			(let [[part & remaining] remaining-asym-parts]
				(recur remaining
					(into final-body-parts
					; add set into vector fbp
						(set [part (matching-part part)])))))))
						; create a set of part and matching part (set so only take uniques)
```

* this represents a common pattern: a sequence is split into a _head_ and _tail_ (rest). The head is processed, added to a result (final-body parts) and the operation is recursed on the tail.
* the loop binds rap to the input value, and the result sequence fpb to an empty vector.
* if rap is empty, return final body parts
* otherwise split the head and tail
* recur with the tail as rap and the fpb as the part and its match pushed into what you started with
* can be simplified with `reduce`


```clj
(defn symmetrize
	[parts]
	(reduce 
		(fn [final-parts part]
			(into 
				final-parts 
				(set [part (matching-part part)])))
		[]
		parts))
```

## hit example
```clj
(defn hit
	[parts]

	(let [sym-parts (symmetrize parts)
		  total-size-of-parts (reduce + (map :size sym-parts))
		  target (rand total-size-of-parts)]
		(loop [[part & remaining] sym-parts
			   accumulated-size (:size part)]
			(if (> accumulated-size target)
				part
				(recur 
					remaining 
					(+ accumulated-size (:size (first remaining))))))))
```

* as reduce (don't know if this works, not even much shorter :( )

```clj
(def hit2
    [parts]
    
    (let [sym-parts (symmetrize parts)
          total-size-of-parts (reduce + (map :size sym-parts))
          target (rand total-size-of-parts)]
        (reduce 
            (fn [accumulator part]
                (if (> accumulator target)
                    (accumulator)
                    (+ accumulator (:size part)))) 
            sym-parts)))
```