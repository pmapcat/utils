;; @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
;; @ Copyright (c) Michael Leachim                                                      @
;; @ You can find additional information regarding licensing of this work in LICENSE.md @
;; @ You must not remove this notice, or any other, from this software.                 @
;; @ All rights reserved.                                                               @
;; @@@@@@ At 2018-13-10 18:04 <mklimoff222@gmail.com> @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
(ns utils.hashes
  (:import [java.security MessageDigest]
           [utils.java.fnv FNV]))

(def ^{:private true} fnv (new FNV))

(defn uuid [] (str (java.util.UUID/randomUUID)))

(defn md5
  "Calculate md5 hash of string"
  [s]
  (let [algorithm (MessageDigest/getInstance "MD5")
        size (* 2 (.getDigestLength algorithm))
        raw (.digest algorithm (.getBytes s))
        sig (.toString (BigInteger. 1 raw) 16)
        padding (apply str (repeat (- size (count sig)) "0"))]
    (str padding sig)))

(defn fnv-1a-32
  [data-str]
  (if (string? data-str)
    (.fnv1a_32  fnv (byte-array (map byte (seq data-str))))
    (throw (Exception. (str  data-str " is not a string") ))))

(defn fnv-1a-64
  [data-str]
  (if (string? data-str)
    (.fnv1a_64  fnv (byte-array (map byte (seq data-str))))
    (throw (Exception. (str  data-str " is not a string") ))))

