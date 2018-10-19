;; @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
;; @ Copyright (c) Michael Leachim                                                      @
;; @ You can find additional information regarding licensing of this work in LICENSE.md @
;; @ You must not remove this notice, or any other, from this software.                 @
;; @ All rights reserved.                                                               @
;; @@@@@@ At 2018-13-10 18:56 <mklimoff222@gmail.com> @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@

(ns ^{:author "Michael Leahcim"
      :doc    "A set of useful collection manipulation functions"}
    thereisnodot.utils.collections
  (:require [clojure.set :as clj-set]
            [thereisnodot.akronim.core :refer [defns]]))

(defns map-val
  "Will execute function over value of the map"
  [(map-val inc {:1 1 :2 2}) => {:1 2 :2 3}]
  [f m]
  (zipmap (keys m) (map f (vals m))))

(defns pad-coll
  "Pad collection <coll> with <pad-with> until <n> is reached"
  [(pad-coll 10 [3 4] nil) => [3 4 nil nil nil nil nil nil nil nil]
   (pad-coll  10 [3 4] "n/a") => [3 4 "n/a" "n/a" "n/a" "n/a" "n/a" "n/a" "n/a" "n/a"]]
  [n coll pad-with]
  (loop [s coll]
    (if (< (count s) n) (recur (conj s pad-with))  s)))

(defns pad-numbers
  "Pad numbers - takes a number and the length to pad to as arguments"
  [(pad-numbers 10 1234567 0) => "1234567000"]
  [n c pad-symbol]
  (apply str (pad-coll n (into [] (str c)) pad-symbol)))

(defns nested-group-by
  "From: https://stackoverflow.com/a/38842018/3362518
   Like group-by but instead of a single function, this is given a list or vec
   of functions to apply recursively via group-by. An optional `final` argument
   (defaults to identity) may be given to run on the vector result of the final
   group-by. !!careful, deep nesting is not supported"
  [(nested-group-by
     [first second]
     [["A" 2011 "Dan"]
      ["A" 2011 "Jon"]
      ["A" 2010 "Tim"]
      ["B" 2009 "Tom"]]) => 
   {"A" {2011 [["A" 2011 "Dan"] ["A" 2011 "Jon"]],
         2010 [["A" 2010 "Tim"]]},
    "B" {2009 [["B" 2009 "Tom"]]}}]
  [fs coll & [final-fn]]
  (if (empty? fs)
    ((or final-fn identity) coll)
    (map-val 
     #(nested-group-by (rest fs) % final-fn)
     (group-by (first fs) coll))))

