(ns markdownify.main
  (:require [reagent.core :as r]))

(defn app []
  [:h1 "Hello World"])

(defn mount! []
  (r/render [app]
            (.getElementById js/document "app")))

(defn main! []
  (mount!))

(defn reload! []
  (mount!))
