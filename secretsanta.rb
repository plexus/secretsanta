require 'sequel'

DB = Sequel.connect('sqlite://secretsanta.db')


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
    String :whishlist
  end
end
