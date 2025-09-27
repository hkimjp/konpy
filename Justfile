set dotenv-load

help:
  just --list

CSS := "resources/public/assets/css"

watch:
  tailwindcss -i {{CSS}}/input.css -o {{CSS}}/output.css --watch=always

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

plus:
  clj -X:dev:plus

nrepl:
  clojure -M:dev:nrepl

dev:
  just watch &
  just nrepl

# under construction
container-repl:
  # clojure -X:dev clojure+.core.server/start-server
  docker compose up

run:
  clojure -M:run-m

kaocha:
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
