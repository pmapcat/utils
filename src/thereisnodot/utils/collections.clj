;; @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
;; @ Copyright (c) Michael Leachim                                                      @
;; @ You can find additional information regarding licensing of this work in LICENSE.md @
;; @ You must not remove this notice, or any other, from this software.                 @
;; @ All rights reserved.                                                               @
;; @@@@@@ At 2018-13-10 18:56 <mklimoff222@gmail.com> @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@

(ns thereisnodot.utils.collections
  (:require [clojure.set :as clj-set]))
;; rename: collections

(defn map-vec
  ;; (map-on-vector
  ;;  ["a" "b" "n"]
  ;;  {"n" 12 "q" 10}
  ;;  "n/a")
  [some-vector some-map or]
  (for [item some-vector]
    (get some-map item or)))

(defn map-val
  [f m]
  (zipmap (keys m) (map f (vals m))))

(defn pad-coll
  "Pad collection <coll> with <pad-with> until <n> is reached
   e.g. (pad-coll 10 [3 4] nil) => [3 4 nil nil nil nil nil nil nil nil]"
   [n coll pad-with] 
   (loop [s coll]  
     (if (< (count s) n) (recur (conj s pad-with))  s)))

(defn pad-numbers
  "Zero Pad numbers - takes a number and the length to pad to as arguments"
  [n c pad-symbol]
  (apply str (pad-coll n (into [] (str c)) pad-symbol)))

(defn nested-group-by
  "From: https://stackoverflow.com/a/38842018/3362518
   Like group-by but instead of a single function, this is given a list or vec
   of functions to apply recursively via group-by. An optional `final` argument
   (defaults to identity) may be given to run on the vector result of the final
   group-by.
   !!careful, deep nesting is not supported
   usage example
   (def foo [[\"A\" 2011 \"Dan\"]
             [\"A\" 2011 \"Jon\"]
             [\"A\" 2010 \"Tim\"]
             [\"B\" 2009 \"Tom\"] ])
   (nested-group-by [first second] foo)"
  [fs coll & [final-fn]]
  (if (empty? fs)
    ((or final-fn identity) coll)
    (map-val (group-by (first fs) coll)
             #(nested-group-by (rest fs) % final-fn))))

(defn jaccard
  [a b]
  (/
     (count (clj-set/intersection a b))
     (count (clj-set/union a b))))

(defn jaccard-string
  "Calculate Jaccard distance between two strings split by space"
  [some-str some-another-str]
  (let [a (into #{} (clojure.string/split some-str #"\s+"))
        b (into #{} (clojure.string/split some-another-str #"\s+"))]
    (jaccard a b)))

(defn invert-group
  "take a group of form {:F [:a :b :n] :Q [:c :d :n]}
   turn it into a group of form:
   {:a [:F] :b [:F] :c [:Q] :d [:Q] :n [:F :Q]}"
  [dataset]
  (->>
   dataset
   (reduce
    (fn [prev [k v]]
      (concat
       prev
       (for [item v]
         [item k]))) [])
   (group-by first)
   (map
    (fn [[key val-list]]
      [key (mapv last val-list)]))
   (into {})))

(defn map-longest
  ([fn & colls]
   (let [maximum (apply max  (map count colls))]
     (apply map fn (map #(take maximum (concat % (repeat nil))) colls)))))

(defn partition-with-nil-tails
  "(println (partition-with-nil-tails 3 (range 10)))
   => ((nil 0 1) (0 1 2) (1 2 3) (2 3 4) (3 4 5) (4 5 6) (5 6 7) (6 7 8) (7 8 9) (8 9 nil))"
  [part-size coll]
  (let [less-by-one (dec part-size)]
    (concat
     (list (concat [nil] (first (partition less-by-one 1 coll))))
     (partition part-size 1 coll)
     (list (concat (last  (partition less-by-one 1  coll)) [nil])))))

(defn keyword-keys-to-int-recursive
  "Recursively transforms all map keys from keywords to integers.
   If cannot, leaves the key as it was before, e.g. :keyword"
  [m]
  (let [f (fn [[k v]] (if (keyword? k) [(mik-parse-int (str (name k)) k) v] [k v]))]
    ;; only apply to maps
    (cljs-walk/postwalk (fn [x] (if (map? x) (into {} (map f x)) x)) m)))


(defn list-of-hashmaps->list-of-lists-aligned
  ([datum]
   (list-of-hashmaps->list-of-lists-aligned datum nil))
  ([datum null-type]
   (list-of-hashmaps->list-of-lists-aligned datum null-type identity))
  ([datum null-type order-by]
   (let [keywords
         (order-by
          (reduce
           (fn [prev next]
             (apply conj prev (keys next)))
           #{} datum))]
     (conj
       (for [item datum]
         (for [keyword keywords]
           (keyword item null-type)))
       (apply list (map name keywords))))))

(defn iterate-with-meta
  [data-set]
  (let [last-by-index (dec (count data-set))]
    (for [[v k] (map list (range) data-set)]
      [k {:point k
          :index v
          :last? (= v last-by-index)
          :first? (= v 0)}])))

(defn filter-map-on-val
  "Sample: (filter-map-on-val #(= % 1) {:1 1 :2 2 :3 3})"
  [filter-fn some-map]
  (into
   {}
   (filter
    (fn [[_ val] & item]
      (filter-fn val))
    some-map)))

(defn filter-map-on-key
  [filter-fn some-map]
  (into
   {}
   (filter
    (fn [[key _] & item]
      (filter-fn key))
    some-map)))


