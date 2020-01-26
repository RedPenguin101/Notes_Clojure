(ns meander-tut.core
  (:require [meander.epsilon :as m]))

(comment
  "Meander is a clojure library for declarative data transformation.
  It's goal is to make the inputs and outputs of data transformations
  clear to the person reading the code.
  
  This document will talk about:
  * Match: the simplest function: give it data, a pattern, and expression
    and it will match the data on the pattern, and execute the expression
  * Search and Scan: given a collection of things, search through it, scanning
    for some pattern, and execute an expression
  * Find: when you want to remember thing, use find")

;; ======================= MATCH ============================

(comment 
  "the match function is traditional pattern matching."
  (m/match x
    pattern_1 expr_1
    
    pattern_n expr_n)
  "It tries to match data x against one of the patterns specified,"
  "and if some pattern i matches, it will execute expr_i")

(m/match
 {:a 123 :b 456 :c "abc"} ;; the data
 
 {:a ?a :b ?b :c ?c} ;; the pattern to match against
 
 {:target-1 ?a ;; the expression to evaluate
  :target-2 ?b
  :target-3 ?c
  :target-4 (+ ?a ?b)})

;; match example from the official site

(def user  {:name :alice :favorite-food {:name :nachos}})

(def foods {:nachos {:popularity :high
                     :calories :lots}
            :smoothie {:popularity :high
                       :calories :less}})

(defn favorite-food-info [foods-by-name user]
  (m/match
    ;; a common pattern: put input data into a single structure
    {:user user
     :foods-by-name foods-by-name}

    ;; this part just describes the structure of the input data
    ;; i.e. the pattern to match against.
    {:user {:name ?name
            :favorite-food {:name ?food}}

     ;; the 2nd use of `?food` here creates an implicit join
     ;; i.e. the `?food` here will be the same as the one in the
     ;; 1st usage.
     :foods-by-name {?food {:popularity ?popularity
                            :calories ?calories}}}

    ;; the output expression
    {:name ?name
     :favorite {:food ?food
                :popularity ?popularity
                :calories ?calories}}))

(favorite-food-info foods user)

;; => {:name :alice, 
;;     :favorite {:food :nachos, :popularity :high, :calories :lots}}

;; ======================== SEARCH ========================================

(comment
  "the above food example matched on alice's favourite food, nachos. What if we
  gave here more than one favourite food?")

(def user2 {:name :alice :favourite-foods [{:name :nachos}
                                            {:name :smoothie}]})

(comment
  "we can't write this with match because what we want to do is search the user
  data and scan through the collection of favourite foods.")

(defn favourite-foods-info [foods-by-name user]
  (m/search {:user user :foods-by-name foods-by-name}
            
            {:user
             {:name ?name
              :favourite-foods (m/scan {:name ?food})}
             
             :foods-by-name {?food {:popularity ?popularity
                                    :calories ?calories}}}
            
            {:name ?name
             :favourite {:food ?food
                         :popularity ?popularity
                         :calories ?calories}}))

(favourite-foods-info foods user2)
;; [{:name :alice, :favourite {:food :nachos, :popularity :high, :calories :lots}} 
;;  {:name :alice, :favourite {:food :smoothie, :popularity :high, :calories :less})

;; ========================== FIND ==============================================

(comment
  "")

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

