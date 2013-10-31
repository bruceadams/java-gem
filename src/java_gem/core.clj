(ns java-gem.core
  (:gen-class)
  (:require [cemerick.pomegranate.aether :as aether]
            [fs.core :as fs]
            [clojure.tools.cli :as cli] ; missing dependencies
            [clojure.java.io :as io]
            [clojure.string :as string]))

(import '(org.jruby.embed ScriptingContainer LocalContextScope))
(def c (ScriptingContainer. LocalContextScope/THREADSAFE))

(defn parse-args
  "Parse command line arguments"
  [raw-args]
  (let [[options args banner]
        (cli/cli raw-args
                 ["-h" "--help" "Show help" :default false :flag true]
                 ["-g" "--group" "Maven group identifier of Java library"]
                 ["-n" "--name" "Name of Java library (Maven artifact name)"]
                 ["-o" "--output" "Directory to write the Ruby Gem into"
                  :default "."]
                 ["-r" "--repository" "Extra Maven repository URL to read from"]
                 ["-u" "--uber-gem" (str "Create a single gem containing"
                                         " all of the Java dependencies")
                  :default false :flag true]
                 ["-v" "--version" "Version of Java library"])]
    (when (:help options)
      (println banner)
      (System/exit 0))
    options))

(defn copy-files
  [files target]
  (doseq [f files]
    (io/copy f (io/file target (.getName f)))))

(defn today
  "Today's date, formatted"
  []
  (.format (java.text.SimpleDateFormat. "yyyy-MM-dd") (java.util.Date.)))

;;; FIXME: this should use hard quotes (apostrophes) for Ruby strings
(defn ruby-const
  "Return a Ruby formatted constant.
Any collection or sequence becomes a Ruby array."
  [x]
  (cond (coll? x) (str "[" (string/join ", " (map ruby-const x)) "]")
        (symbol? x) (pr-str (str x))
        :else (pr-str x)))

(defn gemify-version
  "Modify a Maven legal version string into a Ruby Gem legal version
 string. Only digits, periods and lowercase letters."
  [version]
  (string/replace (string/lower-case version) #"[^0-9.a-z]" "."))

(defn gemspec-str
  "Return a Gem::Specification block of Ruby code."
  [{:keys [group name version output]}]
  (let [data {:name          (str group "," name)
              :version       (gemify-version version)
              :authors       ["unknown"]
              :date          (today)
              :platform      "java"
              :summary       (str "RubyGem wrapper for Java package " name)
              :description   (str "Autogenerated RubyGem wrapper for "
                                  group "/" name "-" version)
              :homepage      "https://github.com/bruceadams/java-gem"
              :files         (for [f (.listFiles (io/file output "lib"))]
                               (str "lib/" (.getName f)))
              :require_paths ["lib"]}]
    (format "Gem::Specification.new do |s|\n%send\n"
            (apply str (for [i data] (format "  s.%s = %s\n"
                                             (.getName (first i))
                                             (ruby-const (last i))))))))

(defn ruby-require
  "Ruby code to require each file"
  [files]
  (string/join "" (map #(str "require '" (.getName %) "'\n") files)))

(defn file-for
  "Get the file path for one item"
  [item dependencies]
  (->> item (find dependencies) first meta :file))

(defn build-lib
  ""
  [{:keys [name output]} jars]
  (let [libdir (io/file output "lib")
        rb (io/file libdir (str name ".rb"))]
    (fs/delete-dir libdir)
    (.mkdir libdir)
    (with-open [f (io/writer rb)]
      (.write f (ruby-require jars)))
    (copy-files jars libdir)))

(defn build-gem
  ""
  [{:keys [name output] :as options} others]
  (let [gemspec-file (io/file output (str name ".gemspec"))]
    (with-open [f (io/writer gemspec-file)]
      (.write f (gemspec-str options)))
    (. c runScriptlet
       (str "require 'rubygems';"
            "require 'rubygems/gem_runner';"
            "Dir.chdir '" output "';"
            "Gem::GemRunner.new.run ['build', '" gemspec-file "']"))))

(defn uber-gem-resolution
  ""
  [dependencies]
  (let [jars (aether/dependency-files dependencies)]
    [jars []]))

(defn skinny-gem-resolution
  ""
  [dependencies coordinates]
  (let [this-item (first coordinates)
        jar (file-for this-item  dependencies)
        others (dissoc dependencies this-item)]
    (println "Gem does not specific its dependencies!")
    (println others)
    [[jar] others]))

(defn -main
  "Generate a Ruby Gem."
  [& raw-args]
  (let [{:keys [name group repository output version uber-gem] :as options}
          (parse-args raw-args)
        coordinates [[(symbol (str group "/" name))
                      version]]
        repos (if repository {"central" "http://repo1.maven.org/maven2/"
                              "name" repository}
                {"central" "http://repo1.maven.org/maven2/"})
        dependencies (aether/resolve-dependencies
                      :coordinates coordinates
                      :repositories repos)
        [jars others] (if uber-gem
                        (uber-gem-resolution   dependencies)
                        (skinny-gem-resolution dependencies coordinates))]
    (build-lib options jars)
    (build-gem options others)))
