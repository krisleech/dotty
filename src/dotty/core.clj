(ns dotty.core
  (:require [org.httpkit.server :as server :refer [send! with-channel on-close on-receive]]
            [compojure.core :refer [routes wrap-routes GET defroutes]]
            [clojure.data.json :as json]
            [ring.middleware.defaults :refer [site-defaults wrap-defaults]]
            [ring.middleware.resource :refer [wrap-resource]]
            [ring.util.response :refer [resource-response]]
            [clojure.java.io :as io])
  (:gen-class))

(defonce players (atom {}))

(defn uuid [] (str (java.util.UUID/randomUUID)))
(defn render [body] { :status 200 :body body :headers {"Content-Type" "text/html"}})
(defn render-json [data] { :status 200 :body (json/write-str data) :headers {"Content-Type" "text/json" "Access-Control-Allow-Origin" "*"}})
(defn render-html-file [path] { :status 200 :body (io/input-stream (io/resource (str "public/" path))) :headers {"Content-Type" "text/html"}})

(defn homepage-handler [r] (render-html-file "index.html"))
(defn debug-handler [r] (render-json {:players @players}))
(defn game-handler [r] (render "GAME"))
(defn join-handler [r] (render-html-file "join.html"))
(defn display-handler [r] (render-html-file "display.html"))

(defroutes http-routes
  (GET "/" [] homepage-handler)
  (GET "/debug" [] debug-handler)
  (GET "/game" [] game-handler)
  (GET "/join" [] join-handler)
  (GET "/display" [] display-handler))

(defn process-display-new-message [raw_msg]
  (let [player (json/read-str raw_msg :fn-key keyword)]
    (println "Get new message" raw_msg)
    {:status 302}))

(defonce display-channel (atom nil))

(defn display-connect! [channel]
  (do
    (reset! display-channel channel)
    (println "Display connected.")))

(defn display-disconnect! [channel status]
  (do
    (reset! display-channel nil)
    (println "Display Disconnected." status)))

(defn display-connected? [] (not (nil? @display-channel)))

(defn display-ws-handler [request]
  (with-channel request channel
    (display-connect! channel)
    (on-close channel (partial display-disconnect! channel))
    (on-receive channel process-display-new-message)))


(defn new-player [] {:x 10 :y 10})

(defn player-connect! [channel]
  (let [player-uuid (uuid)]
    (do
      (swap! players assoc player-uuid (new-player))
      (send! channel (str "your uuid is" player-uuid))
      (println "Player connected."))))

(defn player-disconnect! [channel status]
  (do
    (println "Player Disconnected." status)))

(defn process-player-new-message [raw_msg]
  (println "Player message received" raw_msg)
  {:status 302})

(defn player-ws-handler [request]
  (with-channel request channel
    (player-connect! channel)
    (on-close channel (partial player-disconnect! channel))
    (on-receive channel process-player-new-message)))

(defroutes websocket-routes
  (GET "/ws/display" request (display-ws-handler request))
  (GET "/ws/player" request (player-ws-handler request)))

(def all-routes (routes websocket-routes http-routes))

(def app
  (-> all-routes
      (wrap-defaults (assoc-in site-defaults [:security :anti-forgery] false))
      (wrap-resource "public")))

(defn -main
  [& args]
  (do
    (println "Starting Server on port 3000")
    (server/run-server app {:port 3000})))
