;; @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
;; @ Copyright (c) Michael Leachim                                                      @
;; @ You can find additional information regarding licensing of this work in LICENSE.md @
;; @ You must not remove this notice, or any other, from this software.                 @
;; @ All rights reserved.                                                               @
;; @@@@@@ At 2018-16-10 21:48 <mklimoff222@gmail.com> @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@

(ns thereisnodot.utils.framerate)

(defn format-frames
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
      (if (empty? result) "00:00" result))))

(defn number->frame-rate
  "Will take the amount of frames and frame rate.
   Will output the amount of 
   :hours :minutes  :seconds and :frames
   Will also output the pretty printed 
   view in the :string"
  [frames frame-rate]
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
    (let [result
          {:hours   hours
           :minutes minutes
           :seconds seconds
           :frames  rest-frames}]
      (assoc result :string (format-frames result)))))
