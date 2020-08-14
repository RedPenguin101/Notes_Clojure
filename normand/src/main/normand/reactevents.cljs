(ns normand.reactevents
  (:require [reagent.core :as r]
            [reagent.dom :as rdom]
            [re-frame.core :as rf]
            [day8.re-frame.http-fx]
            [ajax.core :as ajax]))

"Accessing the DOM"
"you don't need to do this often - let react decide how to hanle that stuff"
"refs let you get at deeply nested dom nodes and save them when you want them"

(rf/reg-event-fx
  :save-image
  (fn [cofx [_ form-data]]
    {:http-xhrio {:uri            "https://whispering-cove-34851.herokuapp.comm/avatar"
                  :body           form-data
                  :method         :post
                  :timeout        10000
                  :reponse-format (ajax/json-response-format {:keywords? true})}}))

(defn form1subcomponent [link-text]
  [:p [:a {:href     "#/"
           :on-click (fn [e]
                       (.preventDefault e)
                       (js/console.log "Link"))} link-text]])

(defn counting-button [text]
  (let [state (r/atom 0)]
    (fn [text]
      [:button
       {:on-click #(swap! state inc)}
       ;; the virtual dom will notice the r/atom is changed, and will rerender
       (str text " -- " @state)])))


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
  (let [state (r/atom 0)
        refs  (r/atom nil)]
    [:div.top
     [:div.inner
      [:form {:on-submit (fn [e]
                           (.preventDefault e))
              ;; ref takes one argument, which is the form dom element
              ;; swap it into local state
              ;; ref will give you a null if unmounted
              :ref       #(reset! refs %)}
       [:input {:type      :file
                :name      :image
                :on-change (fn [e]
                             (.preventDefault e)
                             (js/console.log @refs)
                             (rf/dispatch [:save-image
                                           ;; get the actual real dom element
                                           (js/FormData. @refs)]))}]]]
     [:hr]
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
