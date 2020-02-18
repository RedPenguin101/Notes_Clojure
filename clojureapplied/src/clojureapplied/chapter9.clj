(ns clojureapplied.chapter9)

(comment "chapter 9: Formatting data
         * serialization
         * EDN
         * JSON
         * Transit")

(comment
  "How to communicate with other programs")

(comment "Data serialization formats")

(comment
  "Serializable data can be stored and translated.
  Different formats
  As long as two programs, in two languages, know the same format, they
  can talk to eachother
  
  Which format? consider
  * human readble?
  * schema for validation?
  * format validation?
  * change over time, versioning?
  * conform to standard?
  * libs available?
  * speed?
  
  So many formats. We look at json and edn text formats, and transit, which
  can be text or binary.")

(comment "EDN")

(comment
  "basically clojure data structures (actually clojure is a superset)
  
  It's extensible for new types with tagged literals, eg"
  
  {:price #pricing/money "$55.35"}
  
  "read with clojure.edn namespace"
  
  (clojure.edn/read-string (slurp "users.edn"))

  "note that this doesn't try to execute any code brought in, unlike
  the clojure read-string version. Don't use this
  
  values work as you'd expect. symbols can be namespaced
  
  there are two out-of-the-box extended types: #inst (point in time) and
  #uuid")

(comment 
  "tagged literals"
  
  {:kw #namespace/symbol data}
  
  "first you define the representation of the datatype in your program
  then create a reader that can create an instance of the type
  
  Consider a playing card"
  
  (defrecord Card [rank suit])
  (def ranks "23456789TJQKA")
  (def suits "hdcs")

  (defn- check [val vals]
    (if (some #{val} (set vals))
      val
      (throw (IllegalArgumentException.
               (format "Invalid value %s, expected %s" val vals)))))

  (defn card-reader [card]
    (let [[rank suit] card]
      (->Card (check rank ranks) (check suit suits))))

  (card-reader "2c")
  ;;=> #clojureapplied.chapter9.Card {:rank \2, :suit \c}}

  "if you put all your reader functions in a data_readers.clj file
  they will be loaded by clojure on startup and available to your application
  
  It should contain a map literal with the kv pair"

  {tags-from-edn reader-function-symbol}

  {my/card cards/card-reader}

  "alternatively you can do the bindings in the code"

  (binding [*data-readers* {'my/card #'cards/card-reader}]
    (read-string "#my/card \"2c\""))

  "you can also print, but this is getting tedious"
  
  "you write edn out to file like this"
  
  (with-open [w (clojure.java.io/writer f)]
    (.write w text))
  
  "not sure why you wouldn't use spit though")

(comment "JSON")

(comment
  "pretty common. JSON has an object (a map) and an array (ordered lists)
  There's a built in lib, clojure.data.json. You get a read-str and write-str
  
  More featurefull and FASTER is Cheshire. you get parse-string and 
  generate-string.
  
  generate-string automatically converts kw keys to strings
  parse-string takes a second, boolean, argument to tell it whether to 
  convert to kw or not. you can set it up with encoders to do other stuff,
  like date encoding
  
  Since JSON is string serialisation, it's not as fast as binary serialization
  The trade off for the speed is the dev time, and lower mindshare (everyone
  gets JSON)")

(comment "Transit")

(comment 
  "A newcomer from Clojure devs. Extensible metaformat that sits on top of
  either JSON or MessagePack. 
  Blah blah blah")

