# VizTrello

Traverses Trello cards and the links between them to create a directed graph of
relationships between cards.

## Prerequisites

You will need [Leiningen][1] 2 or above installed.

[1]: https://github.com/technomancy/leiningen

## Local Setup

```shell
cp .lein-env.sample .lein-env
```

Edit `.lein-env` and supply values, accordingly.

## Running

Everything is done through the REPL. Start one with:

```shell
$ lein repl
```

Then to control the system, you can do:

```clojure
(go) ; initiaze and start
(stop) ; stops
(start) ; start again
(reset) ; stops, re-inits, re-starts
(refresh) ; if you break things (e.g. syntax error) you may need to run this
          ; to reload everything in order to be able to (reset) again.
```

(If you *really* want to just boot a the system locally without a REPL, you can
do `lein run -m viztrello`.)

## Testing

From a REPL

```clojure
(test/run-all-tests)
```

In dev mode, most things will reload automatically, so you should only need to `(reset)` when
you need to re-initialize something (e.g. database connections).

## Deploying

```shell
git push git@heroku.com:viztrello.git master
```

## License

Copyright © 2014 Bo Jeanes

No license chosen yet.
