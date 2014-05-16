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

Then to boot up a web server, you can do:

```clojure
(require '[viztrello.web :as web])
(def server (web/-main))
```

To stop the server, you can do:

```clojure
(.stop server)
```

## Deploying

## License

Copyright © 2014 Bo Jeanes

No license chosen yet.
