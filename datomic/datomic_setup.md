# How to set up Datomic

from https://docs.datomic.com/on-prem/getting-started/brief-overview.html

## Overview

A cloud first, ACID transactional, immutable database

Not designed to be a DWH. It's a good fit for operational systems.

It is composed of separate services: storage, transactor, peers, clients, console (web server).

### Peer model and Client model

Peer library is embedded in your code, making queries happen in-memory and so faster. Clients talk to peer servers (intermediaries). Much lighter weight at cost of increased read time

## Get Datomic

Login to https://my.datomic.com/account (or set up an account) and download Datomic (Pro) Starter Edition https://www.datomic.com/get-datomic.html

unzip the file to somewhere

## Connect to a database

launch a peer server telling it port, access key and secret, URL

`bin/run -m datomic.peer-server -h localhost -p 8998 -a myaccesskey,mysecret -d hello,datomic:mem://hello`

(note this creates an in-mem database)

Download the client by adding `[com.datomic/client-pro "0.9.41"]` to your deps map

run the repl

`(require '[datomic.client.api :as d])`

set the config as 

```
(def cfg {:server-type :peer-server
          :access-key "myaccesskey"
          :secret "mysecret"
          :endpoint "localhost:8998"
          :validate-hostnames false})

(def client (d/client cfg))

(def conn (d/connect client {:db-name "hello"}))
```

You can inspect the resulting connection from the repl with `conn`

`{:db-name "hello", :database-id "5e49fae3-2712-442e-96d1-eca0dd3a3faa", :t 66, :next-t 1000, :type :datomic.client/conn}`

## Transact Schema

The schema is just data. Just issue a transaction that carries the new schema defintion.

Our demo will be for a movie, which has a title, release year, and genre.

Each of these is an attribute associated with the movie entity

* `:db/ident` specifies the name for the attribute
* `:db/valueType`
* `:db/cardinality` is whether the attribute stores a single value or a collection
* `:db/doc` stores a docstring, which is queryable

```
{:db/ident :movie/title
 :db/valueType :db.type/string
 :db/cardinality :db.cardinality/one
 :db/doc "The title of the movie"}
```

the others would be the same except that release-year would have type/long

in repl:
```
(def movie-schema [{:db/ident :...}
                   {...}
                   {...}

```

Now we transact. A transact gets passed the connection and the map of data

`(d/transact conn {:tx-data movie-schema})`

You'll get back a before and after map.

## Transact data

The same as schema to add movies to DB

`(d/transact conn {:tx-data first-movies})`

By doing ths you created 3 entities, each of which has a uid and collection of associated attributes.

## Query data

2 mechanisms
* query with datalog
* pull - make hierarchical selections

pull not looked at here

to query, you have to get the database value

`(def db (d/db conn))`

The query api takes a query and a map of arguments

`(d/q '[:find ?e :where [?e :movie/title]] db)`

More stuff about datalog, not in scope.

## See historic data

you want to update the genre of commando to future govenor. Do so like this

```
(def commando-id
  (ffirst (d/q '[:find ...] db)))

(d/transact conn {:tx-data [{:db/id commando-id :movie/genre "future governor"}]})
```

Note that if you query straight away you'll get the old value. That's because you still have an old value for the database. you need to get a new one first

To see what the database looked like at a point in time, you can

`(def old-db (d/as-of db 1004))`

Using a tx, a t value or a date. You can also do `d/since`,

`(def hdb (d/history db))` will get you the history, which will let you see the changes in values over time

```
(d/q '[:find ?genre
       :where [?e :movie/title "Commando"]
              [?e :movie/genre ?genre]]
      hdb)
```


