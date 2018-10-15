;; @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
;; @ Copyright (c) Michael Leachim                                                      @
;; @ You can find additional information regarding licensing of this work in LICENSE.md @
;; @ You must not remove this notice, or any other, from this software.                 @
;; @ All rights reserved.                                                               @
;; @@@@@@ At 2018-14-10 20:50 <mklimoff222@gmail.com> @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@

(ns thereisnodot.utils.numbers
  (:require [clojure.pprint :as pprint]))

(defn number-ordinal->english
  [number-int]
  (pprint/cl-format nil "~:R" number-int))

(defn number->english
  [number-int]
  (pprint/cl-format nil "~R" number-int))

(defn number->roman
  [number-int]
  (clojure.pprint/cl-format nil "~@R" number-int))

(defn pluralize->as-s
  [root-str number-int]
  (clojure.pprint/cl-format nil (str root-str "~:P") number-int))

(defn pluralize->as-ies
  [root-str number-int]
  (clojure.pprint/cl-format nil (str root-str "~:@P") number-int))

(defn human-date
  [year month day]
  (str
   (clojure.string/capitalize (number-ordinal->english day))
   " of "
   (clojure.string/capitalize
    (condp = month
      1 "january"
      2 "february"
      3 "march"
      4 "april"
      5 "may"
      6 "june"
      7 "july"
      8 "august"
      9 "september"
      10 "october"
      11 "november"
      12 "december"))
   ", " year))
