(ns meander-tut.core
  (:require [meander.epsilon :as m]))

(comment 
  "the match function is traditional pattern matching."
  (m/match x
    pattern_1 expr_1
    
    pattern_n expr_n)
  "It tries to match data x against one of the patterns specified,"
  "and if some pattern i matches, it will execute expr_i")

(m/match
 {:a 123 :b 456 :c "abc"}
 
 {:a ?a :b ?b :c ?c}
 
 {:target-x ?a
  :target-y ?b
  :target-3 ?c
  :target-4 (+ ?a ?b)})

(defn favorite-food-info [foods-by-name user]
  (m/match
    ;; for data x, creates a map containing the data
   {:user user
    :foods-by-name foods-by-name}

    ;; the pattern
    ;; define source 1 and source 2 scheme, with values and how
    ;; they fit together
    {:user {:name ?name
            :favorite-food {:name ?food}}

     :foods-by-name {?food {:popularity ?popularity
                            :calories ?calories}}}

    ;; the expression to execute
    ;; define target scheme in terms of values
    {:name ?name
     :favorite {:food ?food
                :popularity ?popularity
                :calories ?calories}}))

(def foods {:nachos {:popularity :high
                     :calories :lots}
            :smoothie {:popularity :high
                       :calories :less}})

(favorite-food-info foods {:name :alice
                           :favorite-food {:name :nachos}})
;; => {:name :alice, 
;;     :favorite {:food :nachos, :popularity :high, :calories :lots}}

;; ================ widget example from 
;; http://timothypratley.blogspot.com/2019/01/meander-answer-to-map-fatigue.html?m=1


;; integrate this data

(def skynet-widgets
  [{:basic-info {:producer-code "Cyberdyne"}
    :widgets [{:widget-code "Model-101"
               :widget-type-code "t800"}
              {:widget-code "Model-102"
               :widget-type-code "t800"}
              {:widget-code "Model-201"
               :widget-type-code "t1000"}]
    :widget-types [{:widget-type-code "t800"
                    :description "Resistance Infiltrator"}
                   {:widget-type-code "t1000"
                    :description "Mimetic polyalloy"}]}
   {:basic-info {:producer-code "ACME"}
    :widgets [{:widget-code "Dynamite"
               :widget-type-code "c40"}]
    :widget-types [{:widget-type-code "c40"
                    :description "Boom!"}]}])

(m/search skynet-widgets
          (m/scan 
           {:basic-info {:producer-code ?producer-code}
            :widgets (m/scan {:widget-code ?widget-code
                              :widget-type-code ?widget-type-code})
            
            :widget-types (m/scan {:widget-type-code ?widget-type-code
                                   :description ?description})})
          
          [?producer-code ?widget-code ?description])

;; => (["Cyberdyne" "Model-101" "Resistance Infiltrator"] 
;;     ["Cyberdyne" "Model-102" "Resistance Infiltrator"] 
;;     ["Cyberdyne" "Model-201" "Mimetic polyalloy"] 
;;     ["ACME" "Dynamite" "Boom!"])


;; ================= Jimmy Miller's term rewriting tutorial
;; https://jimmyhmiller.github.io/meander-rewriting

