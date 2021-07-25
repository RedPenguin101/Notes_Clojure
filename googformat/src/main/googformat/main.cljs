(ns googformat.main
  (:require [reagent.core :as r]
            [reagent.dom :as rd]
            goog.i18n.NumberFormat
            goog.i18n.NumberFormat.Format
            goog.i18n.NumberFormatSymbols))


(defn number-format [n]
  (.format (goog.i18n.NumberFormat. goog.i18n.NumberFormat.Format.DECIMAL) n))

(number-format 1234.45)

(.format (goog.i18n.NumberFormat. goog.i18n.NumberFormat.Format.DECIMAL) 100000.12)
(.format (goog.i18n.NumberFormat. goog.i18n.NumberFormat.Format.CURRENCY "GBP") 100000)


(defn currency-format [ccy n]
  (.format (goog.i18n.NumberFormat. goog.i18n.NumberFormat.Format.DECIMAL (name ccy)) n))

(defn app []
  [:div
   [:h1 "hello world"]
   [:p (number-format 12345678.90)]])

(defn mount []
  (rd/render [app]
             (.getElementById js/document "app")))

(defn main []
  (mount))

(defn reload []
  (mount))