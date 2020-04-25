(ns app.nav.views.nav
  (:require [app.nav.views.authenticated :refer [authenticated]]
            [re-frame.core :as rf]
            [app.nav.views.public :refer [public]]))

(defn nav []
  (let [logged-in? @(rf/subscribe [:logged-in?])]
    (if logged-in?
      [authenticated]
      [public])))
