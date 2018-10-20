;; @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
;; @ Copyright (c) Michael Leachim                                                      @
;; @ You can find additional information regarding licensing of this work in LICENSE.md @
;; @ You must not remove this notice, or any other, from this software.                 @
;; @ All rights reserved.                                                               @
;; @@@@@@ At 2018-13-10 20:04 <mklimoff222@gmail.com> @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@

(ns thereisnodot.utils.spreadsheets-test
  (:require [clojure.test :refer :all]
            [thereisnodot.utils.fs :as utils-fs]
            [thereisnodot.utils.spreadsheets :as excel]))

(deftest test-inlines-within-namespace
  (doseq [[symbol access] (ns-publics 'thereisnodot.utils.spreadsheets)]
    (when (:test (meta access))
      (testing (str "Testing inlines of: " symbol)
        (is (=  (test access) :ok))))))


(deftest test-extract-keys
  (testing "Basic workage"
    (is
     (=
      (#'excel/extract-keys [{:hello "world"} {:blab "blip"} {:a "sd" :za "zd"}] [:za :a])
      (list :za :a :hello :blab)))))

(deftest test-maps->header-and-rows
  (testing "Basic workage"
    (is
     (= (#'excel/maps->header-and-rows
         [{:name "hello" :surname "world"}
          {:name "blab" :surname "blip" :varum "blop"}]
         "n/a"
         [:varum :name :surname])
        {:header [:varum :name :surname], :rows
         (list
          (list "n/a" "hello" "world")
          (list "blop" "blab" "blip"))}))))

(deftest test-maps->justified-lists
  (testing "Basic workage"
    (is
     (=
      (#'excel/maps->justified-lists
       [{:name "hello" :surname "world"}
        {:name "blab" :surname "blip" :varum "blop"}]
       "n/a"
       [:varum :name :surname])
      (list
       (list "varum" "name" "surname")
       (list "n/a" "hello" "world")
       (list "blop" "blab" "blip"))))))

(deftest test-excel-slurp
  (testing "Basic workage"
    (is
     (=
      (excel/excel-slurp (.getFile (clojure.java.io/resource "demo.xlsx")))
      {"Sheet1"
       (list
        {:KeyName "Value1", :Key2 "Value3", :Key3 "Value5"}
        {:KeyName "Value2", :Key2 "Value4", :Key3 "Value6"})}))))

(deftest test-excel-spit
  (let [tmpfile (utils-fs/temp-file "whatever" ".xlsx")
        datum    
        [(str tmpfile)
         {"hello" (list {:hello_world "blab" :blab "hello world"}
                        {:hello_world "blab" :blab "hello world"}
                        {:hello_world "blab" :blab "hello world"}
                        {:hello_world "blab" :blab "hello world"})
          
          "world"  (list {:nasty "things" :says "behemoth"}
                         {:nasty "things" :says "behemoth"}
                         {:nasty "things" :says "behemoth"}
                         {:nasty "things" :says "behemoth"})
          
          "begemoth" (list {:is "about" :to "exist"}
                           {:is "about" :to "exist"}
                           {:is "about" :to "exist"}
                           {:is "about" :to "exist"})} "n/a" []]]
    
    (testing "Basic workage"
      (apply excel/excel-spit datum)
      (is
       (=
        (into {} (excel/excel-slurp (first datum)))
        (second datum)))
      (.delete tmpfile))))
