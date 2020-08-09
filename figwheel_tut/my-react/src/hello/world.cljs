(ns ^:figwheel-hooks hello.world
  (:require
   [reagent.dom :as r.dom]))

(defn hello []
  (println "hello, world"))

(js/console.log (hello))

(defn app []
  [:h1.site__title
   [:span.site__title-text "Hello, world"]])

(defn mount []
  (r.dom/render [app] (js/document.getElementById "app")))


(defn ^:after-load re-render []
  (mount))

(defonce start-up (do (mount) true))
