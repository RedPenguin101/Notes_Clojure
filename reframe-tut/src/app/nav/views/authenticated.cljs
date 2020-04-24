(ns app.nav.views.authenticated
  (:require ["@smooth-ui/core-sc" :refer [Box]]))

(defn authenticated []
  [:> Box {:display         "flex"
           :justify-content "flex-end"
           :py              1}
   [:> Box {:as   "a"
            :href "#saved"
            :ml   2
            :pb   10}
    "Saved"]
   [:> Box {:as   "a"
            :href "#recipies"
            :ml   2
            :pb   10}
    "Recipies"]
   [:> Box {:as   "a"
            :href "#inbox"
            :ml   2
            :pb   10}
    "Inbox"]
   [:> Box {:as   "a"
            :href "#profile"
            :ml   2
            :pb   10}
    "Profile"]])
