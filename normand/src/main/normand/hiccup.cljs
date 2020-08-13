(ns normand.hiccup
  (:require [reagent.core :as r]))

"How to deal with HTML attributes that are either there or not there - like checked"

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
     [:p [:a {:href "#/"} "Home"]]
     [:input {:type    :checkbox
              :checked title}]
     [:input {:type    :checkbox
              :checked dontshow}]
     [:input {:type    :checkbox
              :checked {}}]]))

":checked will show up in your html if the value for the key is truthy"
