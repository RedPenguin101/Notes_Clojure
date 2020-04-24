(ns app.core
  (:require [reagent.core :as r]
            [re-frame.core :as rf]
            [app.db]
            [app.theme :refer [cheffy-theme]]
            [app.nav.views.nav :refer [nav]]
            ["@smooth-ui/core-sc" :refer [Normalize Button ThemeProvider]]))
"el"

(defn app []
  [:<>
   [:> Normalize]
   [:> ThemeProvider {:theme cheffy-theme}
    [nav]]])

(defn ^:dev/after-load start
  []
  (rf/dispatch-sync [:initialize-db])
  (r/render [app]
            (.getElementById js/document "app")))

(defn ^:export init
  []
  (start))
