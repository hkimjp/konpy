# hkimjp/konpy

This week's assignments for 2025 introduction to programming python class.

Under construction.

## Installation

Download from https://github.com/hkimjp/konpy

## Require

I'm using,

* java  24.0.1
* Clojure 1.12.1
* Redis redis:7(Orbstack, docker)
* Postgresql@17

## Usage

Postgresql and Redis are required.

In development, run tailwindcss watcher in the background,

    $ just watch

then start Clojure REPL.

    $ just repl

Persistent storage is at `storage/db.sqlite`.

In production,

    $ just run

or,

    $ just build
    $ java -jar target/io.github.hkimjp/konpy-<VERSION>.jar


## License

Copyright © 2025 Hkim

Distributed under the Eclipse Public License version 1.0.
