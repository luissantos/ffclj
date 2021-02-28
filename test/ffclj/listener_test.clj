(ns ffmpeg.listener-test
  (:require [clojure.test :refer :all]
            [clojure.core.async :as async]
            [ffclj.listener :refer :all])
  (:import [java.net Socket]
           [java.io DataOutputStream]))

(deftest ffmpeg-listener
  (testing "FFMPEG progress gets converted to a map"

    (let [c (async/chan)]
      (with-open [ss (listen c)
                  socket (Socket. "127.0.0.1" (.getLocalPort ss))
                  writer (DataOutputStream. (.getOutputStream socket))]
        
        (doseq [line ["frame=1177\n"
                      "fps=50.98\n"
                      "stream_0_0_q=28.0\n"
                      "bitrate=1578.5kbits/s\n"
                      "total_size=9699376\n"
                      "out_time_us=49156644\n"
                      "out_time_ms=49156644\n"
                      "out_time=00:00:49.156644\n"
                      "dup_frames=0\n"
                      "drop_frames=0\n"
                      "speed=2.13x\n"
                      "progress=continue\n"]]

          (.writeBytes writer line)))
      
      (is (= (async/<!! c)
             {:dup_frames "0", 
              :frame "1177", 
              :speed "2.13x", 
              :out_time_ms "49156644", 
              :drop_frames "0", 
              :total_size "9699376", 
              :fps "50.98", 
              :stream_0_0_q "28.0", 
              :bitrate "1578.5kbits/s", 
              :progress "continue", 
              :out_time "00:00:49.156644", 
              :out_time_us "49156644"})))))
