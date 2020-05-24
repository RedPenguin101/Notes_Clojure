(ns chapter7)

(def plays [{:band "Burial", :plays 979, :loved 9}
            {:band "Eno", :plays 2333, :loved 15}
            {:band "Bill Evans", :plays 979, :loved 9}
            {:band "Magma", :plays 2665, :loved 31}])

(defn loved-ratio [{:keys [plays loved]}]
  (/ loved plays))

(map (comp #(* 100 %) float loved-ratio) plays)
;; => (0.9193054400384426 0.6429489701986313 0.9193054400384426 1.163227017968893)

(sort-by loved-ratio plays)
;; => ({:band "Eno", :plays 2333, :loved 15}
;;     {:band "Burial", :plays 979, :loved 9}
;;     {:band "Bill Evans", :plays 979, :loved 9}
;;     {:band "Magma", :plays 2665, :loved 31})

(def sort-by-loved-ratio
  (partial sort-by #(/ (:loved %) (:plays %))))

(sort-by-loved-ratio plays)
;; => ({:band "Eno", :plays 2333, :loved 15}
;;     {:band "Burial", :plays 979, :loved 9}
;;     {:band "Bill Evans", :plays 979, :loved 9}
;;     {:band "Magma", :plays 2665, :loved 31})

(defn columns
  "Given a vector of column headers, returns a function which when applied to a row (a map with keys = column headers) returns a vector of the row values corresponding to the headers"
  [column-headers]
  (fn [row]
    (mapv row column-headers)))

((columns [:band :plays :loved]) (plays 0))
;; => ["Burial" 979 9]

"When used with "

(sort-by (columns [:plays :loved :band]) plays)
;; => ({:band "Bill Evans", :plays 979, :loved 9}
;;     {:band "Burial", :plays 979, :loved 9}
;;     {:band "Eno", :plays 2333, :loved 15}
;;     {:band "Magma", :plays 2665, :loved 31})

"This works because tuples sort on element0, then element1 etc."

(mapv (columns [:plays :loved :band]) plays);; => [[979 9 "Burial"] [2333 15 "Eno"] [979 9 "Bill Evans"] [2665 31 "Magma"]]

"NAMED ARGUMENTS with & and destructuring"

(defn slope "given up to two points :p1 and :p2, returns the slope of the line connecting the two points"
  [& {:keys [p1 p2] :or {p1 [0 0] p2 [1 1]}}]
  (let [[x1 y1] p1
        [x2 y2] p2]
    (float (/ (- y1 y2)
              (- x1 x2)))))

(slope :p2 [2 1]);; => 0.5
