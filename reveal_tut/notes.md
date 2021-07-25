# Notes on getting reveal working

`:deps {vlaaad/reveal {:mvn/version "1.2.186"}}`

```clojure
{:aliases {:reveal {:extra-deps {vlaaad/reveal {:mvn/version "1.2.186"}}
                    :ns-default vlaaad.reveal
                    :exec-fn repl}}}
```
