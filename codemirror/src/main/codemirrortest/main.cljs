(ns codemirrortest.main
  (:require [reagent.core :as r]
            ["codemirror/mode/clojure/clojure"]
            ["react-codemirror2" :refer [UnControlled]]))

(def value (r/atom "(defn hello [] {:this \"is\" :working \"well\"})"))

(defn cm []
  (fn []
    [:> UnControlled
     {:value     @value
      :options   {:mode "clojure"}
      :on-change (fn [_ _ v] (reset! value v))}]))

(defn app []
  [:div
   [:h1 "Testing Codemirror"]
   (prn-str @value)
   [cm]])


(defn mount []
  (r/render [app]
            (.getElementById js/document "app")))

(defn main []
  (mount))

(defn reload []
  (mount))

