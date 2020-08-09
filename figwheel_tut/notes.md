# Figwheel tutorial

## React webapp quickstart
1. create a new project directory
2. create `deps.edn`

```clojure
{:deps {com.bhauman/figwheel-main {:mvn/version "0.2.11"}
        com.bhauman/rebel-readline-cljs {:mvn/version "0.1.4"}}
 :paths ["src" "target" "resources"]}
```

3. create build file

```clojure
^{:css-dirs ["resources/public/css"]}
{:main hello.cruel-world}
```

4. create a hello world app in `src/hello/cruel_world.cljs`
5. test the build with `clojure -m figwheel.main --build cruel --repl`, calling a function in your hello world from the repl
6. create an `index.html` in `resources/public` and a `style.css` in `resources/public/css`


## Official Figwheel Tut
https://figwheel.org/tutorial.html

### Prerequisites
* installed clojure / clj

### Setup
make directory to work in, `mkdir hello-cljs`

in `deps.edn` add `{:deps {com.bhauman/figwheel-main {:mvn/version "0.2.11"}}}`

start the cljs repl: `clj -m figwheel.main`

(or `clojure -m figwheel.main` if you want the REBL readline)

You can get to the repl at `localhost:9500`

### Create a file
create `src/hello/cruel_world.cljs`

```clojure
(ns hello.cruel-world)

(defn what-kind? []
  "Cruel")
  
(js/console.log (what-kind?))
```

In the repl: `(require 'hello.cruel-world')`

Try calling the function with `(hello.cruel-world/what-kind?)`

### Browser environment
You are working in a browser environment, so you can interact with the DOM:

`(js/document.getElementById "app")`

```clojure
(def app-element (js/document.getElementById "app"))
(set! (.-innerHTML app-element) (what-kind?))
```

### Start with initialized code
`$ clojure -m figwheel.main --compile hello.cruel-world --repl`

you might see a warning about adding target to your deps class path - go ahead an do that at your convenience.

You should also add `src` to the classpath, which means figwheel will watch this directory for changes and re-compile on the fly.

You can try it out by changing the text output by `what-kind?` and saving it.

### The build
Figwheel can use a **build file** to specify compiler options, as well as a build name.

First, delete your target directory to put us in a clean position

Now make a build file in the root dir called `cruel.cljs.edn`

In that, put `{:main hello.cruel-world}`

when launching the project, use `$ clojure -m figwheel.main --build cruel --repl`

You can put both clojurescript general compiler options, as well as figwheel specific ones in these build files.

### Packaging and optimisation
There are 4 optimisation modes:
* `:none` the default
* `:whitespace`
* `:simple` - makes safe optimisations
* `:advanced` - this is usually what you want for production apps

You can pick at the command line what you want

`$ clojure -m figwheel.main --optimizations whitespace  --build-once cruel`

### index.html

Add `resources` to your classpath, and create a `resources/public/css/style.css` and a `resrouces/public/index.html`

configure live css reload with `^{:css-dirs ["resources/public/css"]}` in your build file

### a full deps.edn

```clojure
{:deps {com.bhauman/figwheel-main {:mvn/version "0.2.11"}
        com.bhauman/rebel-readline-cljs {:mvn/version "0.1.4"}}
 :paths ["src" "target" "resources"]}
```

```clojure
^{:css-dirs ["resources/public/css"]}
{:main hello.cruel-world}
```

`$ clojure -m figwheel.main --compile hello.cruel-world --repl`


