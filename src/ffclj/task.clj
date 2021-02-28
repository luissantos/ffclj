(ns ffclj.task
  (:import [java.util.concurrent TimeUnit]))

(defprotocol Task
  (wait-for  [this] [this timeout])
  (exit-code [this])
  (done? [this])
  (close [this])
  (success? [this])
  (stdout [this])
  (stderr [this])
  (stdin  [this]))


(defrecord FFmpegTask [ss ex ^Process p]
  Task
  (wait-for  [this]
    (when p (.waitFor p))
    nil)
  (wait-for  [this timeout] (if p
                              (.waitFor p timeout TimeUnit/MILLISECONDS)
                              true))
  (exit-code [this] (when p (try
                              (.exitValue p)
                              (catch Exception _ nil))))
  (done? [this] (if p
                  (not (.isAlive p))
                  false))
  (success? [this] (if ex
                     false
                     (if-let [ec (exit-code this)]
                       (= ec 0)
                       false)))
  (close [this] (do
                  (when ss (.close ss))
                  (when p (.destroy p))))
  (stdin [this] (.getOutputStream p))
  (stderr [this] (.getErrorStream p))
  (stdout [this] (.getInputStream p)))
