(ns async-tut.core
  (:require [clojure.core.async :as a
             :refer [>! <! >!! <!! go chan buffer close! thread
                     alts! alts!! timeout]]))

;; https://github.com/clojure/core.async/blob/master/examples/walkthrough.clj

(comment "Data is transmitted on queues called channels."
         "values are transmitted through a channel, which accepts puts from a producer"
         "and queue values are read off (taken) by a consumer")

(a/chan)

(comment "Channels have buffers (by default 0)")

(a/chan 10)

(comment "a channel stops accepting puts with `close!`")

(let [c (a/chan)]
  (a/close! c))

(comment "use `>!!` and `<!!` to put and take values on a channel")

(let [c (a/chan 10)]
  (a/>!! c "hello")
  (assert (= "hello" (a/<!! c)))
  (a/close! c))

(comment "use thread like a future to execute a body in a pool thread and return"
         "a channel with the result. The following launches a background thread and"
         "accesses it on the main thread")

(let [c (a/chan)]
  (a/thread (a/>!! c "hello"))
  (assert (= "hello" (a/<!! c)))
  (a/close! c))

;; https://www.braveclojure.com/core-async/

(comment 
  "core.async allows creation of independent processes in a"
  "single program"
  "the PROCESS is at the heart of async. It's a concurrently"
  "running unit of logic that responds to events. Like processes"
  "that exist within your program")

(comment
  "Channels are message queues. You put messages on them and take them off"
  "Processes wait for the put or take to suceed before proceeding with"
  "execution"
  "the go block creates a new process in a new thread, and tells it what"
  "to do (in this case take from echo-chan and print)")

(def echo-chan (chan))
(go (println (<! echo-chan)))
(>!! echo-chan "ketchup")
(close! echo-chan)

(comment
  "the 'blocking put' we did blocks the process we executed until something"
  "takes the message. In the above the message was taken immediately, so there"
  "was not blocking, but if you did"
  (>!! (chan) "mustard")
  "the process (i.e. your REPL that is executing the code)"
  "will block, because nothing is taking the"
  "message from that channel")

(comment
  "you can give a channel a buffer, which allow it to stack messages until"
  "the buffer limit is reached, and only at that point will the process be"
  "blocked"
  (def echo-buffer (chan 2))
  (>!! echo-buffer "ketchup")
  (>!! echo-buffer "Mustard")
  (>!! echo-buffer "hot sauce") ;; will block here
  "Note you can Ctrl-C to kill the process and unblock your repl if needed")

(let [hi-chan (chan)]
  (doseq [n (range 1000)]
    (go (>! hi-chan (str "hi " n)))))

(defn hot-dog-machine []
  (let [in (chan) out (chan)]
    (go (<! in) (>! out "hotdog"))
    [in out]))

(let [[in out] (hot-dog-machine)]
  (>!! in "pocket lint")
  (<!! out))
;; => "hotdog"

