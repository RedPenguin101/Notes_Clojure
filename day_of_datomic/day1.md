# Agenda

* what is datomic?
* informational model
* transactional model - how data gets into datomic - ACID
* query model - datalog (logic based) and datomic-pull (navigational model)
* time model - distinctive feature, accumulate only database
* operational model - datomic clients (new as of 2016)

# What is datomic

* rethink of databases
* agile - making things so that you don't paint yourself into a corner
* robust - we will make mistakes, how do we handle things when we make mistakes? Append only means you can rollback.
* powerful - having the difficultly of the work scaled to the difficultly of the problem. Easy things should be easy, hard things should be hard but not harder
* time aware - remember the past. Obviously
* cloud first - lift and shift: on-prem doesn't translate exactly.

# Agile: universal schema

e/a/v/tx/op(t/f)

never define a relation, there is one relation, the 5tuple

eav's are triplestores.

Most domains: things don't always show up in a certain shape. EAV allows you to avoid this, schema is just relation

Update-in-place, mutability. Bad, makes things difficult to reason about. Have to co-ordinate changes to something. Cache expiry concerns.

NoSQL are still update in place (also not cloud first)

Co-ordination is complicating. 

# time aware

start from a database value: value at a specific time.

Can ask for the value `as-of` a certain time, or `since`.

hypothetical `with` proposed additions. Can get pretty sophisticated with this.

`tx-report` live report of new things, as well as before/after values of database.

# Operational model, briefly

Trad DB: DB does everything about being a DB. means it's challenging to make decisions about individual pieces.

Datomic: Services. A transactor service. Doesn't do query or storage. Though it does do indexing.

Storage services: bunch of choices for this.

Peer services do queries. just as close to storage as the transactors. You can have a local peer library (in JVM) to run queries in memory, and a remote peer that client (non-jvm) can connect to it.

immutability means memcache becomes much simpler. Almost no setup overhead. Can have different memcaches for different usecase (operational and analysis)

# Informational Model

* notation (edn)
* datoms (5tuples)
* database (as a set of datoms that only grows)
* entities (a fiction, that if you have a bunch of datoms about the same thing, you can think about them as a single map or object)
  * Note that with datoms the translation is mechinical. With SQL you have to use ORM
* schema - different from rel db

## EDN

Just JSON !?!? We tried to make JSON work, it doesn't work. Would be much better if it did! But JSON sucks, sadly

Why not suitable? missing types. not great at telling number types apart. Doesn't have things which represent the names of things (symbols, keywords). can only use strings as keys in KV pairs.

But EDN doesn't try to cover every data literal. EDN has datatype extensibility for this `#name edn-form`, where name is the interpretation ('GPS coordinates'). It's recursively defined. your program can comprehend it. Other programs don't have to, can still serialise, deserialise, send it on. e.g. `#inst`, `#uuid`.

## Datoms

eav/tx/op(t/f)

jane | likes | pizza    | 1008 | true
jane | likes | brocolli | 1008 | true
jane | likes | pizza    | 1148 | false

Literally

1001 | 63 | jane     | 1008 | true    ; thing with name (attr 63) Jane (id 1001)
1002 | 63 | brocolli | 1008 | true    ; thing with name brocolli (id 1002)
1003 | 63 | pizza    | 1008 | true    ; thing with name pizza (id 1003)
1001 | 64 | 1002     | 1008 | true    ; id 1001 (Jane) likes (attr 64) id 1002 (brocolli)
1001 | 64 | 1003     | 1008 | true    ; id 1001 (Jane) likes (attr 64) id 1003 (pizza)
1001 | 64 | 1003     | 1148 | false    ; id 1001 (Jane) likes (attr 64) id 1003 (pizza) FALSE

* entities are stored as a number
* attributes are stored as a number
* values, if they are a reference between two entities, are stored as a number

## Database

* set of datoms (universal relation)
* efficient storage - compact representation and compression on top of that.
* many sort orders - redundently stored (the indexes)
* accumulate only (not append only, technically)

### indexes

* kv (AVET sort order)
* row (EAVT)
* column (AEVT)
* document (EAVT, components)
* graph (VAET)

Don't have to choose a database based on what questions you want to ask. Don't need to manually store your data multiple ways to optimise queries like mongo or cassandra recommend.

## Entities

* immutable
* associative - presumed identity, kv pairs that describe that identity. Like objects
* entity can be inferred from datoms sharing a common eid
  * caveat: entities by nature are map-like, kv pairs. need to smoosh down 5tuple to 2tuples. You get e for free, but you lose tx/op, because entities are point in time.
  * entities have bi-directional navigation. I can see the attributes of an entity e, but I can also see what entities are related to me. What foods does Jane like? AND Who likes pizza?

## Schema
An information system has a schema, whether or not the tools reify it. It can be implicit, but it's there.

* adds power to system. Datomic schema doesn't define relations, but does say something about attributes
  * give it an ident (first name)
  * value type. names have to be string
  * cardinality: user has only one primary email address (but be CAREFUL about locking yourself in). Datomic will automatically retract the old value if set to one rather than many
  * unique: 
  * component: stops you orphaning things
  * YOU CAN CHANGE ANY OF THESE (except value type)
* schema is data
* scheme intantiated by transactions
* make history compatible changes at any time
