;; @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
;; @ Copyright (c) Michael Leachim                                                      @
;; @ You can find additional information regarding licensing of this work in LICENSE.md @
;; @ You must not remove this notice, or any other, from this software.                 @
;; @ All rights reserved.                                                               @
;; @@@@@@ At 2018-16-10 19:53 <mklimoff222@gmail.com> @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@

(ns thereisnodot.utils.collections-test
  (:require [clojure.test :refer :all]
            [thereisnodot.utils.collections :as collections]))

(deftest test-inlines-within-namespace
  (doseq [[symbol access] (ns-publics 'thereisnodot.utils.collections)]
    (testing (str "Testing inlines of: " symbol)
      (is (=  (test access) :ok)))))

(deftest test-hashmaps->sparse-table
  (testing "Basic workage"
    (is (=  (collections/hashmaps->sparse-table
             [{:hello "world" :blab "blip" :blop "12"}
              {:1 "asd" :2 "zc" :hello "nothing"}])
            (list (list "hello" "blab" "blop" "1" "2")
                  (list "world" "blip" "12" nil nil)
                  (list "nothing" nil nil "asd" "zc"))))))

(deftest test-map-longest
  (testing "Basic workage"
    (is (=  (collections/map-longest list (range 1 10) (range 1 3) (range 10 15)) 
            (list (list 1 1 10)
                  (list 2 2 11)
                  (list 3 nil 12)
                  (list 4 nil 13)
                  (list 5 nil 14)
                  (list 6 nil nil)
                  (list 7 nil nil)
                  (list 8 nil nil)
                  (list 9 nil nil))))))

(deftest test-invert-group
  (testing "Basic workage"
    (is (= (collections/invert-map {:F [:a :b :n] :Q [:c :d :n]})
           {:a [:F] :b [:F] :c [:Q] :d [:Q] :n [:F :Q]}))
    (is (= (collections/invert-map
            {1 ["hello" "world"] 2 ["not" "important"] 3 ["very" "important" "thing"]})
           {"hello" [1], "world" [1], "not" [2], "important" [2 3], "very" [3], "thing" [3]})))
  (testing "Edge cases"
    (is (= (collections/invert-map {}) {}))))

(deftest test-pad-coll
  (testing "Basic workage"
    (is (= (collections/pad-coll  10 [3 4] nil) [3 4 nil nil nil nil nil nil nil nil]))
    (is (= (collections/pad-coll  10 [3 4] "n/a") [3 4 "n/a" "n/a" "n/a" "n/a" "n/a" "n/a" "n/a" "n/a"]))))

(deftest test-filter-map-val
  (testing "Basic workage"
    (is (= (collections/filter-map-val #(= % 1) {:1 1 :2 2 :3 3}) {:1 1}))))

(deftest test-filter-map-key
  (testing "Basic workage"
    (is (= (collections/filter-map-key #(= % :1) {:1 1 :2 2 :3 3}) {:1 1}))))
