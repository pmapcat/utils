(ns utils.strings
  (:require [clojure.string :as string]))

(defn lipsum
  [amount-of-words]
  (let [words (string/split
               "Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo. Nemo enim ipsam voluptatem quia voluptas sit aspernatur aut odit aut fugit, sed quia consequuntur magni dolores eos qui ratione voluptatem sequi nesciunt. Neque porro quisquam est, qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit, sed quia non numquam eius modi tempora incidunt ut labore et dolore magnam aliquam quaerat voluptatem. Ut enim ad minima veniam, quis nostrum exercitationem ullam corporis suscipit laboriosam, nisi ut aliquid ex ea commodi consequatur? Quis autem vel eum iure reprehenderit qui in ea voluptate velit esse quam nihil molestiae consequatur, vel illum qui dolorem eum fugiat quo voluptas nulla pariatur?" #" ")]
    (sort
     (for [_ (range amount-of-words)]
       (string/lower-case (rand-nth words))))))

(defn truncate-words
  "will intelligently truncate (without splitting words, with appending ... in the end, if exists)"
  [amount input]
  (string/trim
   (string/replace-first
    (apply str (take (- amount 3) (str  input " ")))
    #"[^\s]+$"
    "...")))

(defn time-format
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

(defn parse-frames
  "(parse-frames (+ (* 3600 25)
                 (* 29 60 25)
                 (* 17 25)
                 15) 25)"
  [frames frame-rate]
  (let [secs
        (/ frames frame-rate)
        hours
        (.floor Math/floor (/ secs 3600))
        minutes
        (.floor  (/ (- secs (* hours 3600)) 60))
        seconds
        (.floor js/Math (- secs (* hours 3600) (* minutes 60)))
        rest-frames
        (.floor
         js/Math
         (- frames
            (* hours  (* frame-rate 3600))
            (* minutes (* frame-rate) 60)
            (* seconds frame-rate)))]
    {:hours   hours
     :minutes minutes
     :seconds seconds
     :frames  rest-frames}))
