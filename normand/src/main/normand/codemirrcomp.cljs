(ns normand.codemirrcomp
  (:require [reagent.core :as reagent]
            [reagent.dom :as rdom]
            [cljsjs.codemirror]))

(defn create-codemirror [elem options]
  (js/CodeMirror.
   elem
   (clj->js options)))

(defn codemirror [initial-value options on-blur]
  (let [s (reagent/atom {:value initial-value})]
    (reagent/create-class
     {:reagent-render (fn [] [:div])
      :component-did-mount
      (fn [component]
        (let [editor (create-codemirror
                      (rdom/dom-node component)
                      (assoc options
                             :value initial-value))]
          (when on-blur (.on editor "blur"
                             #(on-blur (:value @s))))
          (.on editor "change"
               #(swap! s assoc :value (.getValue editor)))))})))