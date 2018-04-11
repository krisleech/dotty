(ns dotty.core
  (:require [org.httpkit.server :as server :refer [send! with-channel on-close on-receive]]
            [compojure.core :refer [routes wrap-routes GET defroutes]]
            [clojure.data.json :as json]
            [ring.middleware.defaults :refer [site-defaults wrap-defaults]]
            [ring.middleware.resource :refer [wrap-resource]]
            [ring.middleware.reload :as reload]
            [ring.util.response :refer [resource-response]]
            [clojure.java.io :as io]
            [dotty.display :refer :all]
            [dotty.player :refer :all])
  (:gen-class))

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

(defroutes websocket-routes
  (GET "/ws/display" request (display-ws-handler request))
  (GET "/ws/player" request (player-ws-handler request)))

(def all-routes (routes websocket-routes http-routes))

(defonce in-dev? true) ;; FIXME, read env var

(def app
  (-> all-routes
      (wrap-defaults (assoc-in site-defaults [:security :anti-forgery] false))
      (wrap-resource "public")))

(defn -main
  [& args]
  (let [handler (if in-dev? (reload/wrap-reload #'app) app)]
    (do
      (println "Starting Server on port 3000")
      (when in-dev? (println "Development Environment"))
      (server/run-server handler {:port 3000}))))
