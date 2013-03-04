# java-gem [![Build Status](https://secure.travis-ci.org/bruceadams/java-gem.png)](http://travis-ci.org/bruceadams/java-gem)

A command line tool for creating a Ruby Gem from a Java package
including all of its dependencies.

Required command line arguments:
* -g, --group       Maven group identifier of Java library
* -n, --name        Name of Java library
* -v, --version     Version of Java library
* -u, --uber-gem    Create a single gem containing all of the Java dependencies. (Building skinny gems is not yet implemented.)

Optional command line arguments:
* -h, --help        Show help
* -o, --output      Directory to write the Ruby Gem into (defaults to current working directory)
* -r, --repository  Extra Maven repository URL to read from

Example run:

    $ lein run --uber-gem --group org.apache.poi --name poi-ooxml --version 3.8

Alternate run mechanism:

* Compile a standalone Java jar:

        $ lein uberjar

* Run the executable jar:

        $ java -jar target/java-gem-1.0.0-SNAPSHOT-standalone.jar --uber-gem --group org.apache.poi --name poi-ooxml --version 3.8

## Now

Right now, this tool is a working proof of concept. The code needs to
be cleaned up for legibility, with tests and error handling.

## Future

Instead of generating a uber-gem, as it does now, generate a gem for
each individual Java library with gem dependencies that match the Java
dependencies.

Enhance this tool so it can run as a Ruby Gems server which will
create gems on request. It could act as a Ruby Gem front-end server
for a Maven server.

## License

Copyright © 2012, 2013 Bruce Adams. Distributed under the
[Eclipse Public License](http://www.eclipse.org/legal/epl-v10.html).
