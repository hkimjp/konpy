set dotenv-load

# shows available recipies
help:
  just --list

nrepl: dev
dev:
  clojure -M:dev -m nrepl.cmdline

container-repl: dev-container
dev-container:
  clojure -M:dev -m nrepl.cmdline -b 0.0.0.0 -p 7777

CSS := "resources/public/css"
tailwind:
  tailwindcss -i {{CSS}}/input.css -o {{CSS}}/output.css --watch

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
  clojure -T:build ci

deploy:
  just deploy-to 'ubuntu@app.melt.kyutech.ac.jp' konpy

#-------------------
# FIXME: incomplete.
#-------------------
deploy-to host app: build
  ssh {{host}} mkdir -p {{app}}/log
  scp target/io.github.hkimjp/{{app}}-*.jar {{host}}:konpy/konpy.jar
  rsync -av systemd {{host}}:konpy/
  ssh {{host}} sudo cp konpy/systemd/konpy.service /lib/systemd/system
  ssh {{host}} sudo systemctl daemon-reload
  ssh {{host}} sudo systemctl restart konpy
  ssh {{host}} systemctl status konpy

clean:
  rm -rf target
