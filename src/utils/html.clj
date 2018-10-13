;; @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
;; @ Copyright (c) Michael Leachim                                                      @
;; @ You can find additional information regarding licensing of this work in LICENSE.md @
;; @ You must not remove this notice, or any other, from this software.                 @
;; @ All rights reserved.                                                               @
;; @@@@@@ At 2018-13-10 18:18 <mklimoff222@gmail.com> @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@

(ns utils.html
  (:import [org.unbescape.html HtmlEscape]
           [utils.java.paginator Paginator]
           [utils.java.cssinliner CSSInliner]
           [java.net URLEncoder]))

(defn html->unescaped-html
  [some-str]
  (HtmlEscape/unescapeHtml some-str))

(defn html->html-with-inlined-css
  "Will inline CSS styles that are included within given page. 
   Useful when generating a EMail template"
  [data-html ]
  (CSSInliner/inlineStyles data-html))

(defn java-paginate->list-of-pages
  "int,int -> []string"
  [cur-page  last-page]
  (Paginator/Paginate cur-page last-page))

(defn clojure-paginate->list-of-pages
  "int,int -> [{:page 2 :cur? false :name \"2\"}]" 
  [current last-item]
  (let [delta 2
        left (- current delta)
        right (+ current delta 1)]
    (->>
     (filter #(or (= % 1) (= % last-item) (and (>= % left) (< % right))) (range 1 (inc last-item)))
     (reduce
      (fn [prev next]
        (let [prev-last (last prev)
              collapse?  (and  (number? prev-last) (> (- next prev-last) 1))]
          (cond
            (empty? prev)
            [next]
            collapse?
            (-> prev
                (conj "...")
                (conj next))
            :else
            (conj prev next)))) [])
     (map #(if (= % "...")
             {:page nil :name "..."   :cur?  false}
             {:page %   :name (str %) :cur? (= % current)})))))

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

(defn for-every-and-last
  [data-set]
  (let [last-by-index (dec (count data-set))]
    (for [[v k] (map list (range) data-set)]
      [k {:point k
          :index v
          :last? (= v last-by-index)
          :first? (= v 0)}])))

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

