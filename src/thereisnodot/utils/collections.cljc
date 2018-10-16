;; @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
;; @ Copyright (c) Michael Leachim                                                      @
;; @ You can find additional information regarding licensing of this work in LICENSE.md @
;; @ You must not remove this notice, or any other, from this software.                 @
;; @ All rights reserved.                                                               @
;; @@@@@@ At 2018-13-10 18:56 <mklimoff222@gmail.com> @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@

(ns thereisnodot.utils.collections
  (:require [clojure.set :as clj-set]))

(defn map-order
  "Will select keys from a map in a given order"
  [some-vector some-map or]
  (for [item some-vector]
    (get some-map item or)))

(defn map-val
  "Will execute function over value of the map"
  [f m]
  (zipmap (keys m) (map f (vals m))))

(defn pad-coll
  "Pad collection <coll> with <pad-with> until <n> is reached
   e.g. (pad-coll 10 [3 4] nil) => [3 4 nil nil nil nil nil nil nil nil]"
   [n coll pad-with] 
   (loop [s coll]  
     (if (< (count s) n) (recur (conj s pad-with))  s)))

(defn pad-numbers
  "Pad numbers - takes a number and the length to pad to as arguments"
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
  "Will calculate Jaccard distance over two sets"
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

(defn invert-map
  "Turn values into keys and reverse. 
   Basically, an inverted index. See tests for example"
  [dataset]
  (->>
   dataset
   (reduce
    (fn [prev [k v]]
      (concat prev (for [item v] [item k]))) [])
   (group-by first)
   (map (fn [[key val-list]] [key (mapv last val-list)]))
   (into {})))

(defn map-longest
  "Opposite of map. On multiple collections will iterate until 
   the longest sequence has no more members.
   Will hang when used with lazy collections"
  [fn & colls]
  (let [maximum (apply max (map count colls))]
    (apply map fn (map #(take maximum (concat % (repeat nil))) colls))))

(defn hashmaps->sparse-table
  "Will turn a list of hashmaps into a sparse table. 
   Useful when exporting data into a spreadsheet. 
   First row is a headers row"
  ([datum]
   (hashmaps->sparse-table datum nil))
  ([datum null-type]
   (hashmaps->sparse-table datum null-type identity))
  ([datum null-type order-by]
   (let [header-items
         (->> datum
              (reduce
               (fn [prev next]
                 (apply conj prev (keys next)))
               #{})
              order-by)]
     (conj
      (for [item datum]
        (for [head header-items]
          (get item head null-type)))
      (apply list (map name header-items))))))

(defn meta-iterate
  "Provide metadata in the form {:index :last? :first?} on a collection
   For example:
   (for [[item {:keys [last? first? index]}] (meta-iterate (range 10 15))]
     (println item last? first? index))"
  [data-set]
  (let [last-by-index (dec (count data-set))]
    (for [[v k] (map list (range) data-set)]
      [k {:index v
          :last? (= v last-by-index)
          :first? (= v 0)}])))

(defn filter-map-val
  "WIll filter on value of a hashmap. See tests for example
   "
  [filter-fn some-map]
  (->>
   (filter
    (fn [[_ val] & item]
      (filter-fn val))
    some-map)
   (reduce conj {})))

(defn filter-map-key
  [filter-fn some-map]
  (into
   {}
   (filter
    (fn [[key _] & item]
      (filter-fn key))
    some-map)))

