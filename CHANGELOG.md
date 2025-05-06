## Unreleased

- compile routes.
- (set! *default-data-reader-fn* tagged-literal)
- konpy.db wrapper?

## 0.6.0 (2025-05-05)

- updated libraries

| :file    | :name                       | :current | :latest |
|----------+-----------------------------+----------+---------|
| deps.edn | datascript/datascript       | 1.7.4    | 1.7.5   |
|          | io.github.tonsky/clj-reload | 0.9.4    | 0.9.5   |
|          | io.github.tonsky/clj-reload | 0.9.4    | 0.9.5   |
|          | io.github.tonsky/fast-edn   | 1.1.2    | 1.1.3   |
| pom.xml  | org.clojure/clojure         | 0.6.1    | 1.12.0  |


## 0.6.1 (2025-05-05)

- utils/sha1
- utils/remove-spaces

## 0.6.0 (2025-05-05)

- fixed a bug in `middleware/wrap-user`.

## 0.5.0 (2025-05-04)

- **BREAKING** removed '/' from endpoint url.
- start to `konpy.answers`
- returned content-type text/html from not-found.
- favicon.ico
- move `under-construction-page` from `utils` namespace to `views` namespace.
- **BREAKING** konpy.answers -> konpy.answer

## 0.4.5 (2025-05-04)

- (admin/puttask! ^long week ^long num ^String task)
- tasks sort-by :num
- admin sort-by (juxt :week :num)
- `seed-in` function defined in `dev/user.clj`.
- a little tailwind.

## 0.4.4 (2025-05-03)

- updated libraries.

| :file    | :name                         | :current  | :latest |
|--------- | ------------------------------| --------- | --------|
| deps.edn | io.github.tonsky/clojure-plus | 1.3.3     | 1.4.0   |
| deps.edn | com.taoensso/telemere         | 1.0.0-RC5 | 1.0.0   |
| pom.xml  | org.clojure/clojure           | 0.4.3     | 1.12.0  |

- added `utils/weeks`.
- removed `deadline` entity from database.
  showint it caliculated from `weeks` is enough.
- **BREAKING** renamed `/assignments` to `/tasks`.
- `java-time`.

## 0.4.3 (2025-05-03)

- **BUG** Cannot invoke "java.util.concurrent.Future.get()" because "fut" is null
- fixed - did not started datascript in production.

## [0.4.2] (2025-05-02)

- /logout
- prep kp.melt.kyutech.ac.jp, port 8505.
- successed deploy test to app.melt.
- /assignments/
- /admin/
- /admin/new

## 0.4.1 (2025-05-02)

- (def schema {:identical {:db/cardinality :db.cardinality/many}})
- added db/entity - how it works?
- gitignored `.env`.
- **BREAKING** resources/public/assets/css
- /admin/assignments/
- middleware namespace `kp.middleware`.
- success authentication against `l22`.
- display flash in /login.
- **BREAKING** `/` is the link to login. no /login route.

## [0.3.2] (2025-05-01)

- copy konpy.clj to hkimjp/datascript.clj
- /login
- /assignments
- /answers
- not found page, more precisely.

## 0.3.1 (2025-05-01)

- missing tag name?
- do not use this branch.

## 0.3.0 (2025-05-01)

- added (dummy) /assignments, /answers page.
- added konpy/db.clj

## [0.2.0] (2025-05-01)

- added `systemd/{konpy.service,start.sh,stop.sh}`.
- test success `just deploy-to ubuntu@eq.local konpy`.

## 0.1.1 (2025-04-30)

- gitignored resources/public/css/output.css.
- added src/konpy/middleware.clj, which is empty.
- anti-forgery-field

## 0.1.0 - 2025-04-30

- initialized repository.

[0.4.2]: https://github.com/hkimjp/konpy/compare/0.3.2...0.4.2
[0.3.2]: https://github.com/hkimjp/konpy/compare/0.2.0...0.3.2
[0.2.0]: https://github.com/hkimjp/konpy/compare/0.1.0...0.2.0
