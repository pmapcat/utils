;; @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
;; @ Copyright (c) Michael Leachim                                                      @
;; @ You can find additional information regarding licensing of this work in LICENSE.md @
;; @ You must not remove this notice, or any other, from this software.                 @
;; @ All rights reserved.                                                               @
;; @@@@@@ At 2018-13-10 18:04 <mklimoff222@gmail.com> @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
(ns thereisnodot.utils.hashes
  (:import [java.security MessageDigest]))

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

;; (defn fnv-1a-32
;;   [data-str]
;;   (if (string? data-str)
;;     (.fnv1a_32  fnv (byte-array (map byte (seq data-str))))
;;     (throw (Exception. (str  data-str " is not a string") ))))

;; (defn fnv-1a-64
;;   [data-str]
;;   (if (string? data-str)
;;     (.fnv1a_64  fnv (byte-array (map byte (seq data-str))))
;;     (throw (Exception. (str  data-str " is not a string") ))))

;; package utils.java.fnv;

;; import java.math.BigInteger;

;; public class FNV {
;;   private static final BigInteger INIT32  = new BigInteger("811c9dc5",         16);
;;   private static final BigInteger INIT64  = new BigInteger("cbf29ce484222325", 16);
;;   private static final BigInteger PRIME32 = new BigInteger("01000193",         16);
;;   private static final BigInteger PRIME64 = new BigInteger("100000001b3",      16);
;;   private static final BigInteger MOD32   = new BigInteger("2").pow(32);
;;   private static final BigInteger MOD64   = new BigInteger("2").pow(64);

;;   public BigInteger fnv1_32(byte[] data) {
;;     BigInteger hash = INIT32;

;;     for (byte b : data) {
;;       hash = hash.multiply(PRIME32).mod(MOD32);
;;       hash = hash.xor(BigInteger.valueOf((int) b & 0xff));
;;     }

;;     return hash;
;;   }

;;   public BigInteger fnv1_64(byte[] data) {
;;     BigInteger hash = INIT64;

;;     for (byte b : data) {
;;       hash = hash.multiply(PRIME64).mod(MOD64);
;;       hash = hash.xor(BigInteger.valueOf((int) b & 0xff));
;;     }

;;     return hash;
;;   }

;;   public BigInteger fnv1a_32(byte[] data) {
;;     BigInteger hash = INIT32;

;;     for (byte b : data) {
;;       hash = hash.xor(BigInteger.valueOf((int) b & 0xff));
;;       hash = hash.multiply(PRIME32).mod(MOD32);
;;     }

;;     return hash;
;;   }

;;   public BigInteger fnv1a_64(byte[] data) {
;;     BigInteger hash = INIT64;

;;     for (byte b : data) {
;;       hash = hash.xor(BigInteger.valueOf((int) b & 0xff));
;;       hash = hash.multiply(PRIME64).mod(MOD64);
;;     }

;;     return hash;
;;   }
;; }
