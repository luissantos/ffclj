# FFCLJ

FFCLJ is a simple ffmpeg clojure wrapper. It aims to provide a simple wrapper around ffmpeg.

## Features 

### Supported features

* ffmpeg and ffprobe support
* ffmpeg progress via core async channels
* babashka support

### Planned features 

* Filter graph syntax abstraction 
* ffmpeg installation (via https://ffbinaries.com/  )
* clojurescript support

## Usage


### ffmpeg

```clojure

(with-open [task (ffmpeg! [:y 
                          :i "http://ftp.nluug.nl/pub/graphics/blender/demo/movies/ToS/ToS-4k-1920.mov"
                          :ss "00:00:00.000"
                          :t "5"
                          [:s "1280x720" :acodec "aac" :vcodec "h264" "720p.mp4"]])]

    (.wait-for task)
    (println "Transcoding completed. Exit code: " (.exit-code task)))
    
```

### ffmpeg + Progress Listener

```clojure

(let [c (async/chan)]

    (async/go (while true
                (let [[v ch] (async/alts! [c])]
                  (clojure.pprint/pprint v))))

    (with-open [task (ffmpeg! c [:y
                                :i "http://ftp.nluug.nl/pub/graphics/blender/demo/movies/ToS/ToS-4k-1920.mov"
                                :ss "00:00:00.000"
                                :t "5"
                                [:s "1280x720" :acodec "aac" :vcodec "h264" "720p.mp4"]])]

      (.wait-for task)
      (println "Transcoding completed. Exit code: " (.exit-code task))))
```

### ffprobe

```clojure

(let [result (ffprobe! [:show_format :show_streams 
                       "http://ftp.nluug.nl/pub/graphics/blender/demo/movies/ToS/ToS-4k-1920.mov"])
        s (group-by (comp keyword :codec_type) (:streams result))]
        [(:codec_name (first (:video s) )) (:codec_name (first (:audio s)) )])
        
; ["h264" "aac"]

```

### Using ffclj with babashka 

``` clojure
#!/usr/bin/env bb

(require '[babashka.deps :as deps])

(deps/add-deps '{:deps {ffclj/ffclj {:mvn/version "0.1.2" }}})

(require '[ffclj.core :refer [ffmpeg! ffprobe!]]
         '[ffclj.task :as task])

(let [result (ffprobe! [:show_format :show_streams
                       "http://ftp.nluug.nl/pub/graphics/blender/demo/movies/ToS/ToS-4k-1920.mov"])
        s (group-by (comp keyword :codec_type) (:streams result))
        codecs [(:codec_name (first (:video s) )) (:codec_name (first (:audio s)) )]]

  (println codecs))


(with-open [t (ffmpeg! [:y
                        :i "http://ftp.nluug.nl/pub/graphics/blender/demo/movies/ToS/ToS-4k-1920.mov"
                        :ss "00:00:00.000"
                        :t "5"
                        [:s "1280x720" :acodec "aac" :vcodec "h264" "720p.mp4"]])]

      (task/wait-for t)
      (println "Transcoding completed. Exit code: " (task/exit-code t)))
```

## License

Copyright © 2021 Luis Santos

Distributed under the MIT License 
