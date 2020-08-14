(ns normand.reactevents
  (:require [reagent.core :as r]
            [reagent.dom :as rdom]))

"Types of components"

;; a form1 is a function that returns hiccup
;; it only rerenders when its arguments change
(defn form1subcomponent [link-text]
  [:p [:a {:href     "#/"
           :on-click (fn [e]
                       (.preventDefault e)
                       (js/console.log "Link"))} link-text]])

;; a form2 is a function that returns a function that returns hiccup
;; generally you'll use it for component local state
;; the outer function will only be called on mounting. The inner function will
;; be called whenever the component re-renders
(defn counting-button [text]
  (let [state (r/atom 0)]
    (fn [text]
      [:button
       {:on-click #(swap! state inc)}
       ;; the virtual dom will notice the r/atom is changed, and will rerender
       (str text " -- " @state)])))


;; form3 - very rare you'll need it
;; used for fine control on lifecycle
;; returns a create-class - creates the react class
;; you need it when you need to use the imperative API of something
;; the reagent-render method, when used alone, generates output equivalent to a form2
(defn canvas []
  (r/create-class
    (let [size (r/atom 10)
          id   (js/setInterval (fn [] (swap! size #(mod (inc %) 200)))
                               100)]
      {:reagent-render
       (fn []
         @size ;; need to deref in order to trigger the re-render
         ;; - note it just gets thrown away, only the last form (below) is returned
         [:canvas {:style {:width  200
                           :height 200
                           :border "1px solid green"}}])

       :display-name "canvas with sqaure"

       :component-did-mount ;;called on first mounting
       (fn [comp]
         (let [node (rdom/dom-node comp)
               ctx  (.getContext node "2d")]
           (.clearRect ctx 0 0 200 200)
           (.fillRect ctx 10 10 @size @size)))

       :component-did-update ;; after subsequent renders
       (fn [comp]
         (let [node (rdom/dom-node comp)
               ctx  (.getContext node "2d")]
           (.clearRect ctx 0 0 200 200)
           (.fillRect ctx 10 10 @size @size)))

       :component-will-unmount ;;this is like garbage collection for components
       (fn [comp]
         (js/clearInterval id))})))

(defn react-panel []
  (let [state (r/atom 0)]
    [:div.top
     #_[:div.canvas [canvas]]
     [counting-button "click me"]
     [:hr]
     [:div.wrapper
      {:on-click (fn [event]
                   (js/console.log "Wrapper gets triggered 2nd, because of propagation"))}
      [:div.inner
       {:on-click (fn [event]
                    (js/console.log "Inner gets triggered first"))}
       [form1subcomponent "passed in text"]
       (form1subcomponent "not a component")
       [:p "Other content"]]]]))
