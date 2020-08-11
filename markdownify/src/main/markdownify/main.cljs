(ns markdownify.main
  (:require [reagent.core :as r]
            ["showdown" :as showdown]))

(defonce markdown (r/atom ""))

(defonce showdown-converter (showdown/Converter. ))

(defn md->html [md]
  (.makeHtml showdown-converter md))

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

"
const copyToClipboard = str => {
  const el = document.createElement('textarea');
  el.value = str;
  el.setAttribute('readonly', '');
  el.style.position = 'absolute';
  el.style.left = '-9999px';
  document.body.appendChild(el);
  const selected =
    document.getSelection().rangeCount > 0
      ? document.getSelection().getRangeAt(0)
      : false;
  el.select();
  document.execCommand('copy');
  document.body.removeChild(el);
  if (selected) {
    document.getSelection().removeAllRanges();
    document.getSelection().addRange(selected);
  }
};
"

(defn app []
  [:div
   [:h1 "Markdownify"]
   [:div
    {:style {:display :flex}}
    [:div
     {:style {:flex "1"}}
     [:h2 "Markdown"]
     [:textarea {:on-change #(reset! markdown (-> % .-target .-value))
                 :value     @markdown
                 :style     {:resize "none"
                             :height "500px"
                             :width  "100%"}}]
     [:button {:on-click #(copy-to-clipboard @markdown)}
      "Copy Markdown"]]
    [:div
     {:style {:flex         "1"
              :padding-left "2em"}}
     [:h2 "HTML preview"]
     [:div {:style                   {:height "500px"}
            :dangerouslySetInnerHTML {:__html (md->html @markdown)}}]]
    #_[:div
       [:h2 "Raw HTML"]
       [:div (md->html @markdown)]]]])

(defn mount! []
  (r/render [app]
            (.getElementById js/document "app")))

(defn main! []
  (mount!))

(defn reload! []
  (mount!))
