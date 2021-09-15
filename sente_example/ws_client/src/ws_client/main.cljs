(ns ws-client.main
  (:require [re-frame.core :as rf]
            [reagent.dom :as rd]
            [taoensso.sente  :as sente]))

;; Sente Stuff
(def config {:type     :auto
             :packer   :edn
             :protocol :http
             :host     "localhost"
             :port     3000})

(comment
  (sente/make-channel-socket-client! "/chsk" nil config)
  ;;'{:chsk #taoensso.sente.ChAutoSocket{:ws-chsk-opts {:client-id "4971b63d-e770-4185-a269-27612a83264d"
  ;;                                                    :chs {:internal #object[cljs.core.async.impl.channels.ManyToManyChannel]
  ;;                                                          :state #object[cljs.core.async.impl.channels.ManyToManyChannel]
  ;;                                                          :<server #object[cljs.core.async.impl.channels.ManyToManyChannel]}
  ;;                                                    :params nil
  ;;                                                    :headers nil
  ;;                                                    :packer #object[taoensso.sente.EdnPacker]
  ;;                                                    :ws-kalive-ms 20000
  ;;                                                    :url "ws://localhost:3000/chsk"
  ;;                                                    :backoff-ms-fn #object[taoensso$encore$exp_backoff]}
  ;;                                     :ajax-chsk-opts {:ws-kalive-ms 20000
  ;;                                                      :client-id "4971b63d-e770-4185-a269-27612a83264d"
  ;;                                                      :packer #object[taoensso.sente.EdnPacker]
  ;;                                                      :chs {:internal #object[cljs.core.async.impl.channels.ManyToManyChannel]
  ;;                                                            :state #object[cljs.core.async.impl.channels.ManyToManyChannel]
  ;;                                                            :<server #object[cljs.core.async.impl.channels.ManyToManyChannel]}
  ;;                                                      :params nil
  ;;                                                      :headers nil
  ;;                                                      :backoff-ms-fn #object[taoensso$encore$exp_backoff]
  ;;                                                      :url "http://localhost:3000/chsk"
  ;;                                                      :ajax-opts nil}
  ;;                                     :state_ #object[cljs.core.Atom {:val {:type :auto, :open? false, :ever-opened? false, :csrf-token nil}}]
  ;;                                     :impl_ #object[cljs.core.Atom {:val #taoensso.sente.ChWebSocket{:client-id "4971b63d-e770-4185-a269-27612a83264d"
  ;;                                                                                                     :chs {:internal #object[cljs.core.async.impl.channels.ManyToManyChannel]
  ;;                                                                                                           :state #object[cljs.core.async.impl.channels.ManyToManyChannel]
  ;;                                                                                                           :<server #object[cljs.core.async.impl.channels.ManyToManyChannel]}
  ;;                                                                                                     :params nil
  ;;                                                                                                     :headers nil
  ;;                                                                                                     :packer #object[taoensso.sente.EdnPacker]
  ;;                                                                                                     :url "ws://localhost:3000/chsk"
  ;;                                                                                                     :ws-kalive-ms 20000
  ;;                                                                                                     :state_ #object[cljs.core.Atom {:val {:type :auto
  ;;                                                                                                                                           :open? false
  ;;                                                                                                                                           :ever-opened? false
  ;;                                                                                                                                           :csrf-token nil}}]
  ;;                                                                                                     :instance-handle_ #object[cljs.core.Atom {:val "1d3b2155-21b0-4f20-aa0e-8232fb5af659"}]
  ;;                                                                                                     :retry-count_ #object[cljs.core.Atom {:val 0}]
  ;;                                                                                                     :ever-opened?_ #object[cljs.core.Atom {:val false}]
  ;;                                                                                                     :backoff-ms-fn #object[taoensso$encore$exp_backoff]
  ;;                                                                                                     :cbs-waiting_ #object[cljs.core.Atom {:val {}}]
  ;;                                                                                                     :socket_ #object[cljs.core.Atom {:val #object[WebSocket [object WebSocket]]}]
  ;;                                                                                                     :udt-last-comms_ #object[cljs.core.Atom {:val nil}]}}]}
  ;;  :ch-recv #object[cljs.core.async.impl.channels.ManyToManyChannel]
  ;;  :send-fn #object[G__29465]
  ;;  :state #object[cljs.core.Atom {:val {:type :auto
  ;;                                       :open? false
  ;;                                       :ever-opened? false
  ;;                                       :csrf-token nil}}]}
  )


(defn log [message data]
  (.log js/console message (.stringify js/JSON (clj->js data))))

(defn state-watcher [_key _atom _old-state new-state]
  (.warn js/console "New state: " new-state))

(defn handler [{:keys [?data id]}]
  (case id
    :chsk/recv (do (rf/dispatch [:increase-counter]) (log "Push event from server:" ?data))
    :chsk/handshake (log (str "Handshake with id" id ":") ?data)
    :default (log (str "Other event received with id " id ":") ?data)))

(def ws-router (atom nil))
(def ws-receive-channel (atom nil))
(def ws-send-fn! (atom nil))

(defn create-client! []
  (let [{:keys [ch-recv send-fn state]} (sente/make-channel-socket-client! "/chsk" nil config)]
    (reset! ws-receive-channel ch-recv)
    (reset! ws-send-fn! send-fn)
    (add-watch state :state-watcher state-watcher)
    (add-watch state :state-watcher (fn [_ _ _ new-state] (rf/dispatch [:chsk-state new-state])))))

(defn stop-router! []
  (when-let [stop-fn @ws-router] (stop-fn)))

(defn start-router! []
  (stop-router!)
  (reset! ws-router (sente/start-client-chsk-router! @ws-receive-channel handler)))

(defn start! []
  (create-client!)
  (start-router!))

;; events

(rf/reg-event-db
 :initialize-db
 (fn [_ _]
   {:connected false
    :counter 0}))

(rf/reg-event-db
 :connect
 (fn [db []]
   (start!)
   (assoc db :connected true)))

(rf/reg-event-db
 :increase-counter
 (fn [db []] (update db :counter inc)))

(rf/reg-event-db
 :chsk-state
 (fn [db [_ data]] (assoc db :chsk-state data)))

(rf/reg-sub :connected? (fn [db _] (:connected db)))
(rf/reg-sub :counter (fn [db _] (:counter db)))
(rf/reg-sub :db (fn [db _] db))

;; components

(defn sente-connect-button []
  [:button
   {:on-click #(rf/dispatch [:connect])
    :disabled @(rf/subscribe [:connected?])}
   "Websocket Connect"])

(defn app []
  (let [db @(rf/subscribe [:db])]
    [:div
     [:p (pr-str db)]
     [:h1 "hello world"]
     [sente-connect-button]
     [:p @(rf/subscribe [:counter])]]))

(defn mount []
  (rd/render
   [app]
   (.getElementById js/document "app")))

(defn main []
  (rf/dispatch-sync [:initialize-db])
  (mount))

(defn reload []
  (mount))