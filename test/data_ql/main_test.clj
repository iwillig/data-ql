(ns data-ql.main-test
  (:require [clojure.test :as t :refer [deftest is testing]]))

(deftest test-okay
  (testing "Given:"
    (is (true? false))))
