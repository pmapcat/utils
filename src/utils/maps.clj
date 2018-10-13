;; @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
;; @ Copyright (c) Michael Leachim                                                      @
;; @ You can find additional information regarding licensing of this work in LICENSE.md @
;; @ You must not remove this notice, or any other, from this software.                 @
;; @ All rights reserved.                                                               @
;; @@@@@@ At 2018-13-10 18:56 <mklimoff222@gmail.com> @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@

(ns utils.maps)

(defn map-vec
  ;; (map-on-vector
  ;;  ["a" "b" "n"]
  ;;  {"n" 12 "q" 10}
  ;;  "n/a")
  [some-vector some-map or]
  (for [item some-vector]
    (get some-map item or)))

(defn map-val
  [f m]
  (zipmap (keys m) (map f (vals m))))

(defn pad
  "Zero Pad numbers - takes a number and the length to pad to as arguments
   From: https://gist.github.com/mihi-tr/28b4d176dba7d33057e6"
   [n c pad-symbol] 
   (loop [s (str n)]  
     (if (< (count s) c) 
         (recur (str pad-symbol s)) 
         s)))

(defn pad-coll
  "Pad collection <coll> with <pad-with> until <n> is reached
   e.g. (pad-coll 10 [3 4] nil) => [3 4 nil nil nil nil nil nil nil nil]"
   [n coll pad-with] 
   (loop [s coll]  
     (if (< (count s) n) (recur (conj s pad-with))  s)))


(defn nested-group-by
  "From: https://stackoverflow.com/a/38842018/3362518
   Like group-by but instead of a single function, this is given a list or vec
   of functions to apply recursively via group-by. An optional `final` argument
   (defaults to identity) may be given to run on the vector result of the final
   group-by.
   !!careful, deep nesting is not supported
   usage example
   (def foo [[\"A\" 2011 \"Dan\"]
             [\"A\" 2011 \"Jon\"]
             [\"A\" 2010 \"Tim\"]
             [\"B\" 2009 \"Tom\"] ])
   (nested-group-by [first second] foo)"
  [fs coll & [final-fn]]
  (if (empty? fs)
    ((or final-fn identity) coll)
    (map-val (group-by (first fs) coll)
             #(nested-group-by (rest fs) % final-fn))))



