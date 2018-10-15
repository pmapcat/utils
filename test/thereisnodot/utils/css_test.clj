(ns thereisnodot.utils.markdown
  (:require [clojure.test :refer :all]
            [thereisnodot.utils.css :as css]))

(deftest test-html->inlined-css-html
  (testing "General workage"
    (is
     (=
      (spit
       "/home/mik/aga.html"
       (first (-> "demo.html" clojure.java.io/resource slurp  css/html->inlined-css-html)))
      {:key ["value"],
       :list ["value 1" "value 2"],
       :literal ["this is literal value.\n\nliteral values 2"],
       :another_list ["[1 2 3 4]"]}))))
