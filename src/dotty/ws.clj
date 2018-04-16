(ns dotty.ws
  (:require [org.httpkit.server :refer [send!]]
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
  "returns channel tuple for given id"
  (first (filter #(= id (first %)) @channels)))

(defn find-by-tag [tag]
  "returns collection of channel tuples for given tag"
  (filter #(= tag (second %)) @channels))

(defn send-to! [channel event]
  (do
    (println "-->" (second channel) (first channel) event)
    (send! (last channel) (json/write-str event))))

(defn send-event-by-id [id event]
  (let [channel (find-by-id id)]
    (send-to! channel event)))

(defn send-event-by-tag [tag event]
  (doseq [channel (find-by-tag tag)]
    (send-to! channel event)))

(defn send-event! [id-or-tag event]
  (condp = (class id-or-tag)
    java.lang.String (send-event-by-id id-or-tag event)
    clojure.lang.Keyword (send-event-by-tag id-or-tag event)))
