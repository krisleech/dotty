(ns dotty.display
  (:require [org.httpkit.server :as server :refer [send! with-channel on-close on-receive]]
            [dotty.ws :as ws]
            [dotty.utils :refer [uuid]])
  (:gen-class))

(defn handle-it-changed-event [event]
  (let [it-player-id (:player-id event)]
    (ws/send-event-by-id it-player-id { "type" "you-are-it"})))

(defn handle-ping-event [event])

(defn process-display-new-message [channel event]
  (let [event-type (:type event)]
    (case event-type
      "it-changed" (handle-it-changed-event event)
      "ping" (handle-ping-event event)
      (println "No handler for event" event-type))
    {:status 302}))

;; random id for now
(defn display-connect! [channel]
  (let [display-id (uuid)]
    (ws/register-channel! display-id :display channel)
    (println "Display connected.")))

(defn display-disconnect! [channel status]
  (println "Display Disconnected." status))
