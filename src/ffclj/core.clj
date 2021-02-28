(ns ffclj.core
  (:require [ffclj.exec :refer [ffexec!]]
            [clojure.core.async :as async]
            [cheshire.core :as json]))

(defn- parse-stream
  ""
  [is]
  (json/parse-string (slurp is) true))

(defn ffmpeg!
  ([args] (ffmpeg! "ffmpeg" nil args))
  ([channel args] (ffmpeg! "ffmpeg" channel args))
  ([binary channel args]
   (ffexec! binary channel args)))

(defn ffprobe!
  ""
  ([args] (ffprobe! "ffprobe" args))
  ([binary args]
   (with-open [task (->> (concat [:print_format "json"] args)
                         (ffexec! binary nil))]
     (parse-stream (.stdout task)))))

