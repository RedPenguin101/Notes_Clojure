(ns normand.main
  (:require [reagent.core :as r]
            [reagent.dom :as rd]
            [normand.hiccup :refer [hiccup-panel]]))

(defn app []
  [:div
   [:h1 "hello world 2"]
   [hiccup-panel]])

(defn mount []
  (rd/render [app]
             (.getElementById js/document "app")))

(defn main []
  (mount))

(defn reload []
  (mount))
