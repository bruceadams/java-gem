(defproject java-gem "1.0.0-SNAPSHOT"
  :description "A tool for creating a Ruby Gem from a Java package and all of its dependencies."
  :url "https://github.com/bruceadams/java-gem"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [org.clojure/tools.cli "0.2.1"]
                 [fs "1.3.2"]
                 [org.jruby/jruby-complete "1.7.2"]
                 [com.cemerick/pomegranate "0.0.13"]]
  :main java-gem.core)
