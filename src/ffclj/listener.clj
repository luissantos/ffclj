(ns ffclj.listener
  (:require [clojure.java.io :as io]
            [clojure.string :refer [split trim]]
            [clojure.core.async :as async])
  (:import  [java.net ServerSocket])
  (:gen-class))


(defn- progress->map
  [progress]
  (->> progress
       (map #(let [[k v] (split % #"=")] [(keyword k) (trim v)]))
       (into {})))


(defn listen
  [channel]
  (let [socket (ServerSocket. 0)]
    (async/thread
      (with-open [sock (.accept socket)]
        (doseq [msg-in (partition 12  (line-seq (io/reader sock)))]
          (async/>!! channel (progress->map msg-in)))))
    socket))

(defn append-progress-listener
  [socker-server coll]
  (if (instance? ServerSocket socker-server)
    (concat ["-progress" (str "tcp://127.0.0.1:" (.getLocalPort socker-server))] coll)
    coll))
