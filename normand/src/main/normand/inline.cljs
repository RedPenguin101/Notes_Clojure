(ns normand.inline
  (:require [re-frame.core :as rf]
            [reagent.core :as r]))

(rf/reg-sub
 :movies
 (fn [db _]
   (:movies db)))

(rf/reg-event-db
 :title
 (fn [db [event id title]]
   (do 
     (assoc-in db [:movies id :title] title))))

(rf/reg-event-db
 :description
 (fn [db [_ id description]]
   (assoc-in db [:movies id :description] description)))

(defn editable [text id update-event]
  (let [s (r/atom {:text text})]
    (fn [text id update-event]
      [:div
       #_[:div.dev {:style {:border "1px solid red"}}
        (prn-str @s)]
       
       (if (:editing? @s)
         [:form {:on-submit #(do (.preventDefault %)
                                 (rf/dispatch [update-event id (:text @s)])
                                 (swap! s update :editing? not))}
          [:input {:value (:text @s)
                   :on-change #(swap! s assoc :text (-> % .-target .-value))}]
          [:button "save"]]
         [:div
          [:span text
           [:sup {:on-click #(do (js/console.log "edit clicked")
                                 (swap! s update :editing? not))}
            "✎"]]])])))

(defn inline []
  (fn []
    [:div
     [:h2 "Inline editable"]
     (for [[key movie] @(rf/subscribe [:movies])]
       [:div {:key key}
        [:h3 {}
         [editable (:title movie) key :title]]
        [:div [editable (:description movie) key :description]]])]))
