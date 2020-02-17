(ns clojureapplied.chapter4
  (:require [clojure.core.async 
             :refer [>! <! >!! <!! chan go go-loop]]))

(comment "Part 2: Applications and the state model")

(comment "Chapter 4: State, Identity and change
         * modelling change
         * Tools for managing change
         * Living with change")

(comment 
  "Modelling change"
  
  "Mostly you'll be just passing around collections and generating new ones.
  Occasionally you'll need to model changes while holding onto the 'identity'
  of the thing you've changed
  
  Time as we perceive it is not continuous, it's a series of snapshots,
  like the horse zoetrope. When we model things in an object oriented way
  we also model these snapshots. One of the problems is that we only end up
  with the last 'frame' of the video. Sometimes this is OK.
  
  If you only think about the now, you conflate two things: identity and state
  
  Identity is kind of an abstract thing. It's a set of other things we've 
  decided to call by a name. The state represents the identities value at
  an instant of time. To model this we need mutability, and also to be able
  to update discretely and instantaneously from the POV of an observer, ie
  make it visible to all observers at the same time
  
  Clojure has reference entities, which are mutable containers for a 
  sucession of immutable values. It's called the unified update model.
  It generally looks like this"
  
  (update-fn container data-fn & args)

  "where the data-fn is applied to the current value of the reference
  There are 4 types:
  * var
  * atom
  * agent
  * ref")

(comment
  "Atomic succession updates a single entity.
  Since atoms are intended to represent stand-alone values they can change
  independently of the rest of your system and don't need to be co-ordinated
  with other stateful resources.
  Collections are good candidates for atomic succession.
  atomic succession in Clojure can be sync or async, via the atom or agent 
  types."
  
  "Transactional Succession is used when you want to update two identities
  together (in a co-ordinated transaction).
  
  Transactions occur inside a tiny bubble of reality, separate from the rest
  of your system. When the transaction completes, the bubble pops and the 
  effects of the transaction are visible to the rest of the system.")

(comment
  "The types (name, createfn, updatefn, setfn)
  * Atom  - atom  - swap!          - reset!
  * Ref   - ref   - alter, commute - ref-set
  * Var   - def   - alter-var-root - var-set
  * Agent - agent - send, send-off - restart-agent")

(comment
  "Atoms
  Start to become critical when data starts to be observed by multiple 
  threads.")

(comment
  "Grocery shopping example")

(defn go-shopping-naive
  [shopping-list]
  (loop [[item & items] shopping-list
         cart []]
    (if item
      (recur items (conj cart item))
      cart)))

(go-shopping-naive [:bacon :eggs :sausage])
;; => [:bacon :eggs :sausage]]

(comment
  "this requires no co-ordination because there's only one person shopping
  Let's complicate our store API a bit")

(def inventory (atom {}))

(defn no-negative-values? [m]
  (not-any? neg? (vals m)))

(defn in-stock? [item]
  (let [cnt (item @inventory)]
    (and (pos? cnt))))

(defn store-init [items] ; note the book handles this more elegantly
                         ; with namespaces
  (set-validator! inventory no-negative-values?)
  (swap! inventory items))

(defn grab [item]
  (if (in-stock? item)
    (swap! inventory update-in [item] dec)))

(defn stock [item]
  (swap! inventory update-in [item] inc))

(comment 
  "notice:
  to view the contents of the atom you must deref with @inventory
  The validator makes sure we can't decrement below 0. This guards against
  the possibility that an operation gets a value of one, tries to dec it,
  but in the meantime another operation has already decced it.
  With the validator, if this happens the atom will throw an 
  IllegalStateException"
  
  "note you can also declare the validator at instantiation"
  
  (def inventory (atom {} :validator no-negative-values?))
  )

(comment "now lets re-write our single threaded client application")

(defn shop-for-item [cart item]
  (if (grab item)
    (conj cart item)
    cart))

(defn go-shopping [shopping-list]
  (reduce shop-for-item [] shopping-list))

(comment "notice that a much more elegant API has emerged after a few 
         considered steps. This should be happening all the time.
         Think then do.")

(comment
  "Watch functions"
  
  "How to restock as things get low? You could add more state, adding in
  a master list of config file. But that sounds janky. let's use a watch
  function
  
  These are comparable to the observer pattern in OO. Clojure handles
  registrations and notifications. A watcher functions has 4 args:
  a key, a ref to watch, old value and new value"
  
  (defn watch-fn [watch-key reference old-val new-val] ,,,))

(declare sold-items)

(defn restock-order [k r ov nv]
  (doseq [item (for [kw (keys ov)
                     :when (not= (kw ov) (kw nv))] 
                 kw)] ; pulls out changed keyword as 'item'
    (swap! sold-items update-in [item] (fnil inc 0))
    (println "need to restock" item)))

(defn init-with-restock [m]
  (def inventory (atom m))
  (def sold-items (atom {}))
  (set-validator! inventory no-negative-values?)
  (add-watch inventory :restock restock-order))

(comment
  "The watch function is now called whenever the inventory atom is updated.
  This watch function adds the changed items to the sold-items atom, and 
  prints that you need to restock
  
  The implications of the key (:restock) can be opaque. Basically they are
  used by the map that clojure maintains to track all the watches. You can 
  use it to identify the watch, and, for example to remove it.
  
  Now lets restock")

(defn restock-all []
  (swap! inventory #(merge-with + % @sold-items)) 
  ; merges stuff in sold-items with inventory...
  (reset! sold-items {})) ; then empties sold-items

(init-with-restock {:apples 1 :bacon 3 :milk 2})
(grab :bacon)
;; need to restock :bacon
(grab :bacon)
;; need to restock :bacon
(grab :milk)
;; need to restock :milk
sold-items
;; #object [clojure.lang.Atom 0x21442d67 
;; {:status :ready, :val {:bacon 2, :milk 1}]
inventory
;; #object [clojure.lang.Atom 0x34fba471 
;; {:status :ready, :val {:apples 1, :bacon 1, :milk 1]

(restock-all)
;; need to restock :bacon
;; need to restock :milk
inventory
;; {:apples 1, :bacon 3, :milk 2}
sold-items 
;; {}

(comment
  "couple of issues here. First is that when we restock, the watch function
  is called, inventory is swapped and new things are added to our sold list.
  It is immediately reset, but still we see only that 'need to restock: bacon'
  is only printed once, not twice.
  
  More obscure and dangerous; think about what happens when thread 2
  grabs something from the inventory, between the times when thread 1 is
  re-creating the inventory and resetting sold items. That will decrement
  the inventory, increment the sold items, and then the sold-items is reset,
  meaning the item won't be restocked when restock-all is next called.
  
  Suddenly we need to co-ordinate, and an atom is no longer suitable")

(comment
  "so lets use transactions and refs.
  The conceit here is that we bring along kids to help fill the cart
  We will send them out to find one item, they will dawdle for a while
  and when they return with their item they will put it in the cart.
  
  The key is to have rules about the items being shopped for.
  * an item on the list gets crossed off when it's assigned to a child
  * An item remains assigned to a child until it's placed in the cart
  * candy isn't allowed in the list or cart.
  
  We must make sure that removing an item on the list and assigning it to
  a child happens simultaneously, otherwise things will get assigned twice.
  Same for recovering the item and putting it in the cart.
  
  We need to co-ordinate and so we need refs.
  We'll create 3 refs: the list, the assignments, and the cart")

(def shopping-list (ref #{}))
(def assignments (ref {})) ; kv pairs of child and item
(def shopping-cart (ref #{}))

(defn init []
  (store-init {:eggs 2 :bacon 3 :apples 3 :candy 5 :soda 2 :milk 1
         :bread 3 :carrots 1 :potatoes 1 :cheese 3})
  (dosync
    (ref-set shopping-list #{:milk :butter :bacon :eggs :carrots
                             :potatoes :cheese :apples})
    (ref-set assignments {})
    (ref-set shopping-cart #{})))

(comment
  "changing the value of a ref has to be done inside a 'dosync' transaction
  To change a ref you can use 'ref-set' usually for re-intialization,
  alter (checks for internal consistency) and commute (doesn't require
  internal consistency).
  
  Internal consistency means that the the value of the ref when alter is 
  applied must be the same as when the transaction started (i.e. when the
  dosync was kicked off). If it isn't, it will restart the transaction.
  
  Commute is cheaper than alter, but should only be used when the function
  being applied is commutative - e.g. order of execution doesn't matter
  f1(f2(x)) = (f2(f1(x))")
  
(defn assign-item-to-child [child]
  (let [item (first @shopping-list)]
    (dosync
      (alter assignments assoc child item) 
      ; get the first item from the list and assign it to the child
      (alter shopping-list disj item))
      ; remove the item from the shopping list
    item))

(defn assignment 
  "returns the item the child is assigned" 
  [child]
  (get @assignments child))

(defn buy-candy []
  (dosync
    (commute shopping-cart conj (grab :candy))))

(defn dawdle []
  (let [t (rand-int 5000)]
    (Thread/sleep t)
    ;(maybe? buy-candy)
    ))

(defn collect-assignment [child]
  (let [item (assignment child)]
    (dosync
      (alter shopping-cart conj item)
      (alter assignments dissoc child)
      (ensure shopping-list)) 
      ; ensure not actually necessary here since we're using alter, but this
      ; will retry the operation if the ref value has changed
      ; since the transcation started
    item))

(defn send-child-for-item 
  "sends a kid for an item, and once done puts them on a channel"
  [child item q]
  (println child "is searching for" item)
  (dawdle)
  (collect-assignment child)
  (>!! q child))

(defn report []
  (println "store inventory" @inventory)
  (println "shopping list" @shopping-list)
  (println "assignments" @assignments)
  (println "shopping-cart" @shopping-cart))

(def my-kids #{:alice :bobby :cindi})

(defn go-shopping []
  (init)
  (report)
  (let [kids (chan 10)] ; instan kids as a channel with buffer of 10
    (doseq [k my-kids] 
      (>!! kids k)) ; add my-kids to the kids buffer
    (go-loop [kid (<! kids)] ; take the first kid off the channel
             ; If there's stuff left on the shopping list, send the kid
             ; to go get the first thing on the list. Recur with the new
             ; first kid on the list
             (if (seq @shopping-list)
               (do
                 (go
                   (send-child-for-item kid (assign-item-to-child kid) kids))
                 (recur (<! kids)))
               (do
                 (println "done shopping")
                 (report))))))

(comment 
  "we can do the candy thing in two ways, either set a validator
  on the shopping cart, which is simpler but will require error
  handling when candy ends up in the cart"
         
  (def shopping-cart (ref #{}
    :validator #(not (contains? % :candy))))
  
  "or we can set a watch, which notifies the parent when candy ends up 
  in the cart")

(defn notify-parent [k r _ nv]
  (if (contains? nv :candy)
    (println "there's candy in the cart!")))

(defn init []
  (store-init {:eggs 2 :bacon 3 :apples 3 :candy 5 :soda 2 :milk 1
         :bread 3 :carrots 1 :potatoes 1 :cheese 3})
  (dosync
    (ref-set shopping-list #{:milk :butter :bacon :eggs :carrots
                             :potatoes :cheese :apples})
    (ref-set assignments {})
    (ref-set shopping-cart #{})
    (add-watch shopping-cart :candy notify-parent)))

(comment
  "local state with var
  
  Some things, like system state, don't require change management.
  Nor do things that won't be accessed by other threads. This is
  what Var is for. We use it all the time, and just above with my-kids
  
  We update with alter-var-root"
  
  (defn born! [new-kid]
    (alter-var-root #'my-kids conj new-kid))
  
  (born! :donnie) 
  ;;=> #{:alice :bobby :cindi :donnie}}
  my-kids
  ;;=> #{:alice :bobby :cindi :donnie}}

  "Probably avoid doing this, can get messy")


(comment "Living with change")

(comment
  "The reason we are leary of state is that it's easy to shoot yourself 
  in the foot. It happens when your programs get large, so it can be 
  tough to articulate in a blog or even a book. It's something that impacts
  large applications. But here are a few guidelines for managing your API")

(comment
  "validation - how and when to do it
  
  In the shopping example, grab ignores our API by grabbing things directly 
  from inventory, which could lead to having negative items in stock. 
  
  This is why we put no-negative-values? as a validator. but this is a 
  very expensive operation since it looks at every element of the map
  
  It's also used redundently, even when grab isn't called.
  
  A better way would be to construct a function and pass that around to
  the components that require it. This is looked at in more depth in C7
  
  The main problem here is that state can be accessed directly by grab.
  This is usually a bad idea. Make the storage private, and add to your
  API a set of functions for accessing it in the way you want.
  
  you can make a def private with metadata"
  
  (def ^{:private true} inventory [,,,])
  
  "validators work fine for small things though, and they can be a
  good centralized point of validation if you have many things which
  can change your identity. It does mean you'll have to handle exceptions.")


(comment
  "Runtime state and program (application) state"

  "We've been talking about application state mostly - ie the state
  that resides in the problem domain. These should be accessed via APIs
  which dictate how the state can be updated
  
  Runtime state relates to the software's execution. e.g. it might include
  refernces to databases, config files etc. Again we look at it more in C7
  but note that it's unavoidable, and minimizing it effectively means 
  minimizing configurability.")

(comment
  "try to minimize application state. Just enough and no more is the right
  amount. If it's there and you think you might be able to get along without
  it, remove it.
  
  Your entities should mostly be immutable values, your functions should
  mostly be pure. Keep functions that change state separate, and have them
  ONLY change state. Treat them like little plague carriers, you want to 
  fence them off.")
