(ns dotty.player
  (:require [org.httpkit.server :as server :refer [send! with-channel on-close on-receive]]
            [clojure.data.json :as json]
            [dotty.ws :as ws]
            [dotty.utils :refer [uuid]])
  (:gen-class))

(defonce players (atom {}))

(defn new-player [] {:id (uuid) :x (rand-int 200) :y (rand-int 200)})

(defn player-connect! [channel request]
  (println "Player connected." (get (:headers request) "sec-websocket-key")))

(defn player-disconnect! [channel status]
  (println "Player Disconnected." status))

(defn handle-player-move-event [event]
  (ws/send-event! :display event))

(defn handle-new-player [channel event]
  (let [new-player (new-player)
        player-id (:id new-player)]
    (swap! players assoc player-id new-player)
    (ws/register-channel! player-id :player channel)
    (ws/send-event! player-id { :type "id-created" :id player-id})
    (ws/send-event! :display { :type "new-player" :player new-player})))

(defn handle-returning-player [channel event]
  (let [player-id (:player-id event)]
    (ws/register-channel! player-id :player channel)))

(defn process-player-new-message [channel message]
  (let [event (json/read-str message :key-fn keyword)
        event-type (:type event)]
    (do
      (println "<- Player" event)
      (case event-type
        "move" (handle-player-move-event event)
        "new-player" (handle-new-player channel event)
        "returning-player" (handle-returning-player channel event)
        (println "No handler for event" event-type))

      {:status 302})))

;; entry-point, handles all message from players
(defn player-ws-handler [request]
  (with-channel request channel
    (player-connect! channel request)
    (on-close channel (partial player-disconnect! channel))
    (on-receive channel (partial process-player-new-message channel))))

