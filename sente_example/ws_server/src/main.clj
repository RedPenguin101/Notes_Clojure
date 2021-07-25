(ns main
  (:require [org.httpkit.server :refer [run-server]]
            [compojure.core :as comp :refer [defroutes]]
            [clojure.data.json :as json]))

(defonce server (atom nil))

(defroutes routes
  (comp/GET "/" [] {:status 200
                    :body (json/write-str {"Hello" "World"})
                    :headers {"Content-Type" "application/json"
                              "Access-Control-Allow-Origin" "http://localhost:9090"}})
  ())

(def app (-> routes))

(defn start []
  (reset! server (run-server (fn [req] (app req)) {:port 3000 :join? false})))

(defn stop []
  (when-not (nil? @server)
    (@server :timout 100)
    (reset! server nil)))

(comment
  (start)
  (stop))