(ns main
  (:require [ring.adapter.jetty :as jetty]
            [compojure.core :as comp :refer [defroutes]]
            [clojure.data.json :as json]))

(defonce server (atom nil))

(defroutes app
  (comp/GET "/" [] {:status 200
                    :body (json/write-str {"Hello" "World"})
                    :headers {"Content-Type" "application/json"
                              "Access-Control-Allow-Origin" "http://localhost:9090"}}))

(defn start []
  (reset! server (jetty/run-jetty (fn [req] (app req)) {:port 3000 :join? false})))

(defn stop []
  (when-some [s @server]
    (.stop s)
    (reset! server nil)))

(comment
  (start)
  (stop))