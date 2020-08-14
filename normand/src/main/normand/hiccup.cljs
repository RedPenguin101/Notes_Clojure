(ns normand.hiccup
  (:require [reagent.core :as r]
            [clojure.string :as str]))

"Class and id shortcuts"

(defn cs [& args]
  (str/join " " (map name (filter identity args))))

(defn hiccup-panel []
  (let [title        "Show"
        dontshow     nil
        border-color "red"
        active?      nil]
    [:div
     [:div
      [:p#hiccup.big.centered "shortcuts! # for id (1 only) . for class"]
      [:p "use this for permanent stuff, use map/properties for dynamic"]]
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

