(ns rest-demo.core
  (:require [org.httpkit.server :as server]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer :all]
            [clojure.pprint :as pp]
            [clojure.string :as str]
            [clojure.data.json :as json])
  (:gen-class))

(comment
  "this is from"
  "https://medium.com/swlh/building-a-rest-api-in-clojure-3a1e1ae096e")

(declare simple-body-page)
(declare request-example)
(declare hello-name)
(declare people-handler)
(declare add-person-handler)
;; adding these here to retain narrative flow of tutorial

(def people (atom []))

(defn add-person [firstname surname]
  (swap! people conj {:firstname (str/capitalize firstname)
                      :surname (str/capitalize surname)}))

(add-person "Functional" "Human")
(add-person "Mickey" "Mouse")

(defroutes app-routes ; note these are in compojure
  (GET "/" [] simple-body-page)
  (GET "/request" [] request-example)
  (GET "/hello" [] hello-name)
  (GET "/people" [] people-handler)
  (GET "/people/add" [] add-person-handler)
  (route/not-found "Error, page not found!"))

;; main runs a HTTPKit server - this is the version using
;; ring middleware (wrap-defaults), but you can use it without. Ring MW gives us
;; querystring and cookie functions, which are generally required
;; note that site-defaults are only one option. in production you would
;; certainly want to use the https/ssl version, secure-site-defaults
(defn -main []
  (let [port (Integer/parseInt (or (System/getenv "PORT") "3000"))]
    (server/run-server (wrap-defaults #'app-routes site-defaults)
                       {:port port})
    (println (str "Running webserver at http://127.0.0.1:" port "/"))))

(defn simple-body-page [req]
  {:status  200
   :headers {"Content-Type" "text/html"}
   :body    "Hello World"})

(defn request-example [req]
  {:status  200
   :headers {"Content-Type" "text/html"}
   :body    (str "Request Object: " req (pp/pprint req))})

(defn hello-name [req]
  {:status  200
   :headers {"Content-Type" "text/html"}
   :body    (do (pp/pprint req) 
                (str "Hello, " (:name (:params req))))})

(defn people-handler [req]
  {:status  200
   :headers {"Content-Type" "text/html"}
   :body    (str (json/write-str @people))})

(defn add-person-handler [req]
  {:status  200
   :headers {"Content-Type" "text/html"}
   :body    (let [name (get req :params)]
              (str (json/write-str (add-person 
                                    (:firstname name) (:surname name)))))})
