require 'rack'
require 'pp'
run ->(env) {
  puts "#{"="*20} #{Time.now} #{"="*20}"
  pp env
  [200, {}, [""]]
}
