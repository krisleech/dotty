(ns dotty.display
  (:require [org.httpkit.server :as server :refer [send! with-channel on-close on-receive]]
            [dotty.ws :as ws]
            [dotty.utils :refer [uuid]])
  (:gen-class))

(defn process-display-new-message [channel event]
    (println "Got new message" event)
    {:status 302})

;; random id for now
(defn display-connect! [channel]
  (let [display-id (uuid)]
    (ws/register-channel! display-id :display channel)
    (println "Display connected.")))

(defn display-disconnect! [channel status]
  (println "Display Disconnected." status))
