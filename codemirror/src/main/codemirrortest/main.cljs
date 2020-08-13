(ns codemirrortest.main
  (:require [reagent.core :as r]
            [cljsjs.codemirror]))

(def cm-value (atom "(+ 1 1)"))
(defonce cm (atom nil))

(reset! cm (js/CodeMirror. (.createElement js/document "div")
                           (clj->js {:lineNumbers       false
                                     :viewportMargin    js/Infinity
                                     :matchBrackets     true
                                     :autofocus         true
                                     :value             @cm-value
                                     :autoCloseBrackets true
                                     :mode              "clojure"})))


(defn ta []
  (r/create-class {:reagent-render (fn [] @cm [:div {:id "editor"}])
                   :component-did-mount
                   (fn [this]
                     (when @cm
                       (.appendChild (r/dom-node this) (.getWrapperElement @cm))))}))


(defn app []
  [:div
   [:h1 "Testing Codemirror"]
   [ta]])


(defn mount []
  (r/render [app]
            (.getElementById js/document "app")))

(defn main []
  (mount))

(defn reload []
  (mount))

