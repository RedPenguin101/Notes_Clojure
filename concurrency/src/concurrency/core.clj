(ns concurrency.core)



(comment
  "BRAVE AND BOLD CHAPTER 9: Concurrent and parallel programming"
  " > Concurrency and paralellism concepts"
  " > Ref cells, mutual exclusion"
  " > futures, promises and delays")

(comment
  "CONCEPTS"
  "Concurrency is managing more than one task as once"
  "You can either INTERLEAVE them (switch btween them)"
  "Or do them both at the same time (PARALLELISM) - generally using multiple cores")

(comment
  "BLOCKING AND ASYNC"
  "SYNCHRONOUS EXECUTION is when you have one operation that BLOCKS another"
  "until the first one completes. ASYNC is when you can start the first op,"
  "put it aside and start the second one, then coe back from the result of"
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
  "A hypothetical API call"
  (web-api/get :the-weather)
  "will block execution of any other code until it's returned the result."
  "These tools let you break this apart")

(comment
  "FUTURES"
  "future defines a task and puts it on another thread"

  (future (Thread/sleep 400) (println "I'll print after 4 seconds"))
  (println "I'll print immediatelly")
 
  "in a synchonous world the former form would block the 2nd"
  
  "future returns a reference value (like a ticket) to access the result"
  "You need to deref the future to access the value"
  "if you try to deref before the thread has finished running, it will block")

(let [result (future (println "this prints once")
                     (+ 1 1))]
  (println "deref: " (deref result))
  ;; exactly equivalent to
  (println "@: " @result))

(comment "notice the result gets cached, so even though we accessed the result"
         "twice we only executed the body of the future once")

(comment "you can give a future a timeout and default return value"
         (future body timeout-ms default)
         "and interrogate it to ask if it's finished running"
         (realized? (future body)))

(deref (future (Thread/sleep 1000) 0) 10 5)

(comment "A use case for futures is logging - chuck it on another thread")

(comment
  "DELAYS"
  "define a task without executing it. Define with delay, force execution with force")

(def jackson-5-delay
  (delay (let [message "I'll be there"]
           (println "First deref: " message)
           message)))

(force jackson-5-delay)

(comment
  "A delay use case: sending a message when one of a group of futures finishes"
  
  (def headshots ["serious.jpg" "fun.jpg" "playful.jpg"])
  (defn email-user
    [email-address]
    (println "sending headshot to" email-address))
  (defn upload-document [headshot] true)

  (let [ notify (delay (email-user "hello@emmail.com"))]
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
  until a result is delivered"
  
  "A use case for promises is to search a collection for a satisfactory element")

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

(comment "This creates a promise, then 3 futures, each of which evaluate a
         butter and deliver it to the promise if it's satisfactory. the 
         deref in the final line blocks the main thread until the promise is
         delivered, and when it is, prints the result.
         Note promises can only be written to once
         Note as well that if the condition is never satisfied, it will never
         time out unless you tell it to, like"
         (let [p (promise)]
           deref p 100 "timed out"))

(time
  (let [butter-promise (promise)]
    (doseq [butter [yak-butter-international butter-than-nothing baby-got-yak]]
      (future (if-let [satisfactory-butter (satisfactory? (mock-api-call butter))]
                (deliver butter-promise satisfactory-butter))))
    (println @butter-promise)))

;takes 1 seconds
