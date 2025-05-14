# hkimjp/konpy

This week's assignments for 2025 introduction to programming python class.

Under construction.

## Installation

Download from https://github.com/hkimjp/konpy

Require Redis.

## Usage

In development, run tailwindcss watcher in the background,

    $ just watch

trhen start Clojure REPL.

    $ just repl

Redis is required to work.

will make persistent storage at `storage/db.sqlite`.

In production,

    $ just run

or, build uberjar by,

    $ just build

then,

    $ java -jar target/io.github.hkimjp/konpy-VERSION.jar


## License

Copyright © 2025 Hkim

Distributed under the Eclipse Public License version 1.0.
