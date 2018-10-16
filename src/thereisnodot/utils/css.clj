;; @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
;; @ Copyright (c) Michael Leachim                                                      @
;; @ You can find additional information regarding licensing of this work in LICENSE.md @
;; @ You must not remove this notice, or any other, from this software.                 @
;; @ All rights reserved.                                                               @
;; @@@@@@ At 2018-15-10 23:23 <mklimoff222@gmail.com> @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@

(ns thereisnodot.utils.css
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

(defn- html->text
  [html]
  (-> html .getAllElements .get .data))

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

(defn html->inlined-css-html
  "Will inline css in a given HTML
   Available params are: "
  ([html]
   (html->inlined-css-html html {}))
  ([html  params]
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
         (html->string html))))))
