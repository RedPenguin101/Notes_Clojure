(ns normand.reactevents
  (:require [reagent.core :as r]))

"lists keys and lazyness"

;; this returns a data structure
(defn form1subcomponent [link-text]
  [:p [:a {:href     "#/"
           :on-click (fn [e]
                       (.preventDefault e)
                       ;; this prevents the default behaviour, but doesn't stop
                       ;; the propagation
                       (js/console.log "Link"))} link-text]])

(def pets [{:type "Dog" :name "Fido" :noise "woof"}
           {:type "Cat" :name "Mittens" :noise "meow"}
           {:type "Snake" :name "Snape" :noise "hiss"}
           {:type "Tarantula" :name "Cuddles" :noise "I will kill you in your sleep"}])


(defn pets-component [pet]
  [:div.pet
   [:h3 (:type pet)]
   [:p (str (:name pet) " is a " (:type pet))]
   [:p (str "'" (:noise pet) "'")]])


(defn react-panel []
  (let [state (r/atom 0)]
    [:div.top
     (doall (for [pet pets]
              [:div.petwrap
               {:key (str (:name pet) (:type pet))}
               ;; need a unique, stable key when listing
               ;; could also use ^{:key name} - the metadata method
               @state
               ;; this will generate a warning because for creates a lazy seq
               ;; use a doall around your for if you deref state
               [pets-component pet]]))
     [:hr]
     [:div.wrapper
      {:on-click (fn [event]
                   (js/console.log "Wrapper gets triggered 2nd, because of propagation"))}
      [:div.inner
       {:on-click (fn [event]
                    (js/console.log "Inner gets triggered first"))}
       [form1subcomponent "passed in text"] ;; this will get turned into a react component
       (form1subcomponent "not a component") ;; this isn't a react component
       [:p "Other content"]]]]))
