$LOAD_PATH << File.expand_path( File.dirname(__FILE__) + '/../lib' )

require 'test/unit'
require 'java-gem'

java_import java.lang.RuntimeException

class JavaGem_Test < Test::Unit::TestCase

  def test_happy_file_list
    java_gem = JavaGem.new
    file_list = java_gem.list([['org.apache.mina/mina-filter-ssl', '1.0.2']])
    jar_names = file_list.map {|file| file.to_path.to_s[/[^\/]*$/]}
    assert_equal(['backport-util-concurrent-2.2.jar',
                  'slf4j-api-1.2.jar',
                  'mina-core-1.0.2.jar',
                  'mina-filter-ssl-1.0.2.jar'],
                 jar_names)
  end

  def test_not_nested
    java_gem = JavaGem.new
    assert_raise(RuntimeException) do
      file_list = java_gem.list(['org.apache.mina/mina-filter-ssl', '1.0.2'])
    end
  end

  def test_missing_version
    java_gem = JavaGem.new
    assert_raise(RuntimeException) do
      file_list = java_gem.list([['org.apache.mina/mina-filter-ssl']])
    end
  end

end
