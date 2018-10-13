;; @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
;; @ Copyright (c) Michael Leachim                                                      @
;; @ You can find additional information regarding licensing of this work in LICENSE.md @
;; @ You must not remove this notice, or any other, from this software.                 @
;; @ All rights reserved.                                                               @
;; @@@@@@ At 2018-13-10 20:47 <mklimoff222@gmail.com> @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@

(ns utils.filters)

(defn filter-map-on-val
  "Sample: (filter-map-on-val #(= % 1) {:1 1 :2 2 :3 3})"
  [filter-fn some-map]
  (into
   {}
   (filter
    (fn [[_ val] & item]
      (filter-fn val))
    some-map)))
(defn filter-map-on-key
  [filter-fn some-map]
  (into
   {}
   (filter
    (fn [[key _] & item]
      (filter-fn key))
    some-map)))
