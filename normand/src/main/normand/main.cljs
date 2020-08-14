(ns normand.main
  (:require [reagent.core :as r]
            [reagent.dom :as rd]
            [normand.hiccup :refer [hiccup-panel]]
            [normand.reactevents :as rp]))

(defn app []
  [:div
   [:h1 "hello world 2"]
   [:div
    [:h2 "React Panel"]
    [rp/react-panel]]
   [:div
    [:h2 "Hiccup Panel"]
    [hiccup-panel]]])

(defn mount []
  (rd/render [app]
             (.getElementById js/document "app")))

(defn main []
  (mount))

(defn reload []
  (mount))
