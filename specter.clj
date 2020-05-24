(ns specter
  (:require [com.rpl.specter :refer :all]))

"https://www.youtube.com/watch?v=rh5J4vacG98"

"Two of the weaknesses of clojure:
1. often doesn't maintain data-structure - things get turned into lazy seq"

(map inc [1 2 3 4])
;; => (2 3 4 5)

(transform ALL inc [1 2 3 4])
;; => [2 3 4 5]
(transform ALL inc #{1 2 3 4})
;; => #{4 3 2 5}

"2. nested data structures get messy to xform with core library."

(def data [{:a 1 :b 2} {:c 3} {:d 3}])

"How would we increment the even values using the core lib?"

(defn apply-fn-to-map [f m]
  (into {} (for [[k v] m] [k (f v)])))

(defn inc-even? [n]
  (if (even? n)
    (inc n)
    n))

(mapv #(apply-fn-to-map inc-even? %) data)
;; => [{:a 1, :b 3} {:c 3} {:d 3}]

"Lots of code!"

(transform [ALL MAP-VALS even?] inc data)
;; => [{:a 1, :b 3} {:c 3} {:d 3}]

"the values in the vector are Navigators, which you compose together to make a Path to what you want. In this case:
For ALL values in the vector, for MAP-VALS, where values are even, increment.
You can see this navigation in steps like this"

:START
[{:a 1 :b 2} {:c 3} {:d 3}]

:ALL
{:a 1 :b 2}
{:c 3}
{:d 3}

:MAP-VALS
1
2
3
4

:even?
2
4

"Then it applies the function and 'reconstructs' back to your original data structure"

"the syntax is similar to get in"

(get-in {:a {:b {:c 1}}} [:a :b :c])
;; => 1

"A few examples / use cases"

"query with select"

(def data {:timestamp 123
           :res       [{:group   1
                        :catlist [{:cat    1
                                   :start  "none"
                                   :stop   "none"
                                   :points [{:point 1
                                             :start "13.00"
                                             :stop  "13.35"}
                                            {:point 2
                                             :start "11.00"
                                             :stop  "14.35"}]}
                                  {:cat    2
                                   :start  "none"
                                   :stop   "none"
                                   :points [{:point 1
                                             :start "09.00"
                                             :stop  "10.35"}
                                            {:point 2
                                             :start "11.00"
                                             :stop  "12.35"}]}]}
                       {:group   2
                        :catlist [{:cat    1
                                   :start  "none"
                                   :stop   "none"
                                   :points [{:point 1
                                             :start "08.00"
                                             :stop  "08.35"}
                                            {:point 2
                                             :start "16.00"
                                             :stop  "17.35"}]}]}]})

(select [:res ALL :catlist ALL :points ALL (submap [:start :stop])] data);; => [{:start "13.00", :stop "13.35"} {:start "11.00", :stop "14.35"} {:start "09.00", :stop "10.35"} {:start "11.00", :stop "12.35"} {:start "08.00", :stop "08.35"} {:start "16.00", :stop "17.35"}]

"update with transform - change the order of the points based on the stop time (in this case, reversing all the points, so all the point 2's now come first)"

(transform [:res ALL :catlist ALL :points]
           #(reverse (sort-by :stop %))
           data)
;; => {:timestamp 123, :res [{:group 1, :catlist [{:cat 1, :start "none", :stop "none", :points ({:point 2, :start "11.00", :stop "14.35"} {:point 1, :start "13.00", :stop "13.35"})} {:cat 2, :start "none", :stop "none", :points ({:point 2, :start "11.00", :stop "12.35"} {:point 1, :start "09.00", :stop "10.35"})}]} {:group 2, :catlist [{:cat 1, :start "none", :stop "none", :points ({:point 2, :start "16.00", :stop "17.35"} {:point 1, :start "08.00", :stop "08.35"})}]}]}

"this has navigated to the following"

[{:point 1, :start "13.00", :stop "13.35"}
 {:point 2, :start "11.00", :stop "14.35"}]
[{:point 1, :start "09.00", :stop "10.35"}
 {:point 2, :start "11.00", :stop "12.35"}]
[{:point 1, :start "08.00", :stop "08.35"}
 {:point 2, :start "16.00", :stop "17.35"}]

"and within each of the vectors, has applies the sort function provided"

[{:point 2, :start "11.00", :stop "14.35"}
 {:point 1, :start "13.00", :stop "13.35"}]
[{:point 2, :start "11.00", :stop "12.35"}
 {:point 1, :start "09.00", :stop "10.35"}]
[{:point 2, :start "16.00", :stop "17.35"}
 {:point 1, :start "08.00", :stop "08.35"}]

"and then again, reconstructed"

"A common case, you want to navigate in and change a value"

(setval [:timestamp] "hello world" data)
;; => {:timestamp "hello world" ...

"This also works on a collection of values"

(setval [:res FIRST :catlist FIRST :points FIRST] {:point "X"} data)
;; => {:timestamp 123, :res [{:group 1, :catlist [{:cat 1, :start "none", :stop "none", :points [{:point "X"}

(setval [:res FIRST :catlist FIRST :points (nthpath 1)] {:point "X"} data)
;; => {:timestamp 123, :res [{:group 1, :catlist [{:cat 1, :start "none", :stop "none", :points [{:point 1, :start "13.00", :stop "13.35"} {:point "X"}]} ...

"inserting a new value in a vector"

(setval [:res FIRST :catlist FIRST :points AFTER-ELEM] {:point 3 :stuff "bleh"} data)
;; => {:timestamp 123, :res [{:group 1, :catlist [{:cat 1, :start "none", :stop "none", :points [{:point 1, :start "13.00", :stop "13.35"} {:point 2, :start "11.00", :stop "14.35"} {:point 3, :stuff "bleh"}]} ...

(setval [:res FIRST :catlist FIRST :points (before-index 1)] {:point 3 :stuff "bleh"} data);; => {:timestamp 123, :res [{:group 1, :catlist [{:cat 1, :start "none", :stop "none", :points [{:point 1, :start "13.00", :stop "13.35"} {:point 3, :stuff "bleh"} {:point 2, :start "11.00", :stop "14.35"}]} ...

"Walker for data structures"
(select [(walker number?)] data)
;; => [123 1 1 1 2 2 1 2 2 1 1 2]
(select [(walker string?)] data)
;; => ["none" "none" "13.00" "13.35" "11.00" "14.35" "none" "none" "09.00" "10.35" "11.00" "12.35" "none" "none" "08.00" "08.35" "16.00" "17.35"]

"Rounds down all stop times to the nearest hour"
(defn parse [time] (clojure.string/split time #"\."))
(defn unparse [split-time] (clojure.string/join "." split-time))
(unparse (parse "10.32"))

(select [:res ALL :catlist ALL :points ALL :stop (parser parse unparse) LAST]
        data)
;; => ["35" "35" "35" "35" "35" "35"]

(setval [:res ALL :catlist ALL :points ALL :stop
         (parser parse unparse) LAST]
        "00"
        data)
;; => {:timestamp 123, :res [{:group 1, :catlist [{:cat 1, :start "none", :stop "none", :points [{:point 1, :start "13.00", :stop "13.00"} {:point 2, :start "11.00", :stop "14.00"}]} {:cat 2, :start "none", :stop "none", :points [{:point 1, :start "09.00", :stop "10.00"} {:point 2, :start "11.00", :stop "12.00"}]}]} {:group 2, :catlist [{:cat 1, :start "none", :stop "none", :points [{:point 1, :start "08.00", :stop "08.00"} {:point 2, :start "16.00", :stop "17.00"}]}]}]}
