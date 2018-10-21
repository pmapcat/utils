;; @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
;; @ Copyright (c) Michael Leahcim                                                      @
;; @ You can find additional information regarding licensing of this work in LICENSE.md @
;; @ You must not remove this notice, or any other, from this software.                 @
;; @ All rights reserved.                                                               @
;; @@@@@@ At 2018-10-21 21:12 <thereisnodotcollective@gmail.com> @@@@@@@@@@@@@@@@@@@@@@@@

(ns
    ^{:author "Michael Leahcim"
      :doc    "CSS inliner for HTML. Based on "}
    thereisnodot.utils.css
  (:require [thereisnodot.akronim.core :refer [defns]])
  (:import [org.jsoup Jsoup]
           [org.jsoup.nodes Document Element]
           [org.jsoup.select Elements Selector]
           [org.jsoup.select Selector$SelectorParseException]
           [java.util StringTokenizer]))

(defn- string->html
  [input]
  (Jsoup/parse input))

(defn- html->string
  [html]
  (.toString html))

(defn- html->elements
  [selector html]
  (.select html selector))

(defn- html->set-attr
  [key val html]
  (.attr html key val))
(defn- html->del-attr
  [key html]
  (.removeAttr html key))

(defn- html->get-attr
  [key html]
  (.attr html key))

(defn- remove-new-lines
  [datum]
  (clojure.string/replace datum #"\n+" " "))

(defn- join-styles
  ;; (join-styles ";;asdasdasd;;" ";;zczxc;;;")
  [old-one new-one]
  (apply str (-> (str ";" old-one ";" new-one ";" ) (clojure.string/replace  #"\;+" ";") butlast rest)))

(defn- html->style-pairs
  [html]
  (flatten
   (for [style-element (html->elements "style" html)]
     (filter
      (comp not nil?)
      (for [[_ selector content] (re-seq #"(.*?)\{(.*?)\}" (clojure.string/trim (.data style-element)))]
        {:selector (remove-new-lines (clojure.string/trim selector))
         :rule     (remove-new-lines (clojure.string/trim content))})))))

(defn- delete-by-selector
  [selector html]
  (doseq [el (html->elements selector html)]
    (.remove  el)))

(defn- element-change
  [element item]
  (as-> element $
    (html->get-attr "style" $)
    (join-styles $ (:rule item))
    (html->set-attr "style" $ element)))

(defn- html->inlined-css-html-function
  [html params]
  (let [{:keys [silent? delete-style? strip-class?]} (merge  {:silent? true :strip-class? true :delete-style? true} params)
         html (string->html html)
         err-collector (atom [])]
     (do
       (doseq [item (html->style-pairs html)]
         (if silent?
           (try
             (doseq [element (html->elements (:selector item) html)]
               (element-change element item))
             (catch Selector$SelectorParseException e
               (swap! err-collector conj (:cause (Throwable->map e)))))
           (doseq [element (html->elements (:selector item) html)]
             (element-change element item))))
       (when delete-style?
         (delete-by-selector "style" html))
       (when strip-class?
         (doseq [el (html->elements "*" html)]
           (html->del-attr "class" el)))
       (if silent?
         [(html->string html) @err-collector]
         [(html->string html) @err-collector]))))

(defns inline-css-of-a-html
  "
The implementation is based upon:  https://stackoverflow.com/a/4521740/3362518

Will inline css of a given HTML string
Does not take into account linked CSS (CSS that is passed through `link` HTML tag) 
Available params are: `:silent?`, `:strip-class?`, `:delete-style?`

* `:silent?` (`true` by default) will suppress potential errors e.g. (@media selectors), 
   and continue inlining. When set to `false` will throw `SelectorParseException`
* `:strip-class?` (`true` by default)  will remove classes from the html
* `:delete-style?` (`true` by default) will remove `style` tags from the source

will return a vector of two. First with inlined HTML, second with errors that occured during the inlining. In case of `:silent?` `false` the second vector will be empty. "
  [(first (inline-css-of-a-html "<style>.demo-class {background:green;}</style>
<div class='demo-class'>Hello</div>"))
   =>
   "<html>
 <head> 
 </head>
 <body>
  <div style=\"background:green\">
   Hello
  </div>
 </body>
</html>"]
  ([html-string]
   (inline-css-of-a-html html-string {}))
  ([html-string params]
   (html->inlined-css-html-function html-string params)))




