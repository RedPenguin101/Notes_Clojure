(ns markdownify.main
  (:require [reagent.core :as r]
            ["showdown" :as showdown]))

(defonce markdown (r/atom ""))
(defonce html     (r/atom ""))

(defonce showdown-converter (showdown/Converter. ))

(defn md->html [md]
  (.makeHtml showdown-converter md))

(defn html->md [html]
  (.makeMarkdown showdown-converter html))

"https://hackernoon.com/copying-text-to-clipboard-with-javascript-df4d4988697f"
(defn copy-to-clipboard [s]
  (let [el       (.createElement js/document "textarea")
        selected (when (pos? (-> js/document .getSelection .-rangeCount))
                   (-> js/document .getSelection (.getRangeAt 0)))]
    (set! (.-value el) s)
    (.setAttribute el "readonly" "")
    (set! (-> el .-style .-position) "absolute")
    (set! (-> el .-style .-left) "-9999px")
    (-> js/document .-body (.appendChild el))
    (.select el)
    (.execCommand js/document "copy")
    (-> js/document .-body (.removeChild el))
    (when selected
      (-> js/document .getSelection .removeAllRanges)
      (-> js/document .getSelection (.addRange selected)))))

(defn app []
  [:div
   [:h1 "Markdownify"]
   [:div
    {:style {:display :flex}}
    [:div
     {:style {:flex "1"}}
     [:h2 "Markdown"]
     [:textarea {:on-change (fn [event]
                              (reset! markdown (-> event .-target .-value))
                              (reset! html (md->html @markdown)))
                 :value     @markdown
                 :style     {:resize "none"
                             :height "500px"
                             :width  "100%"}}]
     [:button {:on-click #(copy-to-clipboard @markdown)
               :style    {:background-color :green
                          :padding          "1em"
                          :color            :white
                          :border-radius    10}}
      "Copy Markdown"]]
    [:div
     {:style {:flex "1"}}
     [:h2 "HTML"]
     [:textarea {:on-change (fn [event]
                              (reset! html (-> event .-target .-value))
                              (reset! markdown (html->md @html)))
                 :value     @html
                 :style     {:resize "none"
                             :height "500px"
                             :width  "100%"}}]
     [:button {:on-click #(copy-to-clipboard @html)
               :style    {:background-color :green
                          :padding          "1em"
                          :color            :white
                          :border-radius    10}}
      "Copy html"]]
    [:div
     {:style {:flex         "1"
              :padding-left "2em"}}
     [:h2 "HTML preview"]
     [:div {:style                   {:height "500px"}
            :dangerouslySetInnerHTML {:__html @html}}]
     [:button {:on-click #(copy-to-clipboard @html)
               :style    {:background-color :green
                          :padding          "1em"
                          :color            :white
                          :border-radius    10}}
      "Copy HTML"]]
    ]])

(defn mount! []
  (r/render [app]
            (.getElementById js/document "app")))

(defn main! []
  (mount!))

(defn reload! []
  (mount!))
