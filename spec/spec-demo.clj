;; gorilla-repl.fileformat = 1

;; **
;;; # Guide to Spec
;;; 
;;; My home-built guide to Spec
;; **

;; @@
(ns spec-demo 
  (:require [clojure.spec.alpha :as s]))
*ns*
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-unkown'>#namespace[spec-demo]</span>","value":"#namespace[spec-demo]"}
;; <=

;; **
;;; ## What is spec for?
;;; 
;;; A big part of the philosophy of Clojure is that information should be represented as data, without putting 'classes' around them, which create brittleness problems. We just use a map.
;;; 
;;; Still, unless your data is trivially simple you will have to have a way to communicate what your data is for the human readers of your code.
;;; 
;;; For an named attribute, you will have some idea expressable as a predicate which says whether the value is OK. i.e. this attribute should be below 100, this one should be even.
;; **

;; @@
(s/def ::percent #(<= % 100))
(s/valid? ::percent 67)
(s/valid? ::percent 101)
(clojure.repl/doc ::percent)
;; @@
;; ->
;;; -------------------------
;;; :spec-demo/percent
;;; Spec
;;;   (&lt;= % 100)
;;; 
;; <-
;; =>
;;; {"type":"html","content":"<span class='clj-nil'>nil</span>","value":"nil"}
;; <=

;; **
;;; 
;;; For a map (a collection of keys-value pairs), the _only thing you want to say is what keys are in it_. You do not want to specify what the types of the values which associate with those keys are. This is an intentional limitation, as it creates overly rigid structures.
;;; 
;; **

;; @@
(s/def ::person (s/keys :req-un [::first-name ::last-name ::email]
                        :opt-un [::phone]))

(s/explain ::person {:first-name "Bob" 
                    :last-name "Smith" 
                    :phone 1234})
;; @@
;; ->
;;; val: {:first-name &quot;Bob&quot;, :last-name &quot;Smith&quot;, :phone 1234} fails spec: :spec-demo/person predicate: (contains? % :email)
;;; 
;; <-
;; =>
;;; {"type":"html","content":"<span class='clj-nil'>nil</span>","value":"nil"}
;; <=

;; **
;;; These different ideas for, and separation of, keyset specs (for maps) and attribute (key->value) specs is important. You can combine them, or not, as you need. But it's an explicitly two-phase approach
;;; 
;;; A couple of side effects of specification:
;;; * If you specify your data, you can also create generative tests from those specs.
;;; * By comparing data to a spec you can have rich explanations of why it doesn't conform to the spec
;;; 
;; **

;; @@

;; @@
