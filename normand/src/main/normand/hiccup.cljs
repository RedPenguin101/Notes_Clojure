(ns normand.hiccup
  (:require [reagent.core :as r]))

(defn hiccup-panel []
  (let [title    "Show"
        dontshow nil]
    [:div
     nil ;; nil/false is ignored - useful
     (when title [:h1 title])
     false
     (when dontshow [:h1 "Don't show!"])
     [:div {:class "content"}
      [:p "this is in the content class div"]]
     [:p [:a {:href "#/"} "Home"]]]))
