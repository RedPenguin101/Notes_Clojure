(ns clojureapplied.chapter5
  (:require [com.stuartsierra.component :as component]
            [environ.core :refer [env]]))

(comment "chapter 7: Compose your application
         * Taking things apart
         * Implementing with component
         * Putting things together
         * System config")

(comment
  "Assembling components into applications :breaking a problem into component
   sized pieces, defining state and API for each component assembling them 
  into an app")

(comment "Taking things apart")

(comment
  "problem->rough architecture
  
  No rules on how to separate, but common guidelines
  
  Group together
  * things that work on the same kind of data
  * things that have a common scope or lifetime
  * things that are likely to change together as external requirements evolve
  
  Split something out if it is resusable in a different context"
  
  "Consider a system that monitors and responds to mentions of our products
  on social media. It should find messages that need attention, build possible
  responses, and send those responses
  
  Right off the bat you could infer that you should have one component for
  interacting with each third-party source. These feeds can share many
  implementation needs.
  
  You'll need a knowledge base of rules, which determines how to respond
  to messages.
  
  You'll need a component that takes the candidate responses from the ke 
  and sends them for approval, before sending the responses to the 3rd party
  interactor to be posted. This component also handles access to a person")


(comment "Implementing with Component")

(comment
  "component is a library for managing component lifecycles, start stop etc.
  
  Start with feeds. you'll need auth, plus normal stuff")

;; Feed namespace

(defn process-messages [status chan])
(defn handle-responses [status chan])

(defrecord Feed [auth status msg-chan response-chan]
  component/Lifecycle
  (start [component]
    (reset! (:status component) :running)
    (process-messages status msg-chan)
    (handle-responses status response-chan)
    component)
  (stop [component]
    (reset! (:status component) :stopped)
    component))

(defn new-feed [auth msg-chan response-chan]
  (->Feed auth (atom :init) msg-chan response-chan))

(comment
  "component/Lifecycle is a protocol which we are implemening here
  The two methods are start and stop. Note the subprocesses process-messages
  and handle responses are passed the reference to the status atom, and they
  will implement their own behaviour for when the component is stopped.")

;; Kengine namespace

(defrecord KnowledgeEngine
  [ke-config feed-chan alert-chan rules]
  
  component/Lifecycle
  (start [component]
    (watch-feeds feed-chan alert-chan)
    component)
  (stop [component]
    component))

(defn new-knowledge-engine [ke-config feed-chan alert-chan]
  (->KnowledgeEngine ke-config feed-chan alert-chan
                     (atom (:rule-set ke-config))))

(defn add-rule [ke rule]
  (swap! (:rules ke) conj rule))

;; approvals namespace

(defrecord Approvals [approvals-config alert-chan knowledge-engine 
                      response-chan]
  component/Lifecycle
  (start [component]
    (process-alerts alert-chan)
    (process-responses knowledge-engine response-chan)
    component)
  (stop [component]
    component))

(defn new-appovals [approval-config alert-chan response-chan]
  (map->Approvals {:approval-config approval-config
                   :alert-chan alert-chan
                   :response-chan response-chan}))

;; Note that Component will start and inject the knowledge engine

(comment "putting things together")

(comment
  "a system in Component is a special component that starts and stops other 
  components. It figures out the deps and starts things in the correct order
  It does require there are no cycles in the deps graph.
  
  It's defined with a component/system-map in main.")

;; main ns

(defn system [{:keys (twitter facebook knowledge approvals) :as config}]
  (let [twitter-chan (async/chan 100)
        twitter-response-chan (async/chan 100)
        facebook-chan (async/chan 100)
        facebook-response-chan (async/chan 100)
        alert-chan (async/chan 100)
        response-chan (async/chan 100)
        feed-chan (asycn/merge [twitter-chan facebook-chan])
        response-pub (async/pub response-chan :feed)]
    (async/sub response-pub :twitter twitter-response-chan)
    (async/sub response-pub :facebook facebook-response-chan)
    
    (component/system-map
      :twitter (feed/new-feed twitter twitter-chan twitter-response-chan)
      :facebook (feed/new-feed facebook facbook-chan facebook-response-chan)
      :knowledge-engine (kengine/new-knowledge-engine knowledge 
                                                      feed-chan alert-chan)
      :approvals (component/using (approvals/new-approvals 
                                    approvals alert-chan response-chan)
                                  [:knowledge-engine]))))

(comment "System Config")

(comment
  "can be system attributes, per-env information, dev-only info.
  * sys-attrs are flags that affect how your app runs, e.g. feature flags
  * per-env changes for each environment
  * dev-only allows devs to tweak things on their machines as they work
  
  Only sys-attrs should be in source control. per-env should be in the 
  environment itself. Dev only should be on devs box
  
  We'll need to take all of these and make them into one consistent view
  of the system settings at startup
  
  We'll use Environ and Immuconf")

(comment
  "Environ creates a single config pulled from project.clj, env vars, java
  system properties. We might need a :ruleset, :feed1-user :verbose (a
  debug flag for dev work). At dev time we might want to set ruleset and 
  feed1 user in our local build.
  
  You can use leiningen profiles to load things in project.clj"
  
  ,,,
  :profiles {:dev {:env {:rule-set "basic"} }
             :qa { ,,, }
             :prod {:env {:rule-set "advanced"}}}
  ,,,

  "dev is on in lein repl by default. (as is :user). So you can just start
  your repl, require environ and get to your config"

  (env :rule-set)
  ;; "basic"
  
  "mostly you'll want to keep that stuff out of project.clj though, and put
  it in a profiles.clj which you gitignore. That way you're not sticking 
  passwords up on github"

  "when you come to deploy to production you won't be using leiningen, so
  you'll have to use JVM system properties or environmental variables")

(comment
  "Immuconf"
  
  "Environ has some downsides. First it expects to get a flatmap of keywords
  to strings, which is limiting. second, you need the lein-environ plugin to
  use it, which limits options with respect to build tools
  
  Immuconf takes a different approach. It focuses on specifying a set of 
  config files, in EDN formate.
  
  Put the dep in your leiningen, and create a config.edn file in resources
  
  The contents must be an EDN map. Inside app, you can then load the config"
  
  (def config (immuconf.config/load "resources/config.edn"))

  "and get stuff from it"

  (immuconf.config/get config :rule-set)

  "the load function accepts multiple files, and merges them with the same
  semantics as clojure's merge (i.e. right wins). A warning is logged for 
  overwrites."

  (merge {:thing :left} {:thing :middle} {:thing :right})
  ;;=> {:thing :right}

  "make a user.edn at project root, which will be outside source control and
  will give dev specific config."

  "you can turn off the overwrite warning by tagging things as default if
  you expect them to be overwritten"

  {:rule-set #immuconf/default :basic}

  "you can go the other way, and require some values to be overwritten.
  This means you can both see the full picture in your config.edn and put in
  verification that things like a database username and password are being
  passed in without having to specify them in the main config "

  {:rule-db {:url #immuconf/overide "dummy database"
             :user #immuconf/default "admin"
             :password #immuconf/overide "specify password"}}

  "note the nesting here, which Environ can't do")

