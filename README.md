# java-gem

A command line tool for creating a Ruby Gem from a Java package
including all of its dependencies.

Command line arguments:
 -group    a Maven group identifier
 -name     a Maven artifact identifier
 -version  a Maven version string

Example run:

lein run -group org.apache.poi -name poi-ooxml -version 3.8

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

Copyright © 2012 Bruce Adams
Distributed under the
[Eclipse Public License](http://www.eclipse.org/legal/epl-v10.html).
