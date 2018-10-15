;; @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
;; @ Copyright (c) Michael Leachim                                                      @
;; @ You can find additional information regarding licensing of this work in LICENSE.md @
;; @ You must not remove this notice, or any other, from this software.                 @
;; @ All rights reserved.                                                               @
;; @@@@@@ At 2018-13-10 18:18 <mklimoff222@gmail.com> @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@

(ns wireframe.utils.html
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


(defmacro case-html
  [[field-binding field] body]
  `(if (empty? ~field)
     [:div]
     (let [~field-binding ~field]
       ~body)))

(defn gen-layout-class
  [class a b c]
  (for [index  (range)]
    (condp = (mod index class)
      0 a 
      1 b
      2 c)))



(defn parse-sorting-field
  "Will parse string in the form: '-age' 
   (parse-sorting-field \"-age\") => {:inverse? true, :field :age}"
  [input]
  (let [input    (clojure.string/trim (str input))
        inverse? (clojure.string/starts-with? input "-")
        field    (if inverse? (apply str (rest input)) input)]
    (if (empty? input)
      {:inverse? false
       :field    :name}
      {:inverse? inverse?
       :field   (keyword field)})))

(defn style
  [link]
  [:link
   {:href link
    :type "text/css",
    :rel "stylesheet"}])


(defn inlet [text class]
  [:span
   [:span {:class class} (str (first text))]
   (apply str (rest text))])


(defn backlet [text class]
  [:span
   (apply str (butlast text))
   [:span {:class class}
    (str (last text))]])

(defn grid-amount
  [amount]
  (str (float (* amount (/ 100 16))) "%"))

(defn styles-map->string
  [data]
  (clojure.string/join
   " "
   (for [[k v ] data]
     (format "%s:%s;" (name k) v))))
