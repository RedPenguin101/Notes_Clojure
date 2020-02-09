(ns specdemo.core
  (:require [clojure.spec.alpha :as spec]
            [clojure.spec.gen.alpha :as gen]))

;; from https://www.bradcypert.com/an-informal-guide-to-clojure-spec/

(comment
  "Spec is for defining specifications for your data objects
  you can check validity, conform objects to a spec, or 
  generate test data based on a spec")

; a spec that validates something is a string

(spec/def ::id string?)

(spec/valid? ::id "ABC-123") 

(comment
  "The benefits of doing this over"
  (string? "ABV-123")
  "are that you can COMPOSE spec definitions
  and build them up into more complex units")

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

(comment
  "we use maps, so spec with maps using spec.keys")

(spec/def ::name string?)
(spec/def ::age int?)
(spec/def ::skills list?)

(spec/def ::developer (spec/keys :req [::name ::age]
                                 :opt [::skills]))

(spec/valid? ::developer {::name "Brad" ::age 24 ::skills '()})
;; => true

(comment
  "generally when you get in JSON or something your keys won't 
   be namespaced so you need to use :req-un")

(spec/def ::developer-un (spec/keys :req-un [::name ::age]
                                    :opt-un [::skills]))

(spec/valid? ::developer {:name "Brad" :age 24 :skills '()})
;; => true

(comment "You can ask clojure to EXPLAIN why something fails validation")

(spec/explain ::id-types "wrong!")

;; ======================== GENERATING TEST DATA ===========================

(gen/generate (spec/gen int?))
;; => -210199

(gen/generate (spec/gen ::developer))
;; => #:specdemo.core{:name "5P79qEqXo0", :age 1}

;; ========================== Official spec guide =========================
;; https://clojure.org/guides/spec

(spec/conform even? 1000)
;; => 1000

(spec/valid? even? 10)
;; => true

;; note valid? implicity turns the predicate into a spec

;; sets can be used as predicates

(spec/valid? #{:club :diamond :heart :spade} :club)
;; => true

(spec/valid? #{:club :diamond :heart :spade} 42)
;; => false


(import java.util.Date)

(spec/def ::date inst?)

(spec/valid? ::date (java.util.Date.))
;; => true

(use 'clojure.repl)
(doc ::date)
;; :specdemo.core/date
;; Spec
;;   inst?

;; composing with or, you need to annotate

(spec/def ::name-or-id (spec/or :name string?
                             :id int?))

(spec/explain ::name-or-id :foo)

(spec/conform ::name-or-id "abc")
;; => [:name "abc"]

(spec/conform ::name-or-id 123)
;; => [:id 123]

;; you can allow nil as a valid value with s/nilable

(spec/valid? string? nil)
;; => false

(spec/valid? (spec/nilable string?) nil)
;; => true
