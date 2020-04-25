(ns app.auth.views.profile
  (:require [app.components.page-nav :refer [page-nav]]
            [app.components.form-group :refer [form-group]]
            [reagent.core :as r]
            [re-frame.core :as rf]
            ["@smooth-ui/core-sc" :refer [Row Col Box Typography Button]]))

(defn profile []
  (let [values (r/atom {:first-name ""
                        :last-name  ""})]
    (fn []
      [:<>
       [page-nav {:center "Profile"
                  :right  [:> Button {:variant  "light"
                                      :on-click #(rf/dispatch [:log-out])}
                           "Log out"]}]
       [:> Row {:justify-content "center"}
        [:> Col {:xs 12 :sm 6}
         [:> Box {:background-color "white"
                  :border-radius    10
                  :p                3
                  :pt               1}
          [:> Typography {:variant     "h4"
                          :py          10
                          :font-weight 700}
           "Personal Info"]
          [form-group {:id     :first-name
                       :label  "First Name"
                       :type   "text"
                       :values values}]
          [form-group {:id     :last-name
                       :label  "Last Name"
                       :type   "text"
                       :values values}]
          [:> Box {:display         "flex"
                   :justify-content "flex-end"}
           [:> Button {:on-click #(rf/dispatch [:update-profile @values])}
            "Save"]]]]]])))
