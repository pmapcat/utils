(ns thereisnodot.utils.markdown
  (:require [clojure.test :refer :all]
            [thereisnodot.utils.markdown :as markdown]))

(deftest test-text->html
  (testing "General workage"
    (is
     (=
      (-> "demo.md" clojure.java.io/resource slurp markdown/text->html :meta)
      {:key ["value"],
       :list ["value 1" "value 2"],
       :literal ["this is literal value.\n\nliteral values 2"],
       :another_list ["[1 2 3 4]"]}))))
