;; @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
;; @ Copyright (c) Michael Leachim                                                      @
;; @ You can find additional information regarding licensing of this work in LICENSE.md @
;; @ You must not remove this notice, or any other, from this software.                 @
;; @ All rights reserved.                                                               @
;; @@@@@@ At 2018-15-10 22:03 <mklimoff222@gmail.com> @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@

(ns thereisnodot.utils.markdown
  (:require [clojure.walk :as walk])
  (:import
   [org.commonmark.parser Parser]
   [org.commonmark.ext.gfm.tables TablesExtension]
   [org.commonmark.ext.autolink AutolinkExtension]
   [org.commonmark.ext.gfm.strikethrough  StrikethroughExtension]
   [org.commonmark.ext.heading.anchor  HeadingAnchorExtension]
   [org.commonmark.ext.ins  InsExtension]
   [org.commonmark.ext.front.matter
    YamlFrontMatterExtension YamlFrontMatterVisitor]
   [org.commonmark.renderer.html HtmlRenderer]))


(def ^{:dynamic true} *EXTENSIONS*
  (java.util.LinkedList.
   [(TablesExtension/create)
    (AutolinkExtension/create)
    (StrikethroughExtension/create)
    (HeadingAnchorExtension/create)
    (InsExtension/create)
    (YamlFrontMatterExtension/create)]))

(def ^{:private true} parser
  (->
   (Parser/builder)
   (.extensions *EXTENSIONS*)
   (.build)))

(def ^{:private true} html-renderer
  (->
   (HtmlRenderer/builder)
   (.extensions *EXTENSIONS*)
   (.build)))

(defn- doc->meta
  [parsed-doc-tree]
  (let [yaml-front-matter-visitor (new YamlFrontMatterVisitor)]
    (do
      (.visit yaml-front-matter-visitor  parsed-doc-tree)
      (.getData yaml-front-matter-visitor))))

(defn text->html
  ([text] (text->html text {}))
  ([text params]
   (let [parsed-doc (.parse parser text)
         {:keys [keywordize-keys?]} (merge {:keywordize-keys? true} params)]
     {:html
      (.render html-renderer parsed-doc )
      :meta
      (let [meta (into {} (doc->meta parsed-doc))]
        (if keywordize-keys? (walk/keywordize-keys meta)
            meta))})))

