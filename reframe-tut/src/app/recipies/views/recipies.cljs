(ns app.recipies.views.recipies
  (:require [app.components.page-nav :refer [page-nav]]))

(defn recipies []
  [page-nav {:center "Recipies"}])
