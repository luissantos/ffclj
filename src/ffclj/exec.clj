(ns ffclj.exec
  (:require [ffclj.listener :refer [listen append-progress-listener]]
            [ffclj.task :refer [->FFmpegTask]]))

(defn convert-args
  [coll]
  (->> coll
       (seq)
       (flatten)
       (map #(cond
               (keyword? %) (str "-" (name %))
               (coll? %) (convert-args %)
               :else (str %) ))
       (flatten)))

(defn- exec
  [binary args]
  (.start (ProcessBuilder. (concat [binary] args))))


(defn ffexec!
  ([binary channel args]
   (let [ss (when-some [c channel] (listen c))]
     (try
       (->> args
            (convert-args)
            (append-progress-listener ss)
            (exec binary)
            (->FFmpegTask ss nil))
       (catch Exception ex (->FFmpegTask nil ex nil))))))
