(ns java-gem.core
  (:gen-class)
  (:require [leiningen.core.classpath :as classpath]
            [fs.core :as fs]
            [clojure.tools.cli :as cli] ; missing dependencies
            [clojure.java.io :as io]))

(import '(org.jruby.embed ScriptingContainer LocalContextScope))
(def c (ScriptingContainer. LocalContextScope/THREADSAFE))

(defn parse-args
  "Parse command line arguments into a Leiningen project structure"
  [raw-args]
  (let [[options args banner]
        (cli/cli raw-args
                 ["-g" "-group" "Maven group identifier of Java library"]
                 ["-n" "-name" "Name of Java library"]
                 ["-o" "-output" "Directory to write the Ruby Gem into"
                  :default "."]
                 ["-r" "-repository" "Extra Maven repository URL to read from"]
                 ["-v" "-version" "Version of Java library"])]
    options))

(defn lein-project
  "Return a Leiningen project structure based on the user options"
  [{:keys [group name repository version]}]
  (let [base {:name name
              :version version
              :dependencies [[(symbol (str group "/" name)) version]]}]
    (if repository
      (conj base {:repositories {"extra-repo" {:url repository}}})
      base)))

(defn copy-files
  [files target]
  (doseq [f files]
    (io/copy f (io/file (str target (.getName f))))))

(defn today
  "Today's date, formatted"
  []
  (.format (java.text.SimpleDateFormat. "yyyy-MM-dd") (java.util.Date.)))

(defn ruby-const
  "Return a Ruby formatted constant."
  [x]
  (if (seq? x)
    (str "[\"" (clojure.string/join "\", \"" x) "\"]")
    (pr-str x)))

(defn gemify-version
  "Modify a Maven legal version string into a Ruby Gem legal version string."
  [version]
  (clojure.string/replace version "-" "."))

(defn gemspec-str
  "Return a Gem::Specification block of Ruby code."
  [{:keys [group name version output]}]
  (let [data {:name        name
              :version     (gemify-version version)
              :authors     ["unknown"]
              :date        (today)
              :platform    "java"
              :summary     (str "RubyGem wrapper for Java package " name)
              :description (str "Autogenerated RubyGem wrapper for "
                                group "/" name "-" version)
              :homepage    "https://github.com/bruceadams/java-gem"
              :files       (for [f (.listFiles (io/file (str output "/lib")))]
                             (str "lib/" (.getName f)))
              :require_paths ["lib"]}]
    (str "Gem::Specification.new do |s|\n"
         (apply str (for [i data]
                      (str "  s."
                           (.getName (first i))
                           " = "
                           (ruby-const (last i))
                           "\n")))
         "end\n")))

(def require-all "Dir[File.expand_path('*.jar', File.dirname(__FILE__))].each do |file|
  require File.basename(file)
end
")

(defn -main
  "Generate a Ruby Gem."
  [& raw-args]
  (let [options (parse-args raw-args)
        name (:name options)
        gemspec-file (str (:output options) "/" name ".gemspec")
        libdir (str (:output options) "/lib")
        project (lein-project options)]
    (let [jars (classpath/resolve-dependencies :dependencies project)]
      (fs/delete-dir libdir)
      (.mkdir (io/file libdir))
      (with-open [f (io/writer (io/file (io/file (str libdir "/" name ".rb"))))]
        (.write f require-all))
      (copy-files jars (str libdir "/"))
      (with-open [f (io/writer (io/file gemspec-file))]
        (.write f (gemspec-str options)))
      (. c runScriptlet
         (str "require 'rubygems';"
              "require 'rubygems/gem_runner';"
              "Dir.chdir '" (:output options) "';"
              "Gem::GemRunner.new.run ['build', '" gemspec-file "']")))))
