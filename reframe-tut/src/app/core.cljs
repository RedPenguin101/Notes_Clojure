(ns app.core
  (:require [reagent.core :as r]
            ["@smooth-ui/core-sc" :refer [Normalize Button]]))

(defn app []
  [:<>
   [:> Normalize]
   [:> Button "hello"]
   [:div "Cheffy"]])

(defn ^:dev/after-load start
  []
  (r/render [app]
    (.getElementById js/document "app")))

(defn ^:export init
  []
  (start))
