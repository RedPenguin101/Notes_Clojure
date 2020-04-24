(ns app.nav.views.authenticated
  (:require ["@smooth-ui/core-sc" :refer [Box]]
            [reagent.core :as r]))

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
     (for [{:keys [id name href]} nav-items]
       [(r/adapt-react-class Box) {:key  id
                                   :as   "a"
                                   :href href
                                   :ml   2
                                   :pb   10}
        name])]))
