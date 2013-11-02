require 'java-gem/jars'

class JavaGem

  def initialize(repositories = ["http://repo1.maven.org/maven2/"])
    @repositories = repositories
    @file_lists = {}
  end

  # For Clojure 1.6, replace the following with: API = Java::clojure::api::API
  API = Java::clojure::lang::RT

  def file_list(coordinates)
    @file_lists[coordinates] ||= file_list_once(coordinates)
  end

  def requirable(coordinates, name)
    rb = name + '.rb'
    File.rm(rb)
    File.rmdir(name)
    File.mkdir(name)
    files = file_list(coordinates)
    files.each {|file| FileUtils.copy(file, name)}
    open(rb, 'w') do |f|
      files.each do |file|
        filename = FileUtils.basename(file.to_path.to_s)
        f.write("require '@{filename}'\n")
      end
    end
  end

  private

  def file_list_once(coordinates)
    clojure = "
(do (require '[cemerick.pomegranate.aether :as aether])
    (let [coords (map (fn [[n v]] [(symbol n) v]) #{coordinates})
          repos  (zipmap #{@repositories} #{@repositories})]
      (vec (aether/dependency-files
             (aether/resolve-dependencies
               :coordinates coords
               :repositories repos)))))"

    read_string = API.var("clojure.core", "read-string")
    eval = API.var("clojure.core", "eval")
    code = read_string.invoke(clojure)
    eval.invoke(code)
  end
end
