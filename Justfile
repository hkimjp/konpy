set dotenv-load

help:
  just --list

CSS := "resources/public/assets/css"
watch:
  tailwindcss -i {{CSS}}/input.css -o {{CSS}}/output.css --watch

minify:
  tailwindcss -i {{CSS}}/input.css -o {{CSS}}/output.css --minify

fetch:
  scp ${DEST}:konpy/storage/db.sqlite storage/
  scp ${DEST}:konpy/log/konpy.log log/

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
  scp systemd/{konpy.service,start.sh,stop.sh} {{host}}:{{app}}/
#   scp systemd/konpy.env {{host}}:/etc/default/
#   ssh {{host}} sudo cp {{app}}/{{app}}.service /lib/systemd/system
#   ssh {{host}} sudo systemctl daemon-reload
  ssh {{host}} sudo systemctl restart {{app}}.service
  ssh {{host}} systemctl status {{app}}.service

clean:
  rm -rf target
