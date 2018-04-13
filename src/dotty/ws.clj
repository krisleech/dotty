(ns dotty.ws
  (:require [org.httpkit.server :refer [send!] :rename {send! send-message!}]
            [clojure.data.json :as json])
  (:gen-class))

(defonce channels (atom []))

(defn register-channel! [id tag channel]
  "registers channel indexed by id and tag, if the id already exists it is replaced"
  { :pre [(string? id) (keyword? tag)]}
  (let [without-id (remove #(= id (first %)) @channels)
        with-id (conj without-id [id tag channel])]
    (reset! channels with-id)))

(defn find-by-id [id]
  (first (filter #(= id (first %)) @channels)))

(defn find-by-tag [tag]
  (filter #(= tag (second %)) @channels))

(defn send! [channel event]
  (send-message! channel (json/write-str event)))

(defn send-event-by-id [id event]
  (send! (find-by-id id) event))

(defn send-event-by-tag [tag event]
  (doseq [channel (find-by-tag tag)]
    (send! channel event)))

(defn send-event! [id-or-tag event]
  (case (class id-or-tag)
    java.lang.String (send-event-by-id id-or-tag event)
    clojure.lang.Keyword (send-event-by-tag id-or-tag event)))
