(ns ffclj.exec-test
  (:require [ffclj.exec :as exec]
            [clojure.test :refer :all]))

(deftest ffmpeg-arguments-conversion
  (testing "Test that numbers are converted to strings"
    (is (= (exec/convert-args [:s 20 :framerate 30])
           ["-s" "20"  "-framerate" "30"])))

  (testing "Test that nested arguments are flattened"
    (is (= (exec/convert-args [:s 20 [:y [:framerate 30]]])
             ["-s" "20" "-y" "-framerate" "30"]))))
