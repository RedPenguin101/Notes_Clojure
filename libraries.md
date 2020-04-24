# Libraries

## Core
* Core async `[clojure.core.async]`
  * leiningen: `[org.clojure/core.async "1.0.567"]`
  * [github](https://github.com/clojure/core.async)
* spec: [clojure.spec.alpha :as spec]
  * [clojure.spec.gen.alpha :as gen])


## Data and IO
* `[clojure.data.json]`
  * [org.clojure/data.json "1.0.0"]
  * use `json/write-str` and `json/read-str` to xform to clojure data
* Cheshire: Fast and featureful JSON en/decoding
  * `[cheshire.core :refer :all]`
  * leiningen `[cheshire "5.10.0"]`
  * [github](https://github.com/dakrone/cheshire)
* EDN
  * `[clojure.edn]`


## Utilities

* Math Combinatorics `[clojure.math.combinatorics]`
  * [org.clojure/math.combinatorics "0.1.6"]
* numeric tower `[clojure.math.numeric-tower :as math]`
  * [org.clojure/math.numeric-tower "0.0.4"]
  * expt, gcd, lcm, floor, ceil round sqrt

## App design

* __Component__: framework for managing the lifecycle and dependencies of software components which have runtime state.
  * `[com.stuartsierra.component :as component]`
  * leiningen: `[com.stuartsierra/component ""]`
  * [github](https://github.com/stuartsierra/component)

* __Environ__: library for managing environment settings from a number of different sources.
  * `[environ.core :refer [env]]`
  * leiningen `[[environ "1.1.0"]]`
  * [github](https://github.com/weavejester/environ)

* __Immuconf__:  library for explicitly managing configuration files
  * `?`
  * leiningen `[levand/immuconf "0.1.0"]`
  * [github](https://github.com/levand/immuconf)

## Other
* Expound: spec message humanize
  * `[expound "0.8.4"]`
  * `[expound.alpha :as expound]`
  * `(expound/expound)` and `(expound/expound-str)`
