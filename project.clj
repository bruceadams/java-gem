(defproject java-gem "1.0.0-SNAPSHOT"
  :description "A tool for creating a Ruby Gem from a Java package and all of its dependencies."
  :url "https://github.com/bruceadams/java-gem"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [org.clojure/tools.cli "0.2.1"]
                 [org.jruby/jruby-complete "1.6.7"]
                 [leiningen-core "2.0.0-preview6"]]
  :main java-gem.core)
