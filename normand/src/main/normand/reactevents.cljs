(ns normand.reactevents
  (:require [reagent.core :as r]))

"Break big components into subcomponents when they get too big"

;; this returns a data structure
(defn form1subcomponent [link-text]
  [:p [:a {:href     "#/"
           :on-click (fn [e]
                       (.preventDefault e)
                       ;; this prevents the default behaviour, but doesn't stop
                       ;; the propagation
                       (js/console.log "Link"))} link-text]])

(defn react-panel []
  [:div.top
   [:div.wrapper
    {:on-click (fn [event]
                 (js/console.log "Wrapper gets triggered 2nd, because of propagation"))}
    [:div.inner
     {:on-click (fn [event]
                  (js/console.log "Inner gets triggered first"))}
     [form1subcomponent "passed in text"] ;; this will get turned into a react component
     (form1subcomponent "not a component") ;; this isn't a react component
     [:p "Other content"]]]])
