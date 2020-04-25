(ns app.auth.subs
  (:require [re-frame.core :as rf]))

(rf/reg-sub
  :logged-in?
  (fn [db _]
    (boolean (get-in db [:auth :uid]))))
