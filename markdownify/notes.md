# Notes on Eric Normands Markdownify SPA

## Lecture 2: Install and setup
Install node and npm
```
curl-sL https://deb.nodesource.com/setup_12.x | sudo -E bash -
sudo apt-get install -y nodejs
```

`npm init`o
creates package.json. you need name, version, empty scripts

`npm install -g shadow-cljs` installs globally
`npm install --save-dev shadow-cljs` creates a local install, puts it in your package.json

(will also create package-lock.json, which is much more detailed package)

`shadow-cljs init` to create folder structure. Creates shadow-cljs.edn file with the build config. source paths, deps, builds

create src/main/markdownify/main.cljs

Make it look like this

```clojure
{:source-paths
 ["src/dev"
  "src/main"
  "src/test"]

 :dependencies
 []

 :dev-http {9090 "public/"}

 :builds
 {:app {:output-dir "public/compiledjs/"
        :asset-path "compiledjs" ;; if serving index from public, how do you get to main?
        :target     :browser
        :modules    {:main {:init-fn markdownify.main/main!}}
        :devtools   {:after-load markdownify.main/reload!}}}}
```

Also create the two 'hooks' main.cljs: `main!` and `reload!`

create public/index.html

```html
<body>
    <script type="text/javascript" src="/compiledjs/main.js"></script>
</body>
```

call shadow-cljs watch app (where app is the name of the build)

Trigger new build with a space or something and make sure it recompiles and reload

## Lecture 3: creating the markdown editor

Add reagent to shadow.edn

```clojure
 :dependencies
 [[reagent "0.9.1"]]
```
(you'll have to restart shadow-cljs, it will call npm)

require reagent in your main.

Next we **mount** the virtual (react/reagent) DOM into the DOM, at the element called 'app'.

```clojure
(defn mount! []
  (r/render [app]
            (.getElementById js/document "app")))
```

So you need to create the 'app' in both main and index.html

Then you need to call mount! in you main! and reload!