(ns java-gem.core-test
  (:use clojure.test
        java-gem.core))

(deftest gemify-version-test
  (is (= "simpletext" (gemify-version "simpletext")))
  (is (= "1.2.3.dotted" (gemify-version "1.2.3.dotted")))
  (is (= "1.2.3.dashed" (gemify-version "1.2.3-dashed"))))
