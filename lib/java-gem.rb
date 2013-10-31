require 'java-gem/jars'

class JavaGem

  def initialize(repositories = ["http://repo1.maven.org/maven2/"])
    @repositories = repositories
  end

  RT = Java::clojure::lang::RT

  def file_list(coordinates)
    clojure = "
(do (require '[cemerick.pomegranate.aether :as aether])
    (let [coords (map (fn [[n v]] [(symbol n) v]) #{coordinates})
          repos  (zipmap #{@repositories} #{@repositories})]
      (vec (aether/dependency-files
             (aether/resolve-dependencies
               :coordinates coords
               :repositories repos)))))"

    RT.var("clojure.core", "eval").
      invoke(RT.var("clojure.core", "read-string").
             invoke(clojure))
  end

end
