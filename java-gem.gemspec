# -*- encoding: utf-8 -*-
$:.push File.expand_path('../lib', __FILE__)
require 'java-gem/version'

git_files = `git ls-files`.split("\n")
jar_files = [] # FIXME: Dir.files['lib/java-gem/*.jar']

Gem::Specification.new do |s|
  s.name          = 'java-gem'
  s.version       = JavaGem::VERSION
  s.platform      = Gem::Platform::RUBY
  s.authors       = ['Bruce Adams']
  s.email         = ['bruce.adams@acm.org']
  s.homepage      = 'https://github.com/bruceadams/java-gem'
  s.summary       = %q{la de da}
  s.description   = %q{la de da}
  s.files         = git_files + jar_files
  s.test_files    = git_files.select {|n| n =~ /^test\//}
  s.executables   = (git_files.select {|n| n =~ /^bin\//}).map{ |f| File.basename(f) }
  s.require_paths = ['lib']
end
