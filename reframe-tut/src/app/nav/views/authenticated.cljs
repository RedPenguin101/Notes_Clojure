(ns app.nav.views.authenticated
  (:require ["@smooth-ui/core-sc" :refer [Box]]
            [re-frame.core :as rf]
            [app.nav.views.nav-item :refer [nav-item]]))

(defn authenticated []
  (let [active-nav @(rf/subscribe [:active-nav])
        nav-items  [{:id       :saved
                     :name     "Saved"
                     :dispatch #(rf/dispatch [:set-active-nav :saved])
                     :href     "#saved"}
                    {:id       :recipies
                     :name     "Recipies"
                     :dispatch #(rf/dispatch [:set-active-nav :recipies])
                     :href     "#recipies"}
                    {:id       :inboxes
                     :name     "Inbox"
                     :dispatch #(rf/dispatch [:set-active-nav :inboxes])
                     :href     "#inbox"}
                    {:id       :become-a-chef
                     :name     "Chef"
                     :dispatch #(rf/dispatch [:set-active-nav :become-a-chef])
                     :href     "#become-a-chef"}
                    {:id       :profile
                     :name     "Profile"
                     :dispatch #(rf/dispatch [:set-active-nav :profile])
                     :href     "#profile"}]]
    [:> Box {:display         "flex"
             :justify-content "flex-end"
             :py              1}
     (for [{:keys [id name href dispatch]} nav-items]
       [nav-item {:key        id
                  :id         id
                  :name       name
                  :href       href
                  :dispatch   dispatch
                  :active-nav active-nav}])]))
