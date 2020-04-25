(ns app.auth.views.sign-up
  (:require [app.components.page-nav :refer [page-nav]]
            [app.components.form-group :refer [form-group]]
            [reagent.core :as r]
            [re-frame.core :as rf]
            ["@smooth-ui/core-sc" :refer [Box Row Col FormGroup Label Input Button]]))

(defn sign-up []
  (let [initial-values {:first-name "" :last-name ""
                        :email      "" :password  ""}
        values         (r/atom initial-values)]
    (fn []
      [:> Row {:justify-content "center"}
       [:> Col {:xs 12 :sm 6}
        [page-nav {:center "Sign Up"}]

        (form-group {:id     :first-name
                     :label  "First Name"
                     :type   :text
                     :values values})
        (form-group {:id     :last-name
                     :label  "Last Name"
                     :type   :text
                     :values values})
        (form-group {:id     :email
                     :label  "Email"
                     :type   :email
                     :values values})
        (form-group {:id     :password
                     :label  "Password"
                     :type   :password
                     :values values})

        [:> Box {:display         "flex"
                 :justify-content "space-between"}
         [:> Box {:py 1 :pr 2}
          [:a {:href     "#sign-up"
               :on-click #(rf/dispatch [:set-active-nav :log-in])}
           "Already have an account? Log in"]]
         [:> Box
          [:> Button {:on-click #(rf/dispatch [:sign-up @values])}
           "Sign up"]]]]
       ]))
  )
