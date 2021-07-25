(ns ws-client.main
  (:require [reagent.core :as r]
            [re-frame.core :as rf]
            [reagent.dom :as rd]
            [day8.re-frame.http-fx]
            [ajax.core :as ajax]
            [cljs.core.async :as async :refer [<! >! put! chan]]
            [taoensso.sente  :as sente :refer (cb-success?)]))

;; Sente Stuff

(defn handler [] {:hello "world"})

(def router_ (atom nil))
(def ch-chsk (atom nil))
(def chsk-send! (atom nil))
(def chsk-state (atom nil))

(def config {:type     :auto
             :packer   :edn
             :protocol :http
             :host     "localhost"
             :port     3000})

(defn create-client! []
  (let [{:keys [ch-recv send-fn state]} (sente/make-channel-socket-client! "/chsk" nil config)]
    (reset! ch-chsk ch-recv)
    (reset! chsk-send! send-fn)
    (reset! chsk-state state)))

(defn stop-router! []
  (when-let [stop-f @router_] (stop-f)))

(defn start-router! []
  (stop-router!)
  (reset! router_ (sente/start-client-chsk-router! @ch-chsk handler)))

(defn start-client! []
  (create-client!)
  (start-router!))

;; events

(rf/reg-event-db
 :initialize-db
 (fn [_ _]
   {:box-text "Started"
    :result "no result yet"}))

(rf/reg-event-fx
 :handler-with-http
 (fn [{:keys [db]} _]
   {:db (assoc db :box-text "Calling")
    :http-xhrio {:method :get
                 :uri "http://localhost:3000/"
                 :timeout 1000
                 :response-format (ajax/json-response-format {:keywords? true})
                 :on-success [:good-http-result]
                 :on-failure [:bad-http-result]}}))

(rf/reg-event-db
 :good-http-result
 (fn [db [_ result]]
   (-> db
       (assoc :box-text "Good Result!")
       (assoc :result result))))

(rf/reg-event-db
 :bad-http-result
 (fn [db [_ result]]
   (-> db
       (assoc :box-text "Bad Result!")
       (assoc :result result))))

(rf/reg-event-db
 :connect
 (fn [db []]
   (start-client!)
   (assoc db :connected true)))

(rf/reg-sub :box-text (fn [db _] (:box-text db)))
(rf/reg-sub :result (fn [db _] (:result db)))
(rf/reg-sub :connected? (fn [db _] (:connected db)))
(rf/reg-sub :db (fn [db _] db))

;; components

(defn box []
  (fn []
    (let [box-text @(rf/subscribe [:box-text])]
      [:div [:p box-text]])))

(defn result []
  (fn []
    (let [result @(rf/subscribe [:result])]
      [:div [:p (pr-str result)]])))

(defn http-hit-button []
  [:button
   {:on-click #(rf/dispatch [:handler-with-http])}
   "Click Me"])

(defn sente-connect-button []
  [:button
   {:on-click #(rf/dispatch [:connect])
    :disabled @(rf/subscribe [:connected?])}
   "Sente send"])

(defn app []
  (let [db @(rf/subscribe [:db])]
    [:div
     [:p (pr-str @chsk-state)]
     [:p (pr-str db)]
     [:h1 "hello world"]
     [http-hit-button]
     [box]
     [result]
     [sente-connect-button]]))

(defn mount []
  (rd/render [app]
             (.getElementById js/document "app")))

(defn main []
  (rf/dispatch-sync [:initialize-db])
  (mount))

(defn reload []
  (mount))