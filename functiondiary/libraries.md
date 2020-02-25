# Libraries

## Core
* Core async `[clojure.core.async]`
  * leiningen: `[org.clojure/core.async ""]`
  * [github](https://github.com/clojure/core.async)


## Data and IO
* `[clojure.data.json]`
* Cheshire: Fast and featureful JSON en/decoding
  * `[cheshire.core :refer :all]`
  * leiningen `[cheshire "5.10.0"]`
  * [github](https://github.com/dakrone/cheshire)


## Utilities

* Math Combinatorics `[clojure.math.combinatorics]`

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