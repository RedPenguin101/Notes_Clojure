(ns fwpd.core)
(def filename "suspects.csv")

(def vamp-keys [:name :glitter-index])
; a vector of keys

(defn str->int
  "coerces a string to an integer"
  [str]
  (Integer. str))

(def conversions 
  {:name identity
   :glitter-index str->int})
; a map which associates a conversion function with each of the vamp keys
; (identity means there's no conversion)

(defn convert
  "takes a key and a value (as a string) and returns the converted value as an integer"
  [vamp-key value]
  ((get conversions vamp-key) value))
  ; (get conversions vamp-key) evals to the vamp key in the conversion

(defn parse
  "convert a csv into row of columns"
  [string]
  (map #(clojure.string/split % #",")
    (clojure.string/split string #"\n")))

(defn mapify
  "return a seq of maps like {:name \"Edward Cullen\" :glitter-index 10}"
  [rows]
  (map 
    (fn [unmapped-row]
      (reduce 
        (fn 
          ; 
          [row-map [vamp-key value]] ; takes a vector of keys and values
          (assoc row-map vamp-key (convert vamp-key value))) ; turns the value to an integer 
        {}
        (map vector vamp-keys unmapped-row))) ; zips up parsed row from csv into a list of vectors
    rows))