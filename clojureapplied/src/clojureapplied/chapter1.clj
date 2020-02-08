(ns clojureapplied.chapter1)

(comment
  "INTRO"

  "This book is 'level 2', i.e. not about the basic list, 
  vector stuff. It's more operational than tactical, how to
  write good functional applications, from design to 
  production
  
  A Map of the book:
  * modelling the problem domain
  * transforming entities and collections
  * composing functions into components: state, concurrency, components
  * composing components into applications
  
  This ordering mirrors in theory the building of an actual application,
  though in practice it will usually not be so linear.
  
  Next we look at testing, integration and deployment. 

  Testing in the Clojure world tends away from example based testing, 
  towards more property based ideas.
  
  Integration is concerned with how your applications communicates with
  other applications: over the wire.
  
  Deployment: we look at some cloud based options")

(comment
  "Chapter 1: Modelling the domain"
  
  "Start with thinking about your problem domain in terms of data.
  
  Maps or Records? Maps (KV Pairs) are simplest. Since you'll often be 
  driving behaviour by type, it's sensible to stick in a ':type' value")

(def earth {:name "Earth"
            :moons 1
            :volume 1.08321e12
            :mass 5.97219e24
            :aphelion 152098232
            :perihelion 147098290
            :type :Planet})

(comment
  "Records might be useful to add a more defined structure to our data,
  with some more class-like features. You can define a record and then
  instantiate instances of the record, and the Type of the instance will
  be available.
  
  Records are have a bit more functionality around dynamic behaviour,
  and so can do polymorphic dispatch a bit easier.")

(defrecord Planet [name moons volume mass aphelion perihelion])

(def earthrecord (->Planet "Earth" 1 1.08e12 5.97e24 15209 14708))

(comment "this is a bit PLOPpy. The map->record syntax is a little more
         robust, since more descriptive, allows omission of optional args
         and callers will still work even when the definition of Planet
         changes")

(def earthrecord (map->Planet earth))

(comment "Both act like maps for most functions. Records can give better
         performance too. For domain entities, i.e. things which will 
         not be exposed outside your application, records are a good
         choice. Maps, on the other hand, are generally better suited
         to any public API for your application, because it minimises
         constraints on the caller.
         
         TBH, don'e worry about it too much; in practice they act mostly
         the same.")

(comment
  "Constructing Entities"
  
  "Optional args are generally denotes in fns like"
  
  (defn fn-with-opts [f1 f2 & opts] ,,,)
  
  "or with positional destructuring"

  (defn fn-with-opts [f1 f2 & [f3 f4]] ,,,)

  "Consider functionality about manipulating currency"
  
  )

(declare validate-same-currency)

(defrecord Currency [divisor sym desc])

(defrecord Money [amount ^Currency currency]
  java.lang.Comparable
    (compareTo [m1 m2]
      (validate-same-currency m1 m2)
      (compare (:amount m1) (:amount m2))))

(def currencies {:usd (->Currency 100 "USD" "US Dollars")
                 :eur (->Currency 100 "EUR" "Euro")})

(defn- validate-same-currency [m1 m2]
  (or (= (:currency m1) (:currency m2))
      (throw (ex-info "Currencies do not match."
                      {:m1 m1 :m2 m2}))))

(defn =$
  ([m1] true)
  ([m1 m2] (zero? (.compareTo m1 m2)))
  ([m1 m2 & monies]
   (every? zero? (map #(.compareTo m1 %) (conj monies m2)))))

(defn +$
  ([m1] m1)
  ([m1 m2]
   (validate-same-currency m1 m2)
   (->Money (+ (:amount m1) (:amount m2)) (:currency m1)))
  ([m1 m2 & monies]
   (reduce +$ m1 (conj monies m2))))

(defn *$ [m n] (->Money (* n (:amount m)) (:currency m)))

(comment "Constructor")

(defn make-money
  ([] (make-money 0))
  ([amount] (make-money amount :usd))
  ([amount currency] (->Money amount currency)))

(make-money)
(make-money 1)
(make-money 1 (:eur currencies))


(comment 
  "one of the problems with this, is it's still place oriented.
  We can fix this by using maps"
  
  (defn make-entity [f1 f2 {:keys [f3 f4] :as opts}])
  
  )

(def mission-defaults {:evas 0 :orbits 0})

(defn make-mission
  [name system launched manned? opts]
  (let [{:keys [cm-name lm-name orbits evas]} 
        (merge mission-defaults opts)] ; can define defaults and overide
    true))

(def apollo-4
  (make-mission "Apollo 4" 
                "Saturn 5" 
                #inst "1967-11-09T12:00:01-00:00"
                false {:orbits 3}))

(comment "destrucutre varargs as a map")

(defn make-mission
  [name system launched manned? & opts] ; notice new &
  (let [{:keys [cm-name lm-name orbits evas]} opts]
    true))

(def apollo-11
  (make-mission "Apollo 11" 
                "Saturn 5" 
                #inst "1967-07-16T13:32:00-00:00"
                true
                :cm-name "Columbia"
                :lm-name "Eagle"
                :orbits 30
                :evas 1))


(comment "above we saw you can provide a map of defaults. You can also
         just do this in the destructuring")

(defn make-mission
  [name system launched manned? & opts] 
  (let [{:keys [cm-name lm-name orbits evas]
         :or {orbits 0 evas 0}} opts]
    true))

(comment "you can also use constructors to do some 'pre-processing', and
         put some derived data in your object.")

(defn euclidean-norm [ecc-vector] true)

(defrecord Planet [name moons volume mass aphelion perihelion 
                   orbital-eccentricity])

(defn make-planet [name moons volume mass aphelion perihelion ecc-vector]
  (->Planet name moons volume mass aphelion perihelion
            (euclidean-norm ecc-vector)))

(comment "A good use case for constructors is to isolate IO side-effects
         from the rest of your code
         
         So you define your record, and the make-record constructor is
         the only thing that has your IO in it.")

(comment "you might want to contain an 'empty' object, that you will
         add stuff to")


