(ns clojureapplied.chapter5)

(comment "chapter 5: use your cores")

(comment
  "The goals of parallel programming is to move work off the main thread
  and onto another one, let it do it's thing, then get the result back 
  when it's ready. We can use future and promise for this
  
  Another approach is task-oriented concurrency, where tasks are farmed
  out to a pool of worker threads. Clojure utlises Java's strong tools
  for queues to manage these
  
  Sometimes we want to implement finegrained work over collections, while
  taking advantage of multiple cores. reducers allow us to do this
  
  Lastly we want to use threads to structure our program. this is what we
  use channels and go-blocks for"
  )

(comment "push waiting to the background")

(comment 
  "i/o is usually the system bottleneck, we spend a lot of time waiting for 
  it. We want to do other stuff while we're waiting. 
  
  Say we have an app which increments a page-view stat"
  
  (inc-stat :pageview)
  
  "this is calling an external resource, so it will be slow. We want to
  move it to the background. We can do this with"
  
  (future (inc-stat :pageview)))

(comment "queues and workers")

(comment "parallelism with reducers")

(comment "thinking in processes")
