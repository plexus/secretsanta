To start an irb

```
bundle exec irb -I. -rsecretsanta
```

Then in irb you can

``` ruby
create_tables
```

This will create `secretsanta.db`

and then

``` ruby
create_some_elves
```

And you'll have Paul, John, Ringo, and George :) \o/

To start the app

```
bundle exec ruby secretsanta.rb
```
