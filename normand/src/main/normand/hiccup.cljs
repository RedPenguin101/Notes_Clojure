(ns normand.hiccup
  (:require [reagent.core :as r]))

"Attributes in hiccup"
"common to style inline in react and reagent"
"Use a map"
"inline is good because you can use stateful properties"

(defn hiccup-panel []
  (let [title        "Show"
        dontshow     nil
        border-color "red"]
    [:div
     nil ;; nil/false is ignored - useful
     (when title [:h1 title])
     false
     (when dontshow [:h1 "Don't show!"])
     [:div {:class "content"
            :style {:margin-left 100}} ;; integer is interpreted as px. Else use string
      [:p {:style {:border (when border-color
                             (str "1px solid " border-color))}}
       "this is in the content class div"]]
     [:p [:a {:href "#/"} "Home"]]
     [:input {:type    :checkbox
              :checked title}]
     [:input {:type    :checkbox
              :checked dontshow}]
     [:input {:type    :checkbox
              :checked {}}]]))

