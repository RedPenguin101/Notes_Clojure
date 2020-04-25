(ns app.nav.views.public
  (:require ["@smooth-ui/core-sc" :refer [Box]]
            [re-frame.core :as rf]
            [app.nav.views.nav-item :refer [nav-item]]))

(defn public []
  (let [active-nav @(rf/subscribe [:active-nav])
        nav-items  [{:id       :recipies
                     :name     "Recipies"
                     :dispatch #(rf/dispatch [:set-active-nav :recipies])
                     :href     "#recipies"}
                    {:id       :become-a-chef
                     :name     "Chef"
                     :dispatch #(rf/dispatch [:set-active-nav :become-a-chef])
                     :href     "#become-a-chef"}
                    {:id       :sign-up
                     :name     "Sign up"
                     :dispatch #(rf/dispatch [:set-active-nav :sign-up])
                     :href     "#sign-up"}
                    {:id       :log-in
                     :name     "Log in"
                     :dispatch #(rf/dispatch [:set-active-nav :log-in])
                     :href     "#log-in"}]]
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
