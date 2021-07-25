(ns qui-tut
  (:require [quil.core :as q]))

(def world-size 10)
(def cell-size 50)
(def border-size 5)
(def canvas-size
  (+ (* world-size (+ cell-size border-size)) border-size))

(defn draw [state]
  (q/no-stroke)
  (doseq [x (range 0 canvas-size (+ border-size cell-size))
          y (range 0 canvas-size (+ border-size cell-size))]

    (q/fill (rand-int 256))
    (q/rect (+ x border-size)
            (+ y border-size)
            cell-size
            cell-size)))

(defn update-canvas [state]
  )

(defn setup []
  (q/background 255)
  (q/frame-rate 10)
  [])

(comment
  (q/defsketch x :size 
               [canvas-size canvas-size] 
               :draw draw
               :setup setup)
  
  1)