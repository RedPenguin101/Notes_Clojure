# Notes on Eric Normands Markdownify SPA

## Lecture 2: Install and setup
Install node and npm
```
curl-sL https://deb.nodesource.com/setup_12.x | sudo -E bash -
sudo apt-get install -y nodejs
```

`npm init`
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

Use defonce for reagent atom so it won't redef when you reload

use https://www.npmjs.com/package/showdown for markdown->html renderer

Look for the `npm install --save showdown`

add to your requiers like `["showdown" :as showdown]`

Copy the javascript from the exmaples section and turn it into clojurescript

```javascript
var showdown  = require('showdown'),
    converter = new showdown.Converter(),
    text      = '# hello, markdown!',
    html      = converter.makeHtml(text);
```

```clojurescript
(defonce showdown-converter (showdown/Converter. )) ;; . notation is the 'new'

(defn md->html [md]
  (.makeHtml showdown-converter md))
  
[:div (md->html @markdown)]
```
## Lecture 4: adding copy buttons

```javascript
const copyToClipboard = str => {
  const el = document.createElement('textarea');  
  el.value = str;  
  el.setAttribute('readonly', '');
  el.style.position = 'absolute';
  el.style.left = '-9999px';
  document.body.appendChild(el);
  const selected =            
    document.getSelection().rangeCount > 0 
      ? document.getSelection().getRangeAt(0)
      : false;
  el.select();   
  document.execCommand('copy');
  document.body.removeChild(el);
  if (selected) {
    document.getSelection().removeAllRanges();
    document.getSelection().addRange(selected);
  }
};
```
Just transcribe to cljs
