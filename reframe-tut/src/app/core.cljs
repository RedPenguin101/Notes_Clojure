(ns app.core
  (:require [reagent.core :as r]
            ["@smooth-ui/core-sc" :refer [Normalize Button]]))

(defn app []
  [:div
   [(r/adapt-react-class Normalize)]
   [(r/adapt-react-class Button) "hello"]
   [:div "Cheffy"]])

(defn ^:dev/after-load start
  []
  (r/render [app]
    (.getElementById js/document "app")))

(defn ^:export init
  []
  (start))
