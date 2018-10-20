;; @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
;; @ Copyright (c) Michael Leahcim                                                      @
;; @ You can find additional information regarding licensing of this work in LICENSE.md @
;; @ You must not remove this notice, or any other, from this software.                 @
;; @ All rights reserved.                                                               @
;; @@@@@@ At 2018-10-20 19:05 <thereisnodotcollective@gmail.com> @@@@@@@@@@@@@@@@@@@@@@@@
(ns
        ^{:author "Michael Leahcim"
          :doc    "Calculating time needed for reading things"}
    thereisnodot.utils.reading-time
  (:require [thereisnodot.utils.strings :as string-utils]
            [thereisnodot.akronim.core :refer [defns]]))
(defn- amount-of-words
  [text]
  (->>
   (clojure.string/split  text #"\s" )
   (filter (comp not empty?))
   (count)))

(defns dress-minute
  "Turns digit into human readable minute"
  [(dress-minute 12) => "less than twelve minutes"
   (dress-minute 1) => "less than one minute"]
  [minute-int]
  (str
   "less than "
   (string-utils/number->english minute-int) " "
   (string-utils/pluralize->as-s "minute" minute-int)))
   
(defns dress-minute-small
  "Same as dress-minute, but shorter version"
  [(dress-minute-small 1) => "1 minute"
   (dress-minute-small 12) => "12 minutes"]
  [minute-int]
  (str
   minute-int  " "
   (string-utils/pluralize->as-s "minute" minute-int)))

(defns calculate
  "(1/(words per minute)) * (amount of words) = (amount of minutes)"
  [(calculate "Hello world") => 1
   (calculate (apply str (repeat 1000 " huge text "))) => 10
   (calculate (apply str (repeat 256 " middle text "))) => 3
   (calculate (apply str (repeat 256 " middle text ")) 1) => 512]
  ([text]
   (calculate text 200))
  ([text average-words-per-minute]
   (int (Math/ceil (*  (/ 1 average-words-per-minute) (amount-of-words text))))))

(defns calculate-multiformat
  "Will return result in three different formats: numeric/short/long"
  [(calculate-multiformat (apply str (repeat 256 " middle text "))) =>
   [3 "3 minutes" "less than three minutes"]]
  ([text wpm]
   (calculate-multiformat text 200))
  ([text]
   (let [c (calculate text)]
     [c (dress-minute-small c) (dress-minute c)])))
