(ns normand.drag-list
  (:require [re-frame.core :as rf]
            [reagent.core :as r]))

(defn change-pos [order before item]
  (if (empty? order)
    (list item)
    (let [[f & rst] order]
      (cond (= f before) (cons item (remove #{item} order))
            (= f item) (change-pos rst before item)
            :else (cons f (change-pos rst before item))))))

(defn drag-list [& items]
  (let [items (vec items) ;; vectorising makes lookup more efficient, can use get for cst time 
                          ;; instead of nth
        s (r/atom {:order (range (count items))})]
    (fn []
      [:div
       [:div.dev {:style {:border "1px solid red" :text "0.8em"}} @s]
       [:h2 "Drag list"]
       [:ul
        (for [i (:order @s)]
          [:li {:key i
                :draggable true
                :style {:border (when (= i (:drag-index @s)) "1px solid blue")}
                :on-drag-start #(swap! s assoc :drag-index i)
                :on-drag-end #(swap! s dissoc :drag-index :drag-over)
                :on-drag-over (fn [e]
                                (.preventDefault e)
                                (swap! s assoc :drag-over i)
                                (swap! s update :order change-pos (:drag-over @s) (:drag-index @s)))
                :on-drag-leave (fn [e]
                                 (swap! s assoc :drag-over :nothing)
                                 (swap! s update :order change-pos (:drag-over @s) (:drag-index @s)))}
           (get items i)])]])))