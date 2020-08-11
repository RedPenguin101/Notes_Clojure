(ns markdownify.main
  (:require [reagent.core :as r]
            ["showdown" :as showdown]))

(defonce markdown (r/atom ""))

(defonce showdown-converter (showdown/Converter. ))

(defn md->html [md]
  (.makeHtml showdown-converter md))

(defn app []
  [:div
   [:h1 "Hello World"]
   [:textarea {:on-change #(reset! markdown (-> % .-target .-value))
               :value     @markdown}]
   [:div {:dangerouslySetInnerHTML {:__html (md->html @markdown)}}]
   [:div (md->html @markdown)]])

(defn mount! []
  (r/render [app]
            (.getElementById js/document "app")))

(defn main! []
  (mount!))

(defn reload! []
  (mount!))
