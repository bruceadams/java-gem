(ns java-gem.core-test
  (:use clojure.test
        java-gem.core))

(deftest gemify-version-test
  (is (= "simpletext" (gemify-version "simpletext")))
  (is (= "1.2.3.dotted" (gemify-version "1.2.3.dotted")))
  (is (= "1.2.3.dashed" (gemify-version "1.2.3-dashed"))))

(deftest parse-args-test
  (is (= {:group "group", :name "name",
          :output ".", :repository nil, :version "1.0"}
         (parse-args ["-g" "group" "-n" "name" "-v" "1.0"]))
      "Happy path"))

(deftest lein-project-test
  (is (= {:name "name", :version "1.0", :dependencies [['group/name "1.0"]]}
         (lein-project {:group "group", :name "name",
                        :output ".", :repository nil, :version "1.0"}))
      "Happy path"))

(deftest gemspec-str-test
  (is (re-find #"Gem::Specification\.new do |s|
  s\.date = \"[-0-9]*\"
  s\.require_paths = \[\"lib\"]
  s\.name = \"name\"
  s\.version = \"1\.0\"
  s\.homepage = \"https://github\.com/bruceadams/java-gem\"
  s\.files = \[\"\"]
  s\.authors = \[\"unknown\"]
  s\.summary = \"RubyGem wrapper for Java package name\"
  s\.description = \"Autogenerated RubyGem wrapper for group/name-1\.0\"
  s\.platform = \"java\"
end
"
         (gemspec-str {:group "group", :name "name",
                       :output ".", :repository nil, :version "1.0"}))
      "Not very interesting."))
