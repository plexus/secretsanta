require 'sequel'
require 'sinatra'
require 'yaks'
require 'yaks-sinatra'
require 'yaks-html'

DB   = Sequel.connect('sqlite://secretsanta.db')

configure_yaks do
  # Make urls absolute, will be pulled into Yaks
  after :map do |resource, env|
    resource.controls(
      resource.controls.map do |control|
        base_url = URI("#{env['rack.url_scheme']}://#{env['HTTP_HOST']}")
        uri = URI(control.href)
        control.href(URI.join(base_url, uri))
      end
    )
  end
end

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

  control :create do
    method 'POST'
    href '/groups'
    media_type 'application/x-www-form-urlencoded'
    field :name_1, label: 'Name 1', type: 'text'
    field :email_1, label: 'Email 1', type: 'text'
    field :name_2, label: 'Name 2', type: 'text'
    field :email_2, label: 'Email 2', type: 'text'
    field :name_3, label: 'Name 3', type: 'text'
    field :email_3, label: 'Email 3', type: 'text'
  end
end

class ElfMapper < Yaks::Mapper
  attributes :name, :wishlist
end

before do
  headers 'Access-Control-Allow-Origin'  => '*'
  headers 'Access-Control-Allow-Headers' => 'Authorization,Accepts,Content-Type,X-CSRF-Token,X-Requested-With'
  headers 'Access-Control-Allow-Methods' => 'GET,POST,PUT,DELETE,OPTIONS'
end

get '/' do
end

get '/groups/:id' do
  yaks Group[params[:id]]
end

post '/groups' do
end
