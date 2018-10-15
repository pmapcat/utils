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

(defn- html->get-attr
  [key html]
  (.attr html key))

(defn- html->dissoc
  [key html]
  (.remove html key))
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
     (for [[_ selector content] (re-seq #"(.*?)\{(.*?)\}" (clojure.string/trim (.data style-element)))]
       {:selector (remove-new-lines selector)
        :rule     (remove-new-lines content)}))))

(defn- set-element
  [item html]
  (doseq [element (html->elements (:selector item) html)]
    (as-> element $
      (html->get-attr "style" $)
      (join-styles $ (:rule item))
      (html->set-attr "style" $ html))))

(defn html->inlined-css-html-silent
  "Will try its best to inline data"
  [html]
  (let [html (string->html html)
        err-collector (atom [])]
    (do
      (doseq [item (html->style-pairs html)]
        (try
          (set-element item html)
          (catch Selector$SelectorParseException  e
            (swap! err-collector conj (:cause (Throwable->map e))))))
      [(html->string html) @err-collector])))

;; package thereisnodot.java.cssinliner;
;; import java.io.IOException;
;; import java.util.StringTokenizer;
;; import org.jsoup.Jsoup;
;; import org.jsoup.nodes.Document;
;; import org.jsoup.nodes.Element;
;; import org.jsoup.select.Elements;

;; /**
;;  * Css inliner for email,from
;;  * http://stackoverflow.com/questions/4521557/automatically-convert-style-sheets-to-inline-style
;;  * 
;;  * @author dennis<xzhuang@avos.com>
;;  * @date 2013-1-9
;;  */
;; // Modified to exclude Google Guava Dependency

;; public class CSSInliner {
    
;;     public static boolean isNullOrEmpty(String param) { 
;;       return param == null || param.trim().length() == 0;
;;     }
;;     public static String inlineStyles(String html) throws IOException {
;;         // Document doc = Jsoup.connect("http://mypage.com/inlineme.php").get();
;;         Document doc = Jsoup.parse(html);
;;         String style = "style";
;;         Elements els = doc.select(style);// to get all the style elements
;;         for (Element e : els) {
;;             String styleRules = e.getAllElements().get(0).data().replaceAll("\n", "").trim(), delims =
;;                     "{}";
;;             StringTokenizer st = new StringTokenizer(styleRules, delims);
;;             while (st.countTokens() > 1) {
;;                 String selector = st.nextToken(), properties = st.nextToken();
;;                 // Process selectors such as "a:hover"
;;                 if (selector.indexOf(":") > 0) {
;;                     selector = selector.substring(0, selector.indexOf(":"));
;;                 }
;;                 if (isNullOrEmpty(selector)) {
;;                     continue;
;;                 }
;;                 Elements selectedElements = doc.select(selector);
;;                 for (Element selElem : selectedElements) {
;;                     String oldProperties = selElem.attr(style);
;;                     selElem.attr(
;;                         style,
;;                         oldProperties.length() > 0 ? concatenateProperties(oldProperties,
;;                             properties) : properties);
;;                 }
;;             }
;;             e.remove();
;;         }
;;         return doc.toString();
;;     }

;;     private static String concatenateProperties(String oldProp, String newProp) {
;;         oldProp = oldProp.trim();
;;         if (!newProp.endsWith(";")) {
;;             newProp += ";";
;;         }
;;         return newProp + oldProp; // The existing (old) properties should take precedence.
;;     }
;; }