(defns jaccard
  "Will calculate Jaccard distance over two sets"
  [(jaccard #{:a :b} #{:c :d}) => 0
   (jaccard #{:a :b :c} #{:c :d}) => 1/4
   (jaccard #{:a :b :c} #{:a :b}) => 2/3
   (jaccard #{:a :b} #{:a :b}) => 1]
  [a b]
  (/
   (count (clj-set/intersection a b))
   (count (clj-set/union a b))))


(defns jaccard-words
  "Calculate Jaccard distance between two strings split by space"
  [(jaccard-words "Hello my friend" "goodbye my friend") => 1/2
   (jaccard-words "hello nobody" "buy one") => 0
   (jaccard-words "a b c d e n" "a b c d e") => 5/6
   (jaccard-words "a n" "a n") => 1]
  [some-str some-another-str]
  (let [a (into #{} (clojure.string/split some-str #"\s+"))
        b (into #{} (clojure.string/split some-another-str #"\s+"))]
    (jaccard a b)))

(defns invert-map
  "Turn values into keys and reverse. 
   Basically, an inverted index. See tests for example"
  [(invert-map {:F [:a :b :n] :Q [:c :d :n]})
   => {:a [:F] :b [:F] :c [:Q] :d [:Q] :n [:F :Q]}
   (invert-map
    {1 ["hello" "world"]
     2 ["not" "important"]
     3 ["very" "important" "thing"]})
   => {"hello" [1],
       "world" [1],
       "not" [2],
       "important" [2 3],
       "very" [3],
       "thing" [3]}]
  [dataset]
  (->>
   dataset
   (reduce
    (fn [prev [k v]]
      (concat prev (for [item v] [item k]))) [])
   (group-by first)
   (map (fn [[key val-list]] [key (mapv last val-list)]))
   (into {})))

(defns map-longest
  "Opposite of map. On multiple collections will iterate until 
   the longest sequence has no more members.
   Will hang when used with lazy collections"
  [(map-longest list (range 1 6) (range 1 3) (range 10 15))
   => (list (list 1 1 10)
            (list 2 2 11)
            (list 3 nil 12)
            (list 4 nil 13)
            (list 5 nil 14))]
  [fn & colls]
  (let [maximum (apply max (map count colls))]
    (apply map fn (map #(take maximum (concat % (repeat nil))) colls))))

(defns order-by-collection
  "Will order input hashmap by order collection. Will append 
   non appearing at the end"
  [(order-by-collection {:a 1 :b 34 :c 87 :h 47 :d 12} [:h :d])
   => (list 47 12 1 34 87)]
  [input order-coll]
  (for [item (distinct (concat  order-coll (keys input) ))]
    (get input item)))


(defns hashmaps->sparse-table
  "Will turn a list of hashmaps into a sparse table. 
   Useful when exporting data into a spreadsheet. 
   First row is a headers row"
  [(hashmaps->sparse-table
    [{:hello "world" :blab "blip" :blop "12"}
     {:1 "asd" :2 "zc" :hello "nothing"}])
   =>
   (list (list "hello" "blab" "blop" "1" "2")
         (list "world" "blip" "12" nil nil)
         (list "nothing" nil nil "asd" "zc"))
   
   (hashmaps->sparse-table
    [{:name "hello" :surname "world"}
     {:name "blab" :surname "blip" :whatever "blop"}]
    "n/a"
    [:whatever :name :surname])
   =>
   (list
    (list "whatever" "name" "surname")
    (list "n/a" "hello" "world")
    (list "blop" "blab" "blip"))]
  ([datum]
   (hashmaps->sparse-table datum nil))
  ([datum null-type]
   (hashmaps->sparse-table datum null-type []))
  ([datum null-type order-by]
   (let [headers
         (->>
          datum
          (map keys)
          (reduce concat)
          distinct
          (concat order-by)
          distinct)]
     (conj
      (for [item datum]
        (for [head headers]
          (get item head null-type)))
      (apply list (map name headers))))))

(defns meta-iterate
  "Provide metadata in the form {:index :last? :first?} on a collection"
  [(for [[item meta-item] (meta-iterate (range 10 14))]
     [item meta-item])
   =>
   (list [10 {:index 0, :last? false, :first? true}]
         [11 {:index 1, :last? false, :first? false}]
         [12 {:index 2, :last? false, :first? false}]
         [13 {:index 3, :last? true, :first? false}])]
  [data-set]
  (let [last-by-index (dec (count data-set))]
    (for [[v k] (map list (range) data-set)]
      [k {:index v
          :last? (= v last-by-index)
          :first? (= v 0)}])))

(defns filter-map-val
  "WIll filter on value of a hashmap"
  [(filter-map-val #(= % 1) {:1 1 :2 2 :3 3}) => {:1 1}]
  [filter-fn some-map]
  (->>
   (filter
    (fn [[_ val] & item]
      (filter-fn val))
    some-map)
   (reduce conj {})))

(defns filter-map-key
  "Will filter on key of a hashmap"
  [(filter-map-key #(= % :1) {:1 1 :2 2 :3 3}) =>  {:1 1}]
  [filter-fn some-map]
  (into
   {}
   (filter
    (fn [[key _] & item]
      (filter-fn key))
    some-map)))
