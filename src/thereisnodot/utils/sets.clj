;; @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
;; @ Copyright (c) Michael Leachim                                                      @
;; @ You can find additional information regarding licensing of this work in LICENSE.md @
;; @ You must not remove this notice, or any other, from this software.                 @
;; @ All rights reserved.                                                               @
;; @@@@@@ At 2018-13-10 18:11 <mklimoff222@gmail.com> @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
(ns utils.sets
  (:require [clojure.set :as set]))

(defn jaccard
  "Calculate Jaccard distance between two strings split by space"
  [some-str some-another-str]
  (let [a (into #{} (clojure.string/split some-str #"\s+"))
        b (into #{} (clojure.string/split some-another-str #"\s+"))]
    (/
     (count (set/intersection a b))
     (count (set/union a b)))))

