(ns app.core
  (:require [reagent.core :as r]
            [re-frame.core :as rf]
            [app.db]
            ;; -- nav --
            [app.nav.events]
            [app.nav.subs]
            [app.nav.views.nav :refer [nav]]

            [app.auth.views.profile :refer [profile]]
            [app.auth.views.sign-up :refer [sign-up]]
            [app.auth.views.log-in :refer [log-in]]
            [app.inbox.views.inboxes :refer [inboxes]]
            [app.recipies.views.recipies :refer [recipies]]
            [app.become-a-chef.views.become-a-chef :refer [become-a-chef]]

            [app.theme :refer [cheffy-theme]]
            ["@smooth-ui/core-sc" :refer [Normalize Button ThemeProvider
                                          Grid Row Col]]))
"el"

(defn pages [page-name]
  (case page-name
    :profile       [profile]
    :sign-up       [sign-up]
    :log-in        [log-in]
    :become-a-chef [become-a-chef]
    :inboxes       [inboxes]
    :recipies      [recipies]
    [recipies]))

(defn app []
  (let [active-nav @(rf/subscribe [:active-nav])]
    [:<>
     [:> Normalize]
     [:> ThemeProvider {:theme cheffy-theme}
      [:> Grid (:fluid false)
       [:> Row
        [:> Col
         [nav]
         [pages active-nav]]]]]]))

(defn ^:dev/after-load start
  []
  (r/render [app]
            (.getElementById js/document "app")))

(defn ^:export init
  []
  (rf/dispatch-sync [:initialize-db])
  (start))
