;; @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
;; @ Copyright (c) Michael Leachim                                                      @
;; @ You can find additional information regarding licensing of this work in LICENSE.md @
;; @ You must not remove this notice, or any other, from this software.                 @
;; @ All rights reserved.                                                               @
;; @@@@@@ At 2018-13-10 18:18 <mklimoff222@gmail.com> @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@

(ns ^{:author "Michael Leachim"
      :doc "A set of useful functions related to HTML processing"}
  thereisnodot.utils.html
  (:require [thereisnodot.akronim.core :refer [defns]])
  (:import [org.unbescape.html HtmlEscape]
           [java.net URLEncoder]))

(defn html->unescaped-html
  [some-str]
  (HtmlEscape/unescapeHtml some-str))

(defmacro yes
  "(yes [] [:hello]) => nil
   (yes \"blab\" [:hello]) => [:hello]"
  [field & body]
  `(if (not (empty? ~field))
     ~@body
     nil))

(defmacro catcher
  "evaluates body, on error, creates an exception, that binds to a variable declared in
  [err-binding]. Example 
  (catcher
   [blabus]
   (println (.getMessage blabus))
   (/ 1 0))"
  [err-binding on-error & body]
  (let [err-var (gensym 'error)]
    (list
     'try
     (cons 'do body)
     (list 'catch 'Exception err-var
           (list 'let [(first err-binding) err-var] on-error)))))

(defns gen-layout-class
  "Will generate position specific class. 
   For example: For the pivot 3 
   classes are: 
   text-align:left; text-align:center; text-align:right; 
   Useful when implementing something on CSS grid"
  [(take 9 (gen-layout-class 3 "left" "justify" "right")) =>
   (list "left" "justify" "right"
         "left" "justify" "right"
         "left" "justify" "right")]
  [pivot a b c]
  (for [index  (range)]
    (condp = (mod index pivot)
      0 a 
      1 b
      2 c)))

(defns parse-sorting-field
  "Will parse sorting field like this: (year | -year) (price | -price) (age | -age) e.t.c.
   (parse-sorting-field \"-age\") => {:inverse? true, :field :age}"
  [(parse-sorting-field "-age") => {:inverse? true :field :age}
   (parse-sorting-field "age") => {:inverse? false :field :age}
   (parse-sorting-field "blab") => {:inverse? false :field :blab}]
  [input]
  (let [input    (clojure.string/trim (str input))
        inverse? (clojure.string/starts-with? input "-")
        field    (if inverse? (apply str (rest input)) input)]
    (if (empty? input)
      {:inverse? false
       :field    :name}
      {:inverse? inverse?
       :field   (keyword field)})))

(defns style
  "Will make style in a Hiccup syntax"
  [(style "some-link.css")
   => [:link {:href "some-link.css", :type "text/css", :rel "stylesheet"}]]
  [link]
  [:link
   {:href link
    :type "text/css",
    :rel "stylesheet"}])

(defns inlet
  "Will change first letter in hiccup form"
  [(inlet "Hello" "blab")
   => [:span [:span {:class "blab"} "H"] "ello"]]
  [text text-class]
  [:span
   [:span {:class text-class} (str (first text))]
   (apply str (rest text))])

(defns backlet
  "Will change last letter in hiccup form"
  [(backlet "Hello" "blab")
   => [:span "Hell" [:span {:class "blab"} "o"]]]
  [text class-item]
  [:span
   (apply str (butlast text))
   [:span {:class class-item}
    (str (last text))]])

(defns grid-amount
  "Percentage on classical grid. Size is the amount of blocks in a grid"
  [(map  (partial grid-amount 16) (range 16)) =>
   (list "0.0%" "6.25%" "12.5%" "18.75%" "25.0%" "31.25%" "37.5%" "43.75%" "50.0%" "56.25%" "62.5%" "68.75%" "75.0%" "81.25%" "87.5%" "93.75%")]
  [size amount]
  (str (float (* amount (/ 100 size))) "%"))

(defns styles-map->string
  "Will turn a map style of CSS into an inline style of CSS"
  [(styles-map->string {:background "green" :color "white" :font-weight "900"})
   =>
   "background:green; color:white; font-weight:900;"]
  [data]
  (clojure.string/join
   " "
   (for [[k v ] data]
     (format "%s:%s;" (name k) v))))
