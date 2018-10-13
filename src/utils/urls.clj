;; @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
;; @ Copyright (c) Michael Leachim                                                      @
;; @ You can find additional information regarding licensing of this work in LICENSE.md @
;; @ You must not remove this notice, or any other, from this software.                 @
;; @ All rights reserved.                                                               @
;; @@@@@@ At 2018-13-10 18:21 <mklimoff222@gmail.com> @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@

(ns utils.urls
  (:require [ring.util.codec :as url-codec]
            [clojure.walk :as walk]))

(defn encode-url 
  [data]
  (url-codec/form-encode data))

(defn parse-url 
  [url]
  (clojure.walk/keywordize-keys
   (url-codec/form-decode url)))

(defn parse-url-l
  [url]
  (->>
   (url-codec/form-decode url)
   (map (fn [[key val] & item]
          [(keyword key)
           (cond
             (empty?  val) []
             (vector? val) val
             (string? val) [val])]))
   (into {})))

