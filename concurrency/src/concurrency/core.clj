(ns concurrency.core
  (:require [clojure.core.async 
             :as a
             :refer [>! <! >!! <!! go chan buffer close! 
                     thread alts! alts!! timeout]]))


(comment
  "BRAVE AND BOLD CHAPTER 9: Concurrent and parallel programming"
  " > Concurrency and paralellism concepts"
  " > Ref cells, mutual exclusion"
  " > futures, promises and delays")

(comment
  "CONCEPTS"
  "Concurrency is managing more than one TASK at once"
  "You can either INTERLEAVE them (switch btween them)"
  "Or do them both at the same time (PARALLELISM) - generally using multiple cores")

(comment
  "BLOCKING AND ASYNC"
  "SYNCHRONOUS EXECUTION is when you have one operation that BLOCKS another"
  "until the first one completes. ASYNC is when you can start the first op,"
  "put it aside and start the second one, then come back from the result of"
  "the first when it's ready")

(comment
  "JVM THREADS"
  "Normal serial code is a series of tasks. You can tell the JVM to execute"
  "them concurrently by putting them on a JVM Thread"
  "A Thread is a SUBPROGRAM. A thread can SPAWN a new thread and give it tasks"
  "to execute - and interleaves on a single core processor")

(comment 
  "REF CELLS, MUTUAL EXCLUSION AND DEADLOCK"
  "A threaded program is NONDETERMINISTIC, becuase there's no guarantee of"
  "the order the tasks on the different threads will execute"
  "This causes issues with shared state")

(comment
  "FUTURES DELAYS AND PROMISES"
  "These are easy, lightweight tools."
  "Serial code binds together task definition, execution, and requiring result"
  "A hypothetical API call:"

  (web-api/get :the-weather)

  "will block execution of any other code until it's returned the result."
  "These tools let you break this apart")

