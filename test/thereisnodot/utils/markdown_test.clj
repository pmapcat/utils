(ns thereisnodot.utils.markdown-test
  (:require [clojure.test :refer :all]
            [thereisnodot.utils.markdown :as markdown]))

(deftest test-inlines-within-namespace
  (doseq [[symbol access] (ns-publics 'thereisnodot.utils.markdown)]
    (when (:test (meta access))
      (testing (str "Testing inlines of: " symbol)
        (is (=  (test access) :ok))))))

(deftest test-text->html
  (testing "General workage"
    (is
     (=
      (-> "demo.md" clojure.java.io/resource slurp markdown/text->html :meta)
      {:key ["value"],
       :list ["value 1" "value 2"],
       :literal ["this is literal value.\n\nliteral values 2"],
       :another_list ["[1 2 3 4]"]}))))
