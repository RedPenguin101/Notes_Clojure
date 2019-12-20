(ns specdemo.core
  (:require [clojure.spec.alpha :as spec]
            [clojure.spec.gen.alpha :as gen]))

;; from https://www.bradcypert.com/an-informal-guide-to-clojure-spec/

; Spec is for defining specifications for your data objects
; you can check validity, conform objects to a spec, or 
; generate test data based on a spec

; write a spec that validates something is a string

(spec/def ::id string?)
(comment 
  (spec/valid? ::id "ABC-123")) 
;; benefits of doing this over (string? "ABC-123")
;; you can COMPOSE spec definitions

(def id-regex #"^[0-9]*$")

(spec/def ::intid int?)
(spec/def ::id-regex
  (spec/and string? #(re-matches id-regex %)))

(spec/def ::id-types (spec/or ::intid ::id-regex))

(comment
  (spec/valid? ::id-types "12345")
  ;; => true
  
  (spec/valid? ::id-types 12345)
  ;; => false WHY!?!??!?
  
  (spec/valid? ::id 12345)
  ;; => true
  )

;; we use maps, so spec with maps
;; using spec.keys

(spec/def ::name string?)
(spec/def ::age int?)
(spec/def ::skills list?)

(spec/def ::developer (spec/keys :req [::name ::age]
                                 :opt [::skills]))

(spec/valid? ::developer {::name "Brad" ::age 24 ::skills '()})
;; => true

;; generally when you get in JSON or something your keys 
;; won't be namespaced so you need to use :req-un

(spec/def ::developer-un (spec/keys :req-un [::name ::age]
                                    :opt-un [::skills]))

(spec/valid? ::developer {:name "Brad" :age 24 :skills '()})
;; => true

;; you can ask clojure to explain why something failed to
;; validate

(spec/explain ::id-types "wrong!")

;; double colons are namespaced keywords. You need to use
;; them if you want to use spec

;; GENERATING TEST DATA

(gen/generate (spec/gen int?))
;; => -210199

(gen/generate (spec/gen ::developer))
;; => #:specdemo.core{:name "5P79qEqXo0", :age 1}

