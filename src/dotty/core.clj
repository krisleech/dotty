(ns dotty.core
  (:require [org.httpkit.server :as server :refer [send! with-channel on-close on-receive]]
            [compojure.core :refer [routes wrap-routes GET defroutes]]
            [clojure.data.json :as json]
            [ring.middleware.defaults :refer [site-defaults wrap-defaults]]
            [ring.middleware.resource :refer [wrap-resource]])
  (:gen-class))


(defn uuid [] (str (java.util.UUID/randomUUID)))
(defn render [body] { :status 200 :body body :headers {"Content-Type" "text/html"}})
(defn render-json [data] { :status 200 :body (json/write-str data) :headers {"Content-Type" "text/json" "Access-Control-Allow-Origin" "*"}})

(defn homepage-handler [r] (render "HOMEPAGE"))
(defn debug-handler [r] (render "DEBUG"))
(defn game-handler [r] (render "GAME"))
(defn join-handler [r] (render "JOIN"))
(defn display-handler [r] (render "DISPLAY"))

(defroutes http-routes
  (GET "/" [] homepage-handler)
  (GET "/debug" [] debug-handler)
  (GET "/game" [] game-handler)
  (GET "/join" [] join-handler)
  (GET "/display" [] display-handler))

(defn process-new-message [raw_msg]
  (let [player (json/read-str raw_msg :fn-key keyword)]
    (println "Get new message" raw_msg)
    {:status 302}))

(defn connect! [channel]
  (println "Connected"))

(defn disconnect! [channel]
  (println "Disconnected"))

(defn ws-handler [request]
  (with-channel request channel
    (connect! channel)
    (on-close channel (partial disconnect! channel))
    (on-receive channel process-new-message)))

(defroutes websocket-routes
  (GET "/websocket" request (ws-handler request)))

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
