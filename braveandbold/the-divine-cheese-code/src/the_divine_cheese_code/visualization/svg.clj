(ns the-divine-cheese-code.visualization.svg
  (:require [clojure.string :as s])
  (:refer-clojure :exclude [min max]))

(defn latlng->point
  "convert lat/lng map to comma separated string"
  [latlng]
  (str (:lat latlng) "," (:lng latlng)))

(defn points
  [locations]
  (clojure.string/join " " (map latlng->point locations)))

(defn comparator-over-maps
  "Returns a function that applies a comparison over pairwise
  keys in maps and for each pair returns the result of the
  comparison"
  [comparison-fn keys]
  (fn [maps]
    (zipmap keys
            (map #(apply comparison-fn (map % maps)) keys))))

(def min (comparator-over-maps clojure.core/min [:lat :lng]))
(def max (comparator-over-maps clojure.core/max [:lat :lng]))

(defn translate-to-00
  "Shifts a series of locations such that the minimum x and y points are
  translated to (0, 0)"
  [locations]
  (let [minimums (min locations)]
    (map #(merge-with - % minimums) locations)))

(defn scale
  "Given a list of locations, and a desired height and width, returns
  return a list of locations scaled by an appropriate ratio to fit those
  dimensions"
  [width height locations]
  (let [maxcoords (max locations)
        ratio {:lat (/ height (:lat maxcoords))
               :lng (/ width (:lng maxcoords))}]
    (map #(merge-with * % ratio) locations)))

(defn line
  [points]
  (str "<polyline points=\"" points "\" />"))

(defn transform
  [width height locations]
  (->> locations
       translate-to-00
       (scale width height)))

(defn xml
  [width height locations]
  (str "<svg height=\"" height "\" width=\"" width "\">"
       "<g transform=\"translate(0," height ")\">"
       "<g transform=\"scale(1,-1)\">"
       (-> (transform width height locations)
           points
           line)
       "</g></g>"
       "</svg>"))
