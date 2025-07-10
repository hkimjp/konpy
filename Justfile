set dotenv-load

help:
  just --list

CSS := "resources/public/assets/css"
watch:
  tailwindcss -i {{CSS}}/input.css -o {{CSS}}/output.css --watch

minify:
  tailwindcss -i {{CSS}}/input.css -o {{CSS}}/output.css --minify

fetch:
  @echo redis must stop before fetch, or locally over written before long.
  scp ${DEST}:konpy/storage/db.sqlite storage/
  scp ${DEST}:konpy/log/konpy.log log/
  # brew redis is 8.*.
  scp ${DEST}:/var/lib/redis/dump.rdb /opt/homebrew/var/db/redis/
  # if use orbstack redis@7, choose here.
  scp ${DEST}:/var/lib/redis/dump.rdb ~/docker/redis@7/data/

repl:
  clojure -M:dev -m nrepl.cmdline

container-repl:
  clojure -M:dev -m nrepl.cmdline -b 0.0.0.0 -p 7777

run:
  clojure -M:run-m

format_check:
  clojure -M:format -m cljfmt.main check src dev test

format:
  clojure -M:format -m cljfmt.main fix src dev test

lint:
  clojure -M:lint -m clj-kondo.main --lint .

test:
    clojure -M:dev -m kaocha.runner

build:
  just minify
  clojure -T:build ci

deploy:
  just deploy-to ${DEST} konpy

deploy-to host app: build
  ssh {{host}} mkdir -p {{app}}/log
  ssh {{host}} mkdir -p {{app}}/storage
  scp target/io.github.hkimjp/{{app}}-*.jar {{host}}:{{app}}/{{app}}.jar
  # scp systemd/{konpy.service,start.sh,stop.sh,konpy.env} {{host}}:{{app}}/
  # ssh {{host}} sudo cp {{app}}/konpy.env /etc/default/
  # ssh {{host}} sudo cp {{app}}/{{app}}.service /lib/systemd/system
  # ssh {{host}} sudo systemctl daemon-reload
  ssh {{host}} sudo systemctl restart {{app}}.service
  ssh {{host}} systemctl status {{app}}.service

clean:
  rm -rf target
