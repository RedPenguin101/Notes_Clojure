(ns giggin.core
  (:require [reagent.core :as r]
            [giggin.components.header :refer [header]]
            [giggin.components.gigs :refer [gigs]]
            [giggin.components.orders :refer [orders]]
            [giggin.components.footer :refer [footer]]))

(defn app
  []
  [:div.container
   [header]
   [gigs]
   [orders]
   [footer]])

(comment
  :div.container "is sugar for element with class container"
  :div#container "would be id container"
  "full version is like"
  {:div {:class "container"}})

(defn ^:export main []
  (r/render
    [app] ;; passes the function app as arg
    (.getElementById js/document "app") ; puts it to the element in html doc
                                        ; app
    ))

(comment
  ^:export is so can be accessed by index I think?
  )
