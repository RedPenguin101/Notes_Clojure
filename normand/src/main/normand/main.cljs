(ns normand.main
  (:require [reagent.core :as r]
            [reagent.dom :as rd]
            [re-frame.core :as rf]
            [normand.hiccup :refer [hiccup-panel]]
            [normand.userreg :refer [forms-panel]]
            [normand.sorted-table :refer [sorted-table]]
            [normand.drag-list :refer [drag-list]]
            [normand.inline :refer [inline]]
            [normand.reactevents :as rp]
            [normand.codemirrcomp :refer [codemirror]]))

(defn app []
  (let [db @(rf/subscribe [:all])]
    [:div
     [:div [codemirror "Hello, World" {:lineNumbers true} println]]
     [:hr]
     [:div.dev {:style {:border "1px solid red"}} [:p (prn-str db)]]
     [:div#drag-list [drag-list "a" "b" "c" "d"]]
     [:hr]
     [:div#inline-editable
      [inline]]
     [:hr]
     [:h1 "hello world 2"]
     [:div#table
      [:h2 "Sorted Table"]
      [sorted-table]]
     [:div#userform
      [:h2 "User Form"]
      [forms-panel]]
     [:div
      [:h2 "React Panel"]
      [rp/react-panel]]
     [:div
      [:h2 "Hiccup Panel"]
      [hiccup-panel]]]))


(rf/reg-event-db
 :initialize-db
 (fn [_ _]
   {:movies {"tt0095989" {:title "Return of the Killer Tomatoes!"
                          :description "Crazy old Professor Gangreen has developed a way to make tomatoes look human for a second invasion."}}}))

(rf/reg-sub
 :all
 (fn [db _]
   db))

(defn mount []
  (rd/render [app]
             (.getElementById js/document "app")))

(defn main []
  (rf/dispatch-sync [:initialize])
  (mount))

(defn reload []
  (mount))

(defonce _init (rf/dispatch-sync [:initialize-db]))

(comment
  (rf/dispatch-sync [:initialize-db]))