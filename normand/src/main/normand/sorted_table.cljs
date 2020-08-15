(ns normand.sorted-table
  (:require [reagent.core :as r]
            [clojure.string :as str]))

(def data [["Name" "Weapon" "Side" "Height (m)"]
           ["Luke Skywalker" "Blaster" "Light" 1.72]
           ["Leia Organa" "Blaster" "Light" 1.5]
           ["Han Solo" "Blaster" "Light" 1.8]
           ["Obi Wan Kenobi" "Lightsaber" "Light" 1.82]
           ["Chebacca" "Bowcaster" "Light" 2.28]
           ["Darth Vader" "Lightsaber" "Dark" 2.03]])


(defn sorted-table []
  (let [state (r/atom {:header (first data)
                       :data   (rest data)})
        s     (r/atom nil)]
    (fn []
      [:div
       [:div#dev {:style {:font-size "0.5em"
                          :border    "1px solid red"}}
        [:p #_(pr-str @state) (pr-str @s)]]
       [:table {:style {:font-size "0.8em"}}
        [:tr {:style {:background-color :gray}}
         (for [[h index] (map vector (:header @state) (range))]
           [:th {:on-click (fn [_]
                             (if (= index (:sort-key @s))
                               (swap! s update :sort-order not)
                               (swap! s assoc
                                      :sort-key index
                                      :sort-order false)))}
            h])]
        (for [row (cond->>  (:data @state)
                    (:sort-key @s)   (sort-by #(nth % (:sort-key @s)))
                    (:sort-order @s) reverse)]
          [:tr {:style {:background-color :gainsboro}}
           (for [v row]
             [:td  v])])]])))
