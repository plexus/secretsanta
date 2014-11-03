require 'sequel'
require 'sinatra'
require 'yaks'

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

class Group < Sequel::Model
end

class Elf < Sequel::Model
end

class GroupMapper < Yaks::Mapper
  attributes :id

  has_many :elves
end

class ElfMapper < Yaks::Mapper
  attributes :name, :wishlist
end
