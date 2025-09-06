#!/usr/bin/env bash

gsed -i.bak "/^(def ^:private version/c\
(def ^:private version \"$1\")" src/konpy/views.clj

