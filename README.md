# java-gem [![Build Status](https://secure.travis-ci.org/bruceadams/java-gem.png)](http://travis-ci.org/bruceadams/java-gem)

A command line tool for creating a Ruby Gem from a Java package
including all of its dependencies.

Required command line arguments:
* -g, --group       Maven group identifier of Java library
* -n, --name        Name of Java library
* -v, --version     Version of Java library

Optional command line arguments:
* -h, --help        Show help
* -o, --output      Directory to write the Ruby Gem into (defaults to current working directory)
* -r, --repository  Extra Maven repository URL to read from

Example run:

    $ java-gem --group org.apache.poi --name poi-ooxml --version 3.8

The output is two files:
* `org.apache.poi,poi-ooxml-3.8-java.gem`
* `poi-ooxml.gemspec`

        Gem::Specification.new do |s|
          s.date = "2013-03-04"
          s.require_paths = ["lib"]
          s.name = "org.apache.poi,poi-ooxml"
          s.version = "3.8"
          s.homepage = "https://github.com/bruceadams/java-gem"
          s.files = ["lib/stax-api-1.0.1.jar",
                     "lib/poi-ooxml-schemas-3.8.jar",
                     "lib/xmlbeans-2.3.0.jar",
                     "lib/xml-apis-1.0.b2.jar",
                     "lib/dom4j-1.6.1.jar",
                     "lib/poi-3.8.jar",
                     "lib/commons-codec-1.5.jar",
                     "lib/poi-ooxml-3.8.jar",
                     "lib/poi-ooxml.rb"]
          s.authors = ["unknown"]
          s.summary = "RubyGem wrapper for Java package poi-ooxml"
          s.description = "Autogenerated RubyGem wrapper for org.apache.poi/poi-ooxml-3.8"
          s.platform = "java"
        end

and a `lib` directory containing the Java `jar` files and a generated
Ruby file that does a `require` of each `jar` file, `lib/poi-ooxml.rb`

    require 'xml-apis-1.0.b2.jar'
    require 'dom4j-1.6.1.jar'
    require 'stax-api-1.0.1.jar'
    require 'xmlbeans-2.3.0.jar'
    require 'poi-ooxml-schemas-3.8.jar'
    require 'commons-codec-1.5.jar'
    require 'poi-3.8.jar'
    require 'poi-ooxml-3.8.jar'

## License

Copyright © 2012, 2013 Bruce Adams. Distributed under the
[Eclipse Public License](http://www.eclipse.org/legal/epl-v10.html),
see [LICENSE](LICENSE) for details.
