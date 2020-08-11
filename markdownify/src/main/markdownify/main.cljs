(ns markdownify.main
  (:require [reagent.core :as r]))

(defonce markdown (r/atom ""))

(defn app []
  [:div
   [:h1 "Hello World"]
   [:textarea {:on-change #(reset! markdown (-> % .-target .-value))
               :value     @markdown}]])

(defn mount! []
  (r/render [app]
            (.getElementById js/document "app")))

(defn main! []
  (mount!))

(defn reload! []
  (mount!))
