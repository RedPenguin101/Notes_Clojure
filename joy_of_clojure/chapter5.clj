(ns chapter5)

"Neighbour Function:
Given a y-x loation in a square 2d matrix, returns a sequence of the locations surrounding it"

"the neighbours look like this relative to the point being looked at"
(def n [[-1 0] [1 0] [0 -1] [0 1]])

"to get the points that are neigbours of the y-x location, just map with addition"
(mapv #(mapv + [1 2] %) n)
;; => [[0 2] [2 2] [1 1] [1 3]]
(def points (mapv #(mapv + [1 2] %) n))

"any tuple which contains a value that is < 0 or >= size is invalid
Or said another way, a point is valid if every element is (< -1 element size)"

(every? #(< -1 % 3) [-1 2])
;; => false
(every? #(< -1 % 3) [1 2])
;; => true
(every? #(< -1 % 3) [1 3])
;; => false

"so we want to filter out any points that are not valid"

(filter (fn [point] (every? #(< -1 % 3) point)) points)

(defn neighbours
  "Given a y-x location in a square 2d matrix, returns a sequence of the locations surrounding it"
  [size yx]
  (filter (fn valid-point? [point] (every? #(< -1 % size) point))
          (mapv #(mapv + yx %) [[-1 0] [1 0] [0 -1] [0 1]])))
(neighbours 3 [1 2])
;; => ([0 2] [2 2] [1 1])

"threaded version"
(defn neighbours2
  "Given a y-x location in a square 2d matrix, returns a sequence of the locations surrounding it"
  [size yx]
  (->> [[-1 0] [1 0] [0 -1] [0 1]]
       (mapv #(mapv + yx %))
       (filter (fn valid-point? [point] (every? #(< -1 % size) point)))))

(neighbours2 3 [1 2])
;; => ([0 2] [2 2] [1 1])

"version using transducers"
(defn neighbours3
  "Given a y-x location in a square 2d matrix, returns a sequence of the locations surrounding it"
  [size yx]
  (into [] (comp (map #(mapv + yx %))
                 (filter (fn valid-point? [point] (every? #(< -1 % size) point))))
        [[-1 0] [1 0] [0 -1] [0 1]]))

(neighbours3 3 [1 2])
;; => [[0 2] [2 2] [1 1]]

"higher order transducerized - now a generic find relatives of a point within a matrix"
(defn find-relatives [size]
  (fn [yx]
    (comp (map #(mapv + yx %))
          (filter (fn valid-point? [point] (every? #(< -1 % size) point))))))

(into [] ((find-relatives 3) [1 2]) n)
;; => [[0 2] [2 2] [1 1]]



"Counted"
(counted? (apply list (range 10)));; => true
(counted? (vec (range 10)));; => true
(counted? (seq (range 10)));; => true
(counted? (lazy-seq (range 10)));; => false
(counted? (into-array (range 10)));; => false

(into #{[] #{} {}} [()]);; => #{[] #{} {}}

