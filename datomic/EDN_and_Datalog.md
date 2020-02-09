# EDN, Datalog, Datomic

From learndatalogtoday.org

## EDN
Like Json, but with clojure data

Numbers, Strings, kw, symbols, vectors, lists, instants

A datalog query:

```clj
[:find ?title
 :where
 [_ :movie/title ?title]]
```

## Queries
Datomic has atomic facts called __datoms__ - a 4-tuple of entity-id, attribute-name (e.g. :movie/title), value, transaction-if (like a timestamp)

Think of them like a set of facts.

Represented as a vector `[:find ...]`, pattern variables,  `?...`, a `:where` clause which contains __data patterns__

```clj
[:find ?e
 : where
 [?e :person/name "Ridley Scott"]]
```

The data pattern here is the `[?e :person/name "Ridley Scott"]`. Notice it's a datom with some missing information. This has the entity id part of the datom missing, so the query will look for everything meeting which will fill the missing value.

You can wildcard with `_`

## Data patterns
```clj
[:find ?title
 :where
 [?e :movie/year 1987]
 [?e :movie/title ?title]]
```

This query, with 2 data patterns, will return the title of all movies released in 1987. Note that `?e` is in the first dp as a return value, then used as an input value in the second.

The order doesn't actually matter for the result set, though it does from a performance perspective (you want your most restrictive query first).

```clj
[:find ?name
 :where
 [?m :movie/title "Lethal Weapon"]
 [?m :movie/cast ?p]
 [?p :person/name ?name]]
```

returns the names of all people in the cast of the movie with the name "Lethal Weapon"

## Parameterized Queries
`:in` allows you to specify input parameters

```clj
[:find ?title
 :in $ ?name
 :where
 [?p :person/name ?name]
 [?m :movie/cast ?p]
 [?m :movie/title ?title]]
```

## More queries

## Predicates

## Transforms

## Aggregates

## Rules
