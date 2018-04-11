(ns dotty.ws
  (:require [org.httpkit.server :as server :refer [send! with-channel on-close on-receive]]
            [clojure.data.json :as json])
  (:gen-class))

(def channels (atom []))

;; TODO: handle id already exists (replace channel)
(defn register-channel! [id tag channel]
  { :pre [(string? id) (keyword? tag)]}
  (swap! channels conj [id tag channel]))

(defn find-by-id [id]
  (first (filter #(= id (first %)) @channels)))

(defn find-by-tag [tag]
  (filter #(= tag (second %)) @channels))

(defmulti send-event! (fn [id-or-tag event] (class id-or-tag)))

(defmethod send-event! java.lang.String [id event]
  (println "send event by id"))

(defmethod send-event! clojure.lang.Keyword [tag event]
  (println "send event by tag"))

;; (defn send-event! [id-or-tag event]
;;   (case (class id-or-tag)
;;     java.lang.String (println "id")
;;     clojure.lang.Keyword (println "keyword")))
