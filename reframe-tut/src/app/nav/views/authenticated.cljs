(ns app.nav.views.authenticated
  (:require ["@smooth-ui/core-sc" :refer [Box]]
            [app.nav.views.nav-item :refer [nav-item]]))

(defn authenticated []
  (let [nav-items [{:id   :saved
                    :name "Saved"
                    :href "#saved"}
                   {:id   :recipies
                    :name "Recipies"
                    :href "#recipies"}
                   {:id   :inbox
                    :name "Inbox"
                    :href "#inbox"}
                   {:id   :become-a-chef
                    :name "Chef"
                    :href "#become-a-chef"}
                   {:id   :profile
                    :name "Profile"
                    :href "#profile"}]]
    [:> Box {:display         "flex"
             :justify-content "flex-end"
             :py              1}
     (for [item nav-items]
       [nav-item item])]))
