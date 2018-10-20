;; @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
;; @ Copyright (c) Michael Leachim                                                      @
;; @ You can find additional information regarding licensing of this work in LICENSE.md @
;; @ You must not remove this notice, or any other, from this software.                 @
;; @ All rights reserved.                                                               @
;; @@@@@@ At 2018-15-10 22:03 <mklimoff222@gmail.com> @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@

(ns ^{:doc "Thin wrapper over Java commonmark markdown implementation"
      :author "Michael Leahcim"}
    thereisnodot.utils.markdown
  (:require [clojure.walk :as walk]
            [thereisnodot.akronim.core :refer [defns]])
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

(defns text->html
  "Will translate markdown text into HTML.
   This is a thin wrapper over: https://github.com/atlassian/commonmark-java
   The following extensions are enabled by default:

   * Yaml metadata extraction `YamlFrontMatterExtension`
   * `InsExtension`. Underlining of text by enclosing it in ++.
   * Heading anchors via `HeadingAnchorExtension`
   * Tables using pipes, as in: [Github Flavored Markdown](https://help.github.com/articles/organizing-information-with-tables/)
   * Strikethrough of text by enclosing it in ~~ via `StrikethroughExtension`
   * Autolink: turns plain links such as URLS into links via `AutolinkExtension`

   The main reason behind using wrapper because 
   commonmark implementation doesn't have problems with embedded HTML parsing. 

   The parameters currently only encode `:keywordize-keys?` field"
  [(text->html "
---
hello: world
blab:  blip
blop:  blop
---
# This is a library
## And here is its link

https://github.com/MichaelLeachim/utils
") =>
   {:html "<h1 id=\"this-is-a-library\">This is a library</h1>
<h2 id=\"and-here-is-its-link\">And here is its link</h2>
<p><a href=\"https://github.com/MichaelLeachim/utils\">https://github.com/MichaelLeachim/utils</a></p>
", :meta {:hello ["world"], :blab ["blip"], :blop ["blop"]}}]
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
