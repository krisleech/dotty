(ns dotty.utils
  ;; (:require)
  (:gen-class))

(defn uuid [] (str (java.util.UUID/randomUUID)))
