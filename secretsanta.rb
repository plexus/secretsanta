require 'sequel'
require 'sinatra'
require 'yaks'
require 'yaks-html'

DB   = Sequel.connect('sqlite://secretsanta.db')
YAKS = Yaks.new

def create_tables
  DB.create_table(:groups) do
    primary_key :id
  end

  DB.create_table(:elves) do
    primary_key :id
    foreign_key :group_id, :groups
    String :name
    String :email
    String :url_key
    foreign_key :recipient_id, :elves
    String :wishlist
  end
end

def create_some_elves
  group = Group.create
  %w( John Paul George Ringo ).each do |elf|
    Elf.create(group_id: group.id)
  end
end

class Group < Sequel::Model
  one_to_many :elves
end

class Elf < Sequel::Model
  many_to_one :group
end

class GroupMapper < Yaks::Mapper
  attributes :id

  has_many :elves
end

class ElfMapper < Yaks::Mapper
  attributes :name, :wishlist
end

get '/' do

end

get '/groups/:id' do
  YAKS.call(Group[params[:id]], env: env)
end

post '/groups' do
end
