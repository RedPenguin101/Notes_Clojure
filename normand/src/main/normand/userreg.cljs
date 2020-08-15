(ns normand.userreg
  (:require [reagent.core :as r]
            [reagent.dom :as rdom]
            [re-frame.core :as rf]))

"HTML forms best practices"
"Forms should be one component, much more convenient"
"Use an atom, then lock the values of the form into the atom, then
'round trip' changes into the atom with onchange or other events"

(defn user-registration [defaults]
  (let [state (r/atom defaults)]
    (fn []
      [:form {:on-submit (fn [e] (.preventDefault e))}
       [:div.dev {:style {:font-size     "0.5em"
                          :border        "1px solid red"
                          :margin-bottom 20}}
        @state]
       [:div
        [:label "First Name"
         [:input {:name  :first-name
                  ;; this is LOCKING the field - you can't change it because it's
                  ;; just going to slam over it whenever you try to make a change
                  ;; with what's in state
                  :value (:first-name @state)}]]]
       [:div
        [:label "Last Name"
         [:input {:name      :last-name
                  :value     (:last-name @state)
                  :on-change #(swap! state assoc :last-name (-> % .-target .-value))}]]]
       [:div
        [:label "Email"
         [:input {:name      :email
                  :type      :email
                  ;; here we're using a nested storage so we can put in metadata about
                  ;; the field
                  :value     (get-in @state [:email :value])
                  :on-focus  #(swap! state assoc-in [:email :touched?] true)
                  :on-change #(swap! state assoc-in [:email :value] (-> % .-target .-value))}]]]])))

(defn forms-panel []
  [:div [user-registration {:first-name "default"
                            :last-name  "default"}]])
