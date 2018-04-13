(ns dotty.display
  (:require [org.httpkit.server :as server :refer [send! with-channel on-close on-receive]]
            [clojure.data.json :as json]
            [dotty.ws :as ws]
            [dotty.utils :refer [uuid]])
  (:gen-class))

;; FIXME: should not need to deal with JSON at this level, do this earlier in core.
(defn process-display-new-message [raw_msg]
  (let [player (json/read-str raw_msg :key-fn keyword)]
    (println "Got new message" raw_msg)
    {:status 302}))

;; random id for now
(defn display-connect! [channel]
  (let [display-id (uuid)]
    (ws/register-channel! display-id :display channel)
    (println "Display connected.")))

(defn display-disconnect! [channel status]
  (println "Display Disconnected." status))

(defn display-ws-handler [request]
  (with-channel request channel
    (display-connect! channel)
    (on-close channel (partial display-disconnect! channel))
    (on-receive channel process-display-new-message)))
