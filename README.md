# FFCLJ

FFCLJ is a simple ffmpeg clojure wrapper. It aims to provide a simple wrapper around ffmpeg.

## Features 

### Supported features

* ffmpeg and ffprobe support
* ffmpeg progress via core async channels

### Planned features 

* babashka support (not tested)
* Filter graph syntax abstraction 
* ffmpeg installation (via https://ffbinaries.com/  )
* clojurescript support


## Usage


### FFMPEG 

```clojure

(with-open [task (ffmpeg! [:y 
                          :i "http://ftp.nluug.nl/pub/graphics/blender/demo/movies/ToS/ToS-4k-1920.mov"
                          :ss "00:00:00.000"
                          :t "5"
                          [:s "1280x720" :acodec "aac" :vcodec "h264" "720p.mp4"]])]

    (.wait-for task)
    (println "Transcoding completed. Exit code: " (.exit-code task)))
    
```

### FFMPEG + Progress Listener

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

### FFPROBE

```clojure

(let [result (ffprobe! [:show_format :show_streams 
                       "http://ftp.nluug.nl/pub/graphics/blender/demo/movies/ToS/ToS-4k-1920.mov"])
        s (group-by (comp keyword :codec_type) (:streams result))]
        [(:codec_name (first (:video s) )) (:codec_name (first (:audio s)) )])
        
; ["h264" "aac"]

```

## License

Copyright © 2021 Luis Santos

Distributed under the MIT License 
