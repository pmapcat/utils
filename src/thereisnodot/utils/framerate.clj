;; @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
;; @ Copyright (c) Michael Leahcim                                                      @
;; @ You can find additional information regarding licensing of this work in LICENSE.md @
;; @ You must not remove this notice, or any other, from this software.                 @
;; @ All rights reserved.                                                               @
;; @@@@@@ At 2018-10-21 19:00 <thereisnodotcollective@gmail.com> @@@@@@@@@@@@@@@@@@@@@@@@

(ns
    ^{:doc "Framerate tools for Clojure"
      :author "Michael Leahcim"}
    thereisnodot.utils.framerate
  (:require [thereisnodot.akronim.core :refer [defns]]))

(defns duration->string
  "Will format framerate into a string"
  [(duration->string {:hours 1 :minutes 12 :seconds 13 :frames 11})
   =>  "01h 12m 13s 11fr"
   (duration->string {:hours 0 :minutes 0 :seconds 0 :frames 0})
   => "00:00"
   (duration->string {:hours 0 :minutes 0 :seconds 250 :frames 0})
   => "250s"]
  [item]
  (let [zero-formatted
        #(if (and (> % 0) (< % 9)) (str "0" %) %)]
    (when-let
        [result
         (str
          (if (> (:hours item) 0)
            (str (zero-formatted (:hours item)) "h "))
          (if (> (:minutes item) 0)
            (str (zero-formatted (:minutes item)) "m "))
          (if (> (:seconds item) 0)
            (str (zero-formatted (:seconds item)) "s "))
          (if (> (:frames item) 0)
            (str (zero-formatted (:frames item)) "fr ")))]
      (clojure.string/trim (if (empty? result) "00:00" result)))))

(defns number->duration
  "Will take the amount of frames and frame rate.
   Will output the resulting duration in the format 
   of:
   `:hours`, `:minutes`,  `:seconds`, and `:frames`"
  [(number->duration 1234 25)
   => {:hours 0.0, :minutes 0.0, :seconds 49.0, :frames 9.0}
   (number->duration 0 25)
   => {:hours 0.0, :minutes 0.0, :seconds 0.0, :frames 0.0}
   (number->duration 25 25)
   => {:hours 0.0, :minutes 0.0, :seconds 1.0, :frames 0.0}
   (number->duration (* 60 25) 25)
   => {:hours 0.0, :minutes 1.0, :seconds 0.0, :frames 0.0}
   (number->duration (* 60 60 25) 25)
   => {:hours 1.0, :minutes 0.0, :seconds 0.0, :frames 0.0}]
  ([frames]
   (number->duration frames 25))
  ([frames frame-rate]
   (let [secs
         (/ frames frame-rate)
         hours
         (Math/floor (/ secs 3600))
         minutes
         (Math/floor (/ (- secs (* hours 3600)) 60))
         seconds
         (Math/floor (- secs (* hours 3600) (* minutes 60)))
         rest-frames
         (Math/floor
          (- frames
             (* hours  (* frame-rate 3600))
             (* minutes (* frame-rate) 60)
             (* seconds frame-rate)))]
     {:hours   hours
      :minutes minutes
      :seconds seconds
      :frames  rest-frames})))
