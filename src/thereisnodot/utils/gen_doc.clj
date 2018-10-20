;; @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
;; @ Copyright (c) Michael Leahcim                                                      @
;; @ You can find additional information regarding licensing of this work in LICENSE.md @
;; @ You must not remove this notice, or any other, from this software.                 @
;; @ All rights reserved.                                                               @
;; @@@@@@ At 2018-10-20 01:10 <thereisnodotcollective@gmail.com> @@@@@@@@@@@@@@@@@@@@@@@@

(ns
    ^{:doc "A documentation generation system"
      :author "Michael Leachim"}
    thereisnodot.utils.gen-doc)

(defn- multiline-comment
  [some-item]
  (clojure.string/replace some-item #"\n" "\n;;    "))
(defn- gen-example
  [item-meta]
  (clojure.string/trim-newline
   (apply
    str
    (for [[fn-call _ result] item-meta]
      (str fn-call  "\n;; => "  (multiline-comment result) "\n\n")))))

(defn- gen-params
  [item-name arglist]
  (clojure.string/trim-newline
   (clojure.string/join
    "<br>"
    (for [i arglist]
      (str "`" (cons item-name i) "`")))))

(defn- gen-single-fn
  [{:keys [name arglists doc ns name] :as datum}]
  (format
   "#### %s

%s

%s

**Usage:**
```clojure
(require '[%s :refer [%s]])

%s
```
<hr>" name (gen-params name arglists) doc ns name (gen-example (:akronim/example datum))))

(defn- gen-template-on-ns
  [namespace]
  (clojure.string/join
   "\n"
   (for [[_ reference] (ns-publics namespace)]
     (gen-single-fn (meta reference)))))

(require '[thereisnodot.utils.scale]
         '[thereisnodot.utils.collections]
         '[thereisnodot.utils.strings]
         '[thereisnodot.utils.framerate]
         '[thereisnodot.utils.html]
         '[thereisnodot.utils.transliterate]
         '[thereisnodot.utils.fs])

(defn- wrap-replace-make
  []
  (let [template (slurp  (clojure.java.io/resource "README_template.md"))]
    (->
     template
     (.replace "{{scale}}"         (gen-template-on-ns 'thereisnodot.utils.scale))
     (.replace "{{collections}}"   (gen-template-on-ns 'thereisnodot.utils.collections))
     (.replace "{{strings}}"       (gen-template-on-ns 'thereisnodot.utils.strings))
     (.replace "{{framerate}}"     (gen-template-on-ns 'thereisnodot.utils.framerate))
     (.replace "{{html}}"          (gen-template-on-ns 'thereisnodot.utils.html))
     (.replace "{{transliterate}}" (gen-template-on-ns 'thereisnodot.utils.transliterate))
     (.replace "{{fs}}"            (gen-template-on-ns 'thereisnodot.utils.fs)))))

(comment
  (let [testfn (:test (meta (last (first (ns-publics 'thereisnodot.utils.scale)))))]
    (zp/zprint-fn testfn))
  
  (spit "README.md" (wrap-replace-make)))

