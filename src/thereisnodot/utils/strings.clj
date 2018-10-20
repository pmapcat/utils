;; @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
;; @ Copyright (c) Michael Leahcim                                                      @
;; @ You can find additional information regarding licensing of this work in LICENSE.md @
;; @ You must not remove this notice, or any other, from this software.                 @
;; @ All rights reserved.                                                               @
;; @@@@@@ At 2018-10-18 19:47 <thereisnodotcollective@gmail.com> @@@@@@@@@@@@@@@@@@@@@@@@

(ns ^{:doc "Useful set of string functions"
      :author "Michael Leahcim"}
    thereisnodot.utils.strings
  (:require [clojure.string :as string]
            [clojure.set :as clj-set]
            [thereisnodot.akronim.core :refer [defns]]
            [clojure.pprint :as pprint]))


(defns remove-new-lines
  "Will remove new lines from text"
  [(remove-new-lines "Hello
                      world") => "Hello                       world"]
  [datum]
  (clojure.string/replace datum #"\n+" " "))

(defns slugify
  "Will slugify given string. Will remove non ASCII characters"
  [(slugify "Will slugify given string.") => "will-slugify-given-string"
   (slugify "Это не работает") => ""
   (slugify "whatever whoever" "_") => "whatever_whoever"]
  ([some-text]
   (slugify some-text "-"))
  ([some-text split-kind]
   (->>
    some-text
    (clojure.string/lower-case)
    (re-seq  #"[A-Za-zА-Яа-я]+")
    (clojure.string/join split-kind))))


(defns lorem-ipsum
  "Generate lorem ipsum of words of a given size
   Accepts second parameter as a source of randomness. 
   Default rand-int"
  [(lorem-ipsum 5 (fn [_] 0)) => (list "sed" "sed" "sed" "sed" "sed")]
  ([amount-of-words]
   (lorem-ipsum amount-of-words rand-int))
  ([amount-of-words rand-fn]
   (let [words (string/split
                "Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo. Nemo enim ipsam voluptatem quia voluptas sit aspernatur aut odit aut fugit, sed quia consequuntur magni dolores eos qui ratione voluptatem sequi nesciunt. Neque porro quisquam est, qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit, sed quia non numquam eius modi tempora incidunt ut labore et dolore magnam aliquam quaerat voluptatem. Ut enim ad minima veniam, quis nostrum exercitationem ullam corporis suscipit laboriosam, nisi ut aliquid ex ea commodi consequatur? Quis autem vel eum iure reprehenderit qui in ea voluptate velit esse quam nihil molestiae consequatur, vel illum qui dolorem eum fugiat quo voluptas nulla pariatur?" #" ")
         amount (count words)]
     (sort
      (for [_ (range amount-of-words)]
        (string/lower-case (nth words (rand-fn amount))))))))

(defns truncate-words-by-chars
  "will intelligently truncate (without splitting words, with appending ... in the end, if exists)"
  [(truncate-words-by-chars 40  "This is a beautiful sunny day") => "This is a beautiful sunny day"
   (truncate-words-by-chars 30  "This is a beautiful sunny day") => "This is a beautiful sunny ..."
   (truncate-words-by-chars 20  "This is a beautiful sunny day") => "This is a ..."
   (truncate-words-by-chars 10  "This is a beautiful sunny day") => "This ..."]
  ([amount input]
   (truncate-words-by-chars amount input "..."))
  ([amount input ending]
   (string/trim
    (string/replace-first
     (apply str (take (- amount (count ending)) (str  input " ")))
     #"[^\s]+$" ending))))

(defns number-ordinal->english
  "Number ordinal to English"
  [(number-ordinal->english 3) => "third"]
  [number-int]
  (pprint/cl-format nil "~:R" number-int))

(defns number->english
  "Number to English"
  [(number->english 3) => "three"]
  [number-int]
  (pprint/cl-format nil "~R" number-int))

(defns number->roman
  "Number to Roman"
  [(number->roman 3) => "III"]
  [number-int]
  (clojure.pprint/cl-format nil "~@R" number-int))

(defns pluralize->as-s
  "Pluralize English with -s suffix"
  [(pluralize->as-s "friend" 1) => "friend"
   (pluralize->as-s "friend" 2) => "friends"]
  [root-str number-int]
  (clojure.pprint/cl-format nil (str root-str "~:P") number-int))

(defns pluralize->as-ies
  "Pluralize English with -ies suffix"
  [(pluralize->as-ies "strawberr" 1) => "strawberry"
   (pluralize->as-ies "strawberr" 2) => "strawberries"]
  [root-str number-int]
  (clojure.pprint/cl-format nil (str root-str "~:@P") number-int))

(defns human-date
  "Will make a human readable date"
  [(human-date 2017 12 12) => "Twelfth of December, 2017"
   (human-date 2000 10 10) => "Tenth of October, 2000"
   (human-date 2000 01 01) => "First of January, 2000"]
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
