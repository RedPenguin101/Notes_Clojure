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


