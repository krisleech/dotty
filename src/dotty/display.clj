(ns dotty.display
  (:require [org.httpkit.server :as server :refer [send! with-channel on-close on-receive]]
            [clojure.data.json :as json])
  (:gen-class))

;; FIXME: should not need to deal with JSON at this level, do this earlier in core.
(defn process-display-new-message [raw_msg]
  (let [player (json/read-str raw_msg :key-fn keyword)]
    (println "Got new message" raw_msg)
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

(defn send-display-event! [event]
  (do
    (println "-> display" event)
    (send! @display-channel (json/write-str event))))


