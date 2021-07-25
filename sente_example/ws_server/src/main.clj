(ns main
  (:require [org.httpkit.server :refer [run-server]]
            [compojure.core :as comp :refer [defroutes GET POST]]
            [clojure.data.json :as json]
            [taoensso.sente :as sente]
            [ring.middleware.keyword-params]
            [ring.middleware.params]
            [ring.middleware.session]
            [ring.middleware.anti-forgery :refer [wrap-anti-forgery]]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [taoensso.sente.server-adapters.http-kit :refer (get-sch-adapter)]))

(defonce server (atom nil))

(let [{:keys [ch-recv send-fn connected-uids
              ajax-post-fn ajax-get-or-ws-handshake-fn]}
      (sente/make-channel-socket! (get-sch-adapter) {})]

  (def ring-ajax-post                ajax-post-fn)
  (def ring-ajax-get-or-ws-handshake ajax-get-or-ws-handshake-fn)
  (def ch-chsk                       ch-recv) ; ChannelSocket's receive channel
  (def chsk-send!                    send-fn) ; ChannelSocket's send API fn
  (def connected-uids                connected-uids) ; Watchable, read-only atom
  )

(defroutes routes
  (GET "/" [] {:status 200
               :body (json/write-str {"Hello" "World"})
               :headers {"Content-Type" "application/json"
                         "Access-Control-Allow-Origin" "http://localhost:9090"}})
  (GET  "/chsk" req (ring-ajax-get-or-ws-handshake req))
  (POST "/chsk" req (ring-ajax-post req)))

(def app (-> routes
             (wrap-defaults site-defaults)
             ring.middleware.keyword-params/wrap-keyword-params
             ring.middleware.params/wrap-params
             ring.middleware.session/wrap-session))

(defn start []
  (reset! server (run-server (fn [req] (app req)) {:port 3000 :join? false})))

(defn stop []
  (when-not (nil? @server)
    (@server :timout 100)
    (reset! server nil)))

(comment
  (start)
  (stop))