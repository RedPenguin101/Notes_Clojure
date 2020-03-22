(ns clojureapplied.chapter6)

(comment "chapter 6: Creating components
         * namespaces
         * component api design
         * connecting components with channels
         * implementing components")

(comment
  "Components are larger units of code which represent boundaries within
  our system. It lets us reason about things at a higher level, and divide
  work more easily between teams or team members
  
  They are collections of functions etc. that combine to an overall purpose.
  They will be accessed via and API.")

(comment "Organizing with namespaces")

(comment
  "namespaces allow you to group forms. They can prevent name conflicts
  (e.g. the shopping example from C4 had two functions called init)
  
  Namespaces are used for organizing functions, Components are used for
  organizing at the problem level. They work in tandem
  
  You want to create a logical namespace structure. For example:
  * Utility: generic functions organized by domain/purpose. Few deps
  * Data definition: separate you domain entities / schema
  * Abstractions: protocols
  * Implementation: the things that implement the protocols
  * Assembly: ties everything together
  * Entry point: connect the start of the application to start lifecycle ops.
    web apps, CLIs, services etc.
  
  The dependencies in this will generally flow upwards and spread out

  entry point -> assembly -> impl -> abstractions/data defs -> utils
  
  the directed nature is important for keeping you application simple!
  
  When a project is small, the most sensible organization is a horizontal
  cut, grouping implementations with the abstractions and utilities they use,
  but as you get larger you'll want to change to a more vertical structure
  with, with a vertical slice of namespaces being a component with an API,
  an implementation, datadef, utils etc.
  
  Use `defn-` and `^:private` meta to only expose the things you want
  
  This is hinting to a component user rather than a hard 'no'. It's about
  signalling that they shouldn't use these, and de-emphasizing them (eg they
  won't show up on lists of namespace functions)")

(comment "Designing component APIs")

(comment
  "When you have identified a component, try to make explicit what it is 
  for, and how it will be used.
  
  The API is the thing that outside consumers to the component will use.
  They do this in two primary ways: invoking functions and putting messages
  on queues or channels
  
  Your components should expose data directly _so long as it's immutable_
  
  Consider a knowlege engine component which persist and manages rules 
  about getting requests and delivering a response. The API might look like 
  this"

  ;; ke is the ke state - i.e. mutable collection of rules 
  ;; Read interface
  (defn get-rules [ke])
  (defn find-rules [ke criteria])

  ;; update
  (defn add-rule [ke rule])
  (defn replace-rule [ke old-rule new-rule])
  (defn delete-rule [ke rule])

  ;; process
  (defn fire-rules [ke request])
  
  ;; client code
  (let [ke (new-ke)]
    (add-rule ke :r1)
    (add-rule ke :r2)
    (add-rule ke :r3)
    (replace-rule ke :r1 :r1b)
    (delete-rule ke :r3)
    (get-rules ke))

  "Notice that a this whole can be supported by a smaller set
  (get-rules, transform-rules, fire-rules). This is a common pattern:
  a few base functions which are split into more specific ones for ease of
  use. Protocols are a good way to capture this"

  (defprotocol KE
    (get-rules [ke] "Get full rule set")
    (transform-rules [ke update-fn] "apply transform, return new KE")
    (fire-rules [ke request]))

  ;; private helper function
  (defn- transform-criteria [criteria] ,,,)
  
  ;; api fns over protocol 
  (defn find-rules [ke criteria]
    (filter (transform-criteria criteria) (get-rules ke)))

  (defn add-rule [ke rule]
    (transform-rules ke #(conj % rule)))

  ;; etc

  "The implication of this is that you can implement get-rules, 
  transform-rules and fire-rules on a record with extend-protocol stored
  with that record
  
  users ---> components.ke <- - extends - - defrecord rules
             defprotocol
             api fns
  
  You could make the protocol cover the entire API, but that's excessive
  Use the namespace to separate that, and the protocol for the minimal 
  abstraction")

(comment
  "Async APIs"
  
  "If the fire-rules operation is expensive, consider returning a future
  so that the caller can decide when to block (by derefing)"
  
  (let [response-future (fire-rules ke request)]
    ;; other work
    @response-future)
  
  
  "you could also use callbacks and promises"

  (let [callback (fn [response]) ,,,]
    (fire-rules ke request callback))

  (let [result-promise (fire-rules ke request)]
    ;;other work
    @result-promise))


(comment "connecting components with channels")

(comment 
  "queues are great for talking between components! Language of the system
  etc.
  
  You can either accept channels from the caller, or have your component 
  create them and return them to the caller."
  
  (defn make-feed-processor [input-channel] ,,,)

  ;; vs

  (defn make-feed-processor []
    (let [ch (chan 100)] ,,, )
    ;;maybe ch as return value
    )

  (defn input-chan [feed-processor]
    ,,,)

  "accepting external channels creates more options for assembling your system
  later. One point to consider is buffer size. If it's internal, then buffer
  size is more controllable; you can set it or allow the client to specify.
  
  Connecting components via channels can be done with direct connections,
  or via fan in or fan out")


(comment
  "direct connections (one-to-one)"
  
  "We connect them with a pipe if the channels are internally specified"
  
  (let [c1 (make-comp-1)
        output-chan (get-output c1)
        c2 (make-comp-2)
        input-chan (get-input c2)]
    (pipe output-chan input-chan))
  
  "this connects the out of c1 with the in of c2. By default when the first is
  closed, the second will be also. Effectively these are now a single channel
  
  If the channels are externally specified you don't even need to do that,
  you can just pass the output channel from c1 as the input channel for c2
  
  Lastly, a pipeline function links two pipes. See c5 for details")


(comment
  "Fan out (one-to-many)
  
  When a output channel needs to send it's messages to multiple consumers
  
  Good for logging etc. you can do this with
  * split: divides traffic based on the result of a predicate, good 
    for sending invalid messages to a seperate process for handling
  * mult: takes a single message and sends it to multiple channels via
    'taps', which are added and removed from a channel with the tap and
    untap function (a tap is removed if the channel is closed)"
  
  (defn connect-and-tap
    [input output]
    (let [m (mult input)
          log (chan (dropping-buffer 100))]
      (tap m output)
      (tap m log)
      log))
  
  "Note that a full buffer on one tap will block the whole mult
  so in the above example we added a dropping-buffer to the log channel
  If the log queue gets too full of unproccessed messages, it will just drop 
  the message. This is not great, but maybe better than clogging up the pipe
  and maybe killing our application at 2am!
  
  * pub/sub is what it sounds like. pub a channel with a kw 'topic', and 
    that topic can be subbed to."
  
  (defn assumble-chans []
    (let [in (chan 10)
          p (pub in :topic)
          news-ch (chan 10)
          weather-ch (chan 10)]
      (sub p :news news-ch)
      (sub p :weather weather-ch)
      [in news-ch weather-ch]))

  "when a message has the kv pair {:topic :news} it will direct only to the
  news channel, etc.
  
  You can dynamically sub and unsub, so this is basically an in-mem message
  bus")

(comment
  "Fan in: take messages from several chans onto one with mix and merge"
  
  "merge simply combines messages, returning the combined channel. you can't
  modify a merge channel after creation"
  
  (defn combine-channels [twitter-ch facebook-ch]
    (merge [twitter-ch facebook-ch] 100))
  
  "mix allows more controll over what comes from each channel, using :pause
  :mute and :solo - like channels on an audio mixer"
  
  (defn mix-chans [twit-ch face-ch out]
    (let [m (mix out)]
      (admix m twit-ch)
      (admix m face-ch)
      (toggle m {twit-ch {:mute true}
                 face-ch {:mute true}})
      m)))


(comment "Implementing components")

(comment
  "How to implement functionality behind the API?
  
  Most components will have state. the API might allow state update, or
  invoke functionality that depends on it. We must consider 
  * granulatiry of state
  * component life-cycle")

(comment
  "Granularity of state"
  
  "A component often has changeable runtime state, such as db conns, config
  Sometimes they will be passed in, but more often they will be internally 
  created. You should use an atom or a ref to hold the state.
  
  Which one depends on the level of coordination required. If you will need
  to update things together, consider a ref
  
  But dont rush straight to it! you can co-orindate atoms 'manually'"
  
  (defrecord CustomerAccounts [accounts customers])
  (defn make-customer-accounts []
    (map->CustomerAccounts {:accounts (atom {})
                            :customers (atom {})}))
 
  "if we think we might need to update accounts and customers with a 
  transaction guarantee we _could_ replace them with refs, OR we could just
  put them both in a single atom"

  (defrecord CustomerAccounts [state])
  (defn make-customer-accounts []
    (map->CustomerAccounts (atom {:account {} :customers {}})))
 
  "This is descibed as 'course grained state', where you group your state
  into a single identity, which you have to update together.")


(comment
  "Configuration"
  
  "A component needs to know: configuration, deps and runtime state
  
  We'll cover how you get or build config in C7.
  
  When you construct a component you should pass in config values as a map
  or record (to avoid PLOP!), because config values can change as a system
  develops
  
  Components will often need to get a reference to another component.
  You can pass it the ref in as an arg.
  This is fine, but often it's better to decouple them by passing in a chan
  
  Records are a good choice for storing config, deps and channels, and 
  runtime state, because it means you can use protocols to define component
  behaviour")

(comment
  "Lifecycle"
  
  "Mostly it's pretty simple: construction, start and stop.
  
  Consider the rule-based knowledge engine"
  
  (defrecord KnowledgeEngine
    [config
     ch-in
     ch-out
     rules ; current rule set
     active])

  (defn make-knowledge-engine
    [config ch-in ch-out rule-set]
    (->KnowledgeEngine config ch-in ch-out (atom rule-set) (atom false)))

  (defn start-knowledge-engine
    [{:keys (ch-in ch-out rules active) :as ke}]
    (reset! active true)
    (go-loop [request (<! ch-in)
              response (fire-rules ke request)]
             (>! ch-out response)
             (when @active (recur)))
    ke)

  (defn stop-knowledge-engine
    [{:keys (ch-out active) :as ke}]
    (reset! active false) ; exit go-loop
    (async/close! ch-out)
    ke)
  
  "note the input channel isn't closed. Convention is that the owner of the
  input channel (i.e. the component putting stuff on it) should be responsible
  for closing it.")


