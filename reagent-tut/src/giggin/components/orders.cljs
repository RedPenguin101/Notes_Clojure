(ns giggin.components.orders
  (:require [giggin.state :as state]
            [giggin.helpers :refer [format-price]]))

(defn total []
  (reduce + (for [[id quant] @state/orders]
              (* quant (get-in @state/gigs [id :price])))))

(defn orders []
  [:aside
   (if (empty? @state/orders)
     [:div.empty
      [:div.title "You don't have any orders"]
      [:div.subtitle "Click on a plus to add an order"]]
     [:div.order
      [:div.body
       (for [[id quant] @state/orders]
         [:div.item {:key id}
          [:div.img
           [:img {:src (get-in @state/gigs [id :img])
                  :alt (get-in @state/gigs [id :title])}]]
          [:div.content
           [:p.title (str (get-in @state/gigs [id :title]) " \u00D7 " quant)]]
          [:div.action
           [:div.price (format-price (* (get-in @state/gigs [id :price]) quant))]
           [:button.btn.btn--link.tooltip
            {:data-tooltip "remove"
             :on-click     #(swap! state/orders dissoc id)}
            [:i.icon.icon--cross]]]])]
      [:div.total
       [:hr]
       [:div.item
        [:div.content "Total"]
        [:div.action
         [:div.price (format-price (total))]]
        [:button.btn.btn--link.tooltip
         {:data-tooltip "Remove all"
          :on-click     #(reset! state/orders {})}
         [:i.icon.icon--delete]]]]])])
