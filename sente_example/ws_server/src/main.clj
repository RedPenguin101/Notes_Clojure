(ns main
  (:require [org.httpkit.server :refer [run-server]]
            [clojure.pprint :refer [pprint]]
            [compojure.core :as comp :refer [defroutes GET POST]]
            [clojure.data.json :as json]
            [clojure.core.async :refer [go-loop <!] :as async]
            [taoensso.sente :as sente]
            [ring.middleware.keyword-params]
            [ring.middleware.params]
            [ring.middleware.session]
            [ring.middleware.cors :refer [wrap-cors]]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [taoensso.sente.server-adapters.http-kit :refer (get-sch-adapter)]))

(comment
  "Terms:
   Websocket Server
   Websocket Router
   Channel Socket
   Receive channel
   Send function
   Event handler
   Broadcast")

;; socket

(comment
  "First we make a channel socket server. The relevant adapter for our webserver (http-kit) is passed in.
   Returned from this 'make' function is a map with the following:
   * ch-recv: a channel which will receive messages from clients
   * send-fn: a function which we'll call to send messages to our clients
   * connected-uids: an Atom which keeps track of who is connected to us via websocket. 
     is has keys 'ws', 'ajax' and 'any'. Can sort of guess what they do but not precisely
   * ajax-get-or-ws-handshake-fn: this is the handler for GETs (incl. the initial handshake)
     that we'll use in our router
   * ajax-post-fn: the handler that gets used for POSTs to the socket route.
   * send buffers: ???
   
   We'll define each of these and make use of them later."

  (sente/make-channel-socket-server! (get-sch-adapter) {:packer :edn :csrf-token-fn nil})
  ;; => {:ch-recv #object[clojure.core.async.impl.channels.ManyToManyChannel 0x37569326 "clojure.core.async.impl.channels.ManyToManyChannel@37569326"],
  ;;     :send-fn #function[taoensso.sente/make-channel-socket-server!/send-fn--20535],
  ;;     :connected-uids #<Atom@6b240309: {:ws #{}, :ajax #{}, :any #{}}>,
  ;;     :send-buffers #<Atom@6ebd7fe8: {:ws {}, :ajax {}}>,
  ;;     :ajax-post-fn #function[taoensso.sente/make-channel-socket-server!/fn--21056],
  ;;     :ajax-get-or-ws-handshake-fn #function[taoensso.sente/make-channel-socket-server!/fn--21376]}
  )


(let [chsk-server (sente/make-channel-socket-server! (get-sch-adapter) {:packer :edn :csrf-token-fn nil})]
  (def ws-post-handler    (:ajax-post-fn chsk-server))
  (def ws-get-handler     (:ajax-get-or-ws-handshake-fn chsk-server))
  (def ws-receive-channel (:ch-recv chsk-server))
  (def ws-send!           (:send-fn chsk-server))
  (def connected-uids     (:connected-uids chsk-server)))

(comment

  @connected-uids
  "Now we have all the various handlers and fns that make up our server, we'll set up the other main
   part of the furniture: the web-socket ROUTER.
   
   The router will live in an atom, and will be started (together with the server) with sente's
   'start-server-chsk-router!' function, which takes your receive channel, and a handler which determines
   how to deal with messages received on the receive channel.
   
   Stopping the router is a matter of derefing the atom to get the 'stop' function, and calling it.")

(defn ws-event-handler [msg]
  #_(println "Message received")
  #_(pprint msg))

(defonce ws-router (atom nil))

(defn stop-ws-router! []
  (when-let [stop-fn @ws-router] (stop-fn)))

(defn start-ws-router! []
  (stop-ws-router!)
  (reset! ws-router (sente/start-server-chsk-router! ws-receive-channel ws-event-handler)))

;; Broadcasts

(comment
  "Next we set up a simple async loop in the background which will 'broadcast' to all connected UIDs.
   We'll use 'connected uids' to get everyone who is connected to us.
   We use our send-fn to send the message. 
   The send-fn takes a uid and a message, and sends the message to the client with that UID")

(defn broadcast [i uids send-fn]
  (doseq [uid uids]
    #_(println (str "Broadcasting to user:" uid))
    (send-fn uid
             [:some/broadcast
              {:what-is-this "An async broadcast pushed from server"
               :how-often    "Every 10 seconds"
               :to-whom      uid
               :i            i}])))

(defn start-example-broadcaster! []
  (go-loop [i 0]
    (<! (async/timeout 10000))
    (broadcast i (:any @connected-uids) ws-send!)
    (recur (inc i))))

;; routes

(comment
  "Setting up the routes is as simple as using the 2 handlers we got from the 'make' function above")

(defroutes routes
  (GET "/" [] {:status 200 :body (json/write-str {"Hello" "World"}) :headers {"Content-Type" "application/json"}})
  (GET  "/chsk" req (do (pprint req) (ws-get-handler req)))
  (POST "/chsk" req (do (pprint req) (ws-post-handler req))))

;; app

(defonce server (atom nil))

(def app
  (-> #'routes
      ring.middleware.keyword-params/wrap-keyword-params
      ring.middleware.params/wrap-params
      ring.middleware.session/wrap-session
      (wrap-defaults site-defaults)
      (wrap-cors :access-control-allow-origin [#".*"])))

(defn start-server []
  (reset! server (run-server (fn [req] (app req)) {:port 3000 :join? false})))

(defn stop-server []
  (when-not (nil? @server)
    (@server :timout 100)
    (reset! server nil)))

(comment
  (start-ws-router!)
  (start-server)
  (start-example-broadcaster!)

  (stop-ws-router!)
  (stop-server))