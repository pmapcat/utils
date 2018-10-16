;; @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
;; @ Copyright (c) Michael Leachim                                                      @
;; @ You can find additional information regarding licensing of this work in LICENSE.md @
;; @ You must not remove this notice, or any other, from this software.                 @
;; @ All rights reserved.                                                               @
;; @@@@@@ At 2018-16-10 19:53 <mklimoff222@gmail.com> @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@

(ns thereisnodot.utils.css-test
  (:require [clojure.test :refer :all]
            [thereisnodot.utils.css :as css])
  (:import [org.jsoup.select Selector$SelectorParseException]))

(deftest test-html->inlined-css-html
  (let [src (-> "demo.html" clojure.java.io/resource slurp)]
    (testing "General workage. (Demo expects some @media errors). But, nevertheless, output should exist"
      (let [[html errors] (css/html->inlined-css-html src)]
        (is (= (not (empty? errors)) true))
        (is (= (not (empty? html)) true))
        ;; (spit "/home/mik/aga.html" html)
        ))
    
    (testing "Non silent should throw a @media parsing error"
      ;; should just check this stff visually. There is no point
      ;; in doing it harder
      (is (thrown? Selector$SelectorParseException
                   (css/html->inlined-css-html src {:silent? false}))))))
