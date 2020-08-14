(ns normand.reactevents
  (:require [reagent.core :as r]))

"click events propogate outwards - in the below, when clicking the some content para, both the Inner Div and wrapper div with be triggered"

(defn react-panel []
  [:div.top
   {:on-click (fn [event]
                ;; This won't get triggered if we have this method
                (.stopPropagation event))}
   [:div.wrapper
    {:on-click (fn [event]
                 (js/console.log "Wrapper gets triggered 2nd, because of propagation"))}
    [:div.inner
     {:on-click (fn [event]
                  (js/console.log (.-target event))
                  ;; the p that was clicked is the target
                  (js/console.log "Inner gets triggered first"))}
     [:p [:a {:href     "#/"
              :on-click (fn [e]
                          (.preventDefault e)
                          ;; this prevents the default behaviour, but doesn't stop
                          ;; the propagation
                          (js/console.log "Link"))}"some content"]]
     [:p "Other content"]]]])
