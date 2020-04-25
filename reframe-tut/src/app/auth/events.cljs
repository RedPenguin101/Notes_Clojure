(ns app.auth.events
  (:require [re-frame.core :refer [reg-event-db]]))

(reg-event-db
  :log-in
  (fn [db [_ {:keys [email password]}]]
    (let [user              (get-in db [:users email])
          correct-password? (= password (get-in user [:profile :password]))]
      (cond
        (not user)
        (assoc-in db [:errors :email] "User not found")

        (not correct-password?)
        (assoc-in db [:errors :email] "Wrong Password")

        correct-password? (-> db
                              (assoc-in [:auth :uid] email)
                              (update-in [:errors] dissoc :email))))))
