(ns app.core
  (:require [reagent.core :as r]
            [re-frame.core :as rf]
            [app.db]
            ;; -- nav --
            [app.nav.events]
            [app.nav.subs]
            [app.nav.views.nav :refer [nav]]

            [app.auth.views.profile :refer [profile]]
            [app.inbox.views.inboxes :refer [inboxes]]
            [app.recipies.views.recipies :refer [recipies]]
            [app.become-a-chef.views.become-a-chef :refer [become-a-chef]]

            [app.theme :refer [cheffy-theme]]
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