(comment
  "FUTURES define a task and puts it on another thread"

  (future (Thread/sleep 400) (println "I'll print after 4 seconds"))
  (println "I'll print immediatelly")
 
  "in a synchonous world the former form would block the 2nd"
  
  "future returns a reference value (like a ticket) to access the result"
  "You need to deref the future to access the value"
  "if you try to deref before the thread has finished running, it will block"

  (let [result (future (println "this prints once")
                      (+ 1 1))]
    (println "deref: " (deref result))
    ;; exactly equivalent to
    (println "@: " @result))
  
  "notice the result gets cached, so even though we accessed the result 
  twice we only executed the body of the future once")

(comment 
  "you can give a future a timeout and default return value"
  (future body timeout-ms default)
  (deref (future (Thread/sleep 1000) 0) 10 5)

  "and interrogate it to ask if it's finished running"
  (realized? (future body)))

(comment "A use case for futures is logging - chuck it on another thread")

(comment
  "DELAYS"
  "define a task without executing it. Define with delay, force execution with force"

  (def jackson-5-delay
    (delay (let [message "I'll be there"]
            (println "First deref: " message)
            message)))

  (force jackson-5-delay))

(comment
  "A delay use case: sending a message when one of a group of futures finishes"
  
  (def headshots ["serious.jpg" "fun.jpg" "playful.jpg"])
  (defn email-user
    [email-address]
    (println "sending headshot to" email-address))
  (defn upload-document [headshot] true)

  (let [notify (delay (email-user "hello@emmail.com"))]
    (doseq [headshot headshots]
      (future (upload-document headshot)
              (force notify))))

  "notice this avoid te mutual exclusion problem: the delay is guarding the
  email server resource, and it can only be fired one")

(comment
  "PROMISES"
  "Allow you to say you expect a result without saying what the task is,
  or what you expect the results to be.
  Create with promise, deliver with deliver, deref the result"
  
  (def my-promise (promise))
  (deliver my-promise (+ 1 2))
  (deref my-promise)

  "id you had tried to deref before delivering, the program would block
  until a result is delivered")

(comment "A use case for promises is to search a collection for a satisfactory element"

(def yak-butter-international
    {:store  "Yak Butter International"
         :price 90
             :smoothness 90})
(def butter-than-nothing
    {:store  "Butter Than Nothing"
        :price 150
           :smoothness 83})

;; This is the butter that meets our requirements
(def baby-got-yak
    {:store  "Baby Got Yak"
        :price 94
           :smoothness 99})

(defn mock-api-call
    [result]
      (Thread/sleep 1000)
        result)

(defn satisfactory?
    "If the butter meets our criteria, return the butter, else return false"
      [butter]
        (and  (<= (:price butter) 100)
                    (>= (:smoothness butter) 97)
                           butter))

(time (some (comp satisfactory? mock-api-call)
            [yak-butter-international butter-than-nothing baby-got-yak]))

; takes 3 seconds

 "This creates a promise, then 3 futures, each of which evaluate a
butter and deliver it to the promise if it's satisfactory. the 
deref in the final line blocks the main thread until the promise is
delivered, and when it is, prints the result.
Note promises can only be written to once
Note as well that if the condition is never satisfied, it will never
time out unless you tell it to, like"

 (let [p (promise)]
    deref p 100 "timed out")

  (time
    (let [butter-promise (promise)]
      (doseq [butter [yak-butter-international butter-than-nothing baby-got-yak]]
        (future (if-let [satisfactory-butter (satisfactory? (mock-api-call butter))]
                  (deliver butter-promise satisfactory-butter))))
      (println @butter-promise)))

  ;takes 1 seconds
  )

;; ==========================  CORE ASYNC ========================================

(comment "Create multiple independent processes within a single program
         > CHANNELS - like message queues - to communicate
         > GO BLOCKS and thread to pass them
         > parking and blocking
         > alts!!
         > commen patterns, queues and callbacks")

(comment 
  "the process is at the heart of async - a concurrently running unit of logic
  works with our mental model of the real world - the vending machine"
  
  "this is a process that just returns the message it receives"
  
  (def echo-chan (chan))
  (go (println (<! echo-chan)))
  (>!! echo-chan "ketchup")
  
  "The first line creates a channel, which is like a message queue
  A process puts messages on the queue with >!! and take them with <!
  Processes that put a message on the queue wait until it is taken
  before contining, like a baton in a relay race
  
  The go block creates a new process, which executes everyhing in
  the block body on a separate thread. This go block waits for a message
  to be put on echo-chan, takes it, and prints it. The process then shuts down"
  
  "the 3rd line puts the string 'ketchup' on the channel. and returns true
  Since the go block is waiting for the message, it will pick it up and 
  execute it, so you'll see 'true ketchup' in the REPL
  If you were to put a second message on the channel, it would block, 
  because there's no 2nd process waiting to pick it up.")

(comment
  "BUFFERING a channel is a way to say what the maximum queue capacity
  of a channel is. Default is 0, so any process that puts a message on
  a channel will have to wait until it's taken. If you set the buffer to two,
  the process can put a message on, and continue to execute, possibly putting
  another message on the queue, still executing. But if it tries to put a 3rd
  message on the queue without something picking them up on the other side,
  the process will block"
  
  (def echo-buffer (chan 2))
  (>!! echo-buffer "ketchup")
  ;; true
  (>!! echo-buffer "ketchup")
  ;; true
  (>!! echo-buffer "ketchup")
  ;; will block, buffer is full
  )

(comment
  "BLOCKING AND PARKING and THREAD
  Use only '<!!' or '>!!' outside go blocks
  Inside go blocks you can use the single or double bang flavours
  
  The reason has to do with efficiency. Won't go into detail, but basically
  go blocks get assigned to a common pool of threads. You computer has 2+cores
  threads available. So if you start 100 go blocks, they will be distributed
  around the 6 or so threads.
  
  blocking (!!) and parking (!) are two different types of waiting.
  When told to block-wait, a thread will block any execution until the task it's
  been assigned has been completed. When told to park-wait, the thread can switch
  between two tasks it's been given as it sees fit.
  
  Use blocking puts and takes in your async when you thing the process will take
  a long time. Instead of a go block, use a thread, which as it suggests spins up
  a new thread for the process you give it. Do this so you don't jam up the thread
  pool
  
  A thread returns a channel, and then
  goes away and runs the process. When it's done executing the process it puts
  the results of the process on that channel, where another process can pick it up"
  
  (thread (println (<!! echo-chan)))
  (>!! echo-chan "mustard") 

  (let [t (thread "chili")]
    ;; returns channel t
    (<!! t)))


(comment "A HOTDOG VENDING MACHINE")

(defn hot-dog-machine
  "creates an in channel for recieving money and an out channel for dispensing
  a hotdog. The go block waits fo input, then when it recieves it it puts a hot
  dog on the out channel. It then returns the in and out channels as a vector
  so they can be used by client code"
  []
  (let [in (chan)
        out (chan)]
    (go (<! in)
        (>! out "hot dog"))
    [in out]))

(comment
  (let [[in out] (hot-dog-machine)]
    (>!! in "pocket lint")
    (<!! out))
  )


(defn hot-dog-machine-v2
  "A better hot dog machine that contains a set number of hot dogs, and stops
  dispensing if the hot-dog count is 0, and checks whether the input is 3"
  [hot-dog-count]
  (let [in (chan)
        out (chan)]
    (go (loop [hc hot-dog-count]
          (if (> hc 0)
            (let [input (<! in)]
              (if (= 3 input)
                (do (>! out "hot dog")
                    (recur (dec hc)))
                (do (>! out "NOTHING")
                    (recur hc))))
            (do (close! in)
                (close! out)))))
    [in out]))

(comment 
  (let [[in out] (hot-dog-machine-v2 2)]
    (>!! in "pocket lint")
    (println (<!! out))
    
    (>!! in 3)
    (println (<!! out))
    
    (>!! in 3)
    (println (<!! out))
    
    (>!! in 3)
    (<!! out))

  "Doing a put and take within the same go block is a common pattern
  when you're creating a PIPELINE of processes. You'll often see a process
  outputing directly to the input channel of another process"

  (let [c1 (chan)
        c2 (chan)
        c3 (chan)]
    (go (>! c2 (clojure.string/upper-case (<! c1))))
    (go (>! c3 (clojure.string/reverse (<! c2))))
    (go (println (<! c3))) 
    (>!! c1 "redrum"))
  ;; => MURDER
  
  "We'll see later how pipelines can be used instead of callbacks later")

(comment
  "ALTS!! lets you use the result of the first successful channel operation
  from a collections of operations. This is similar to the headshot example
  above where we used delay to define a task, then execute it only when one
  of several futures we defined hsa resolved")

(defn upload
  [headshot channel]
  (go (Thread/sleep (rand 100)) ;faking the upload process
      (>! channel headshot))) ;putting the headshot to the channel

(comment
  (let [c1 (chan)
        c2 (chan)
        c3 (chan)]
    (upload "serious.jpg" c1)
    (upload "fun.jpg" c2)
    (upload "sassy.jpg" c3)
    (let [[headshot channel] (alts!! [c1 c2 c3])]
      (println "Sending headshot notitication for" headshot)))
  
  "The alts here is like saying 'try to do a blocking take on all
  of these channels, and as soon as one of them suceeds return the taken
  value and the winning channel"
  
  "You can use alts as mechanism for putting time limits on concurrent
  operations by passing it a timeout channel"
  
  (timeout 20)
  
  "which will resolve after 20ms. If that's the first thing that resolves, then
  this will be the thing that alts gets"

  (let [c1 (chan)]
    (upload "serious.jpg" c1)
    (let [[headshot channel] (alts!! [c1 (timeout 20)]) ]
      (if headshot
        (println "sending headshot notification for" headshot)
        (println "timeout")))))

(comment
  "you can use alts to specify put operations, by passing a nested vector"
  
  (let [c1 (chan) c2 (chan)]
    (go (<! c2))
    (let [[value channel] (alts!! [c1 [c2 "put me"]])]
      (println value)
      (= channel c2)))
  
  "this returns true true, the first from the result of putting 'put me' on c2
  the second from checking the channel c2 is the returned channel")

(comment 
  "QUEUES"
  "say you want to get random quotes from a website and write them to a file"
  "you want to make sure only one is written at a time so they don't get garbled")

(defn append-to-file
  "Write a string to the end of a file"
  [filename string]
  (spit filename string :append true))

(defn format-quote
  "Deliniate the start and end of a quote"
  [quote]
  (str "=== BEGIN QUOTE ===\n" quote "=== END QUOTE ===\n\n"))

(defn random-quote
  []
  (format-quote (slurp "https://www.braveclojure.com/random-quote")))

(defn snag-quotes [filename num-of-quotes]
  (let [c (chan)]
    (go (while true (append-to-file filename (<! c))))
    (dotimes [n num-of-quotes] (go (>! c (random-quote))))))

(comment (snag-quotes "quotes.txt" 3))

(comment
  "PIPELINES INSTEAD OF CALLBACKS
  Without channels, you have to express 'when x happens, do y' with callbacks
  This can lead to callback hell, where you have dependencies between layers
  of callbacks that aren't obvious, which makes it difficult to reason about
  your code. You get around that with process pipelines
  
  In a pipeline, each unit of logic is isolated, and the communication between
  processes is clearly defined")

(defn upper-caser [in]
  (let [out (chan)]
    (go (while true (>! out (clojure.string/upper-case (<! in)))))
    out))

(defn reverser [in]
  (let [out (chan)]
    (go (while true (>! out (clojure.string/reverse (<! in)))))
    out))

(defn printer [in]
  (go (while true (println (<! in)))))

(def in-chan (chan))
(def upper-caser-out (upper-caser in-chan))
(def reverser-out (reverser upper-caser-out))
(printer reverser-out)

(comment
  (>!! in-chan "redrum"))
