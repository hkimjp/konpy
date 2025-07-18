## Unreleased

- datascript/restore issue
```
(set! *default-data-reader-fn* tagged-literal)
```
- let admin can check who logined, submitted.
- validate. how about checking :type attribute given or not.
- check uploaded files.
- transducer.
- just recipe to backup db.sqlite
- just recipe to upload db.sqlite
- the name of `db.sqlite` must be `konpy.sqlite`.
- name of the downloaded file.
- read version info from `pom.xml`.
- color self in answers and logins list.
- deliver ⚫️ to the same (bad) answers at once.
- <! DOCTYPE html>

## 0.28.2-SNAPSHOT (2025-07-18)

- views.clj:
```
  [:body {:hx-boost "true"}
```

- when rendering pages, prepend "<!DOCTYPE html>" at the contents top.
  if it is a htmx response, no "<!DOCTYPE html>" in it.

## 0.28.1 (2025-07-18)

- missed merge? do it again `inner links`.

## 0.28.0 (2025-07-18)

- inner links.

## 0.27.0 (2025-07-14)

- show answered users.
- record/display to QA.
- list of answers


## 0.26.2 (2025-07-12)

- forgot to log qa/q-a.

```
before:
(t/log! :debug (str "qa/q-a, q: " q")

updated:
(t/log! :info (str "qa/q-a, q: " q " by " (user request)))
```
## 0.26.1 (2025-07-12)

- order of problems list

```
(sort-by (juxt (fn [x] (* -1 (:week x))) :num))
```

## 0.26.0 (2025-07-12)

- added `goods_bads.clj`.

## 0.25.1

- display how many to-qa to this answer .
- refactored. (response/response `map`)  ok?

```
(defn- good-display [logins]
  (apply str (mapv #(str "❤️ " %) logins)))

(defn- bad-display [n]
  (apply str (for [_ (range n)]
               "⚫️ ")))
```

- add warning statement to stop redis before `just fetch`.

## 0.24.0 (2025-06-29)

- stopped java24 warnings.
- added answer/content - argument `s` is a python code or a markdown?
- no underline definition in tailwind.

```
@import 'tailwindcss';
@source inlude("underline"); <-
```
- fixed typo `downlaod`.
- padding left inside input.

## 0.20.3

- fetch problem number

## 0.20.2 (2025-06-18)

- no effect `:accept ".py, .md"` =>

## 0.20.1 (2025-06-18)

- improved looks of `/tasks`.

## 0.20.0 (2025-06-17)

- confirmation of 'to-QA'.
- updated sqlite-jdbc.

| :file    | :name                  | :current | :latest  |
|----------|------------------------|----------|----------|
| deps.edn | org.xerial/sqlite-jdbc | 3.49.1.0 | 3.50.1.0 |


## 0.19.1-hotfix (2025-06-15)

- does not hit any answers because of the new entry `week-num` is not
  found in old answers.
  resume to old version. version is `0.19.1-hotfix`.

```
(db/q '[:find ?e
        :where
        [?e :db/id 31]])
```

should be

```
(db/q '[:find ?e
        :where
        [(= ?e 31)]])
```

## 0.19.0 (2025-06-15)

- Q-A: fetch week-num and author from the answer.

## 0.18.4 (2025-06-14)

- FIXED: did not display 'sent'. response map.
- good❤️.

## 0.18.3 (2025-06-14)

- improve display good and bads.
- display as  user1, user2, user3. not ["user1" "user2" "user3"]
- display bads using ⚫️.

## 0.18.2 (2025-06-14)

- added pg.clj -- direct access to `questions` DB.
  didn't understand correct way to refer db-conn(s) from other
  namespaces.
- initial values of good/bad.

## 0.18.1 (2025-06-14)

- refactored `answer/show-answer`.
- eliminated forward references.
- not a good looking?
```
👍 []
👎 1
```

## 0.18.0 (2025-06-14)

- arranged good, bad buttn and qa input field.

## 0.17.1 (2025-06-12)

- added `answer/worm-eaten`.
- changed `answer/worm-eaten` to use `Character/isWhitespace`
  intead of #(= \space %)
- renamed `worm-eaten` to `hide-chars`.
- blacklist (fake)

## 0.16.3

- changed the method uploading answers from copy-paste to upload-files.
- TTL was wrong 24 hours. corrected it to 12 hours in `answer.clj`.
- renamed newer `last-answer` to `this-weeks-last-answer` because batting.
- /last-answer
- changed /aswer/recent-{answers,logins}
- added carmine/{put,get}-last-answer

## 0.16.1 (2025-06-08)

- black button
- java 24.0.1 came to macos.
- updated libraries.

| :file    | :name                         | :current   | :latest    |
|--------- | ----------------------------- | ---------- | -----------|
| deps.edn | clj-kondo/clj-kondo           | 2025.04.07 | 2025.06.05 |
|          | com.github.igrishaev/pg2-core | 0.1.39     | 0.1.40     |
|          | com.taoensso/telemere         | 1.0.0      | 1.0.1      |
|          | io.github.clojure/tools.build | v0.10.8    | v0.10.9    |
|          | io.github.tonsky/clj-reload   | 0.9.6      | 0.9.7      |
|          | metosin/reitit-ring           | 0.8.0      | 0.9.1      |
|          | org.clojure/clojure           | 1.12.0     | 1.12.1     |



## 0.15.0 (2025-06-07)

- stop no-use-upload-update systemd files.
- display last answer's login and answered time.
- display last login, who logined at when.
- pruned unused deps using `unused-deps`.
- app.melt# usermod -G redis ubuntu
- Justfile: fetch redis db to homebrew redis.

    scp ${DEST}:/var/lib/redis/dump.rdb /opt/homebrew/var/db/redis/

    - ubuntu redis 7.0.15
    - homebrew redis 8.0.2

  use orbstack redis ~/docker/redis@7/data/dump.rb?


## 0.14.7 (2025-05-30)

- answer.clj: (def sep ["🍄","🍅","🍋","🍏","🍇","🍒"])

## 0.14.6 (2025-05-26)

- show typing-avg/typing-count in answers.

## 0.14.5 (2025-05-25)

- append typing-ex trainig count to answers.


## 0.14.4 (2025-05-24)

- embed link to "/task" in "今週のPython"
- num🍅user, num🍅user, ...

## 0.14.3 (2025-05-24)

- redirect to /answer/e/others after sending one's answer.
- return 0 as typing-ex score if development mode.

## 0.14.2 (2025-05-24)

- changed format of recent answers from `user🍅num` to `num🍅user`.

## 0.14.0 (2025-05-22)

- adaptive pre(simply `pre` is suffice)
- download button.

## 0.13.1 (2025-05-22)

- improve recent-answers - append problem number to login name.
- fixed - `(shorten nil)` failed. returns "" now.
- sqlite vacuum - 7.6MB db.sqlite -> 2.4MB.

## 0.12.0

- links to `/answer/e` from admin page.

## 0.11.1 (2025-05-19)

- changed Typing: average last week's scores.

## 0.11.0 (2025-05-19)

- linked wil documents.
- fixed `just deply` bug.

## 0.10.2 (2025-05-19)

- fixed typing-ex bug.

## 0.10.1-bug

- answers which does not have :typing-ex.
- added app.melt:/etc/default/konpy.env

## 0.10.0 (2025-05-16)

pull typing scores.

- fixed bug:

    ; bad
    (catch Exception e (.getMssaage e))

    ; good
    (catch Exception e
        (t/log! :error (.getMessage e)))))

- added typing-ex namespace. insert avarage of last typing 10 times.

- update libraries

```sh
❯ neil dep upgrade
:action "upgrading" :lib io.github.tonsky/clj-reload :current-version 0.9.5 :version 0.9.6
:action "upgrading" :alias :dev :lib io.github.tonsky/clj-reload :current-version 0.9.5 :version 0.9.6
```

## 0.9.2 (2025-05-15)

- resend-btton
- my-2 around answers.

## 0.9.1 (2025-05-14)

- forgot to bump up view/version.
- return to /tasks after submit one's answer.

## 0.9.0 (2025-05-12)

- /admin/gc

## 0.8.4 (2025-05-10)

- the first 7 characters of SHA1 is sufficient(?).
  sha1 (-> answer remove-spaces sha1)
  removed comments from answers before calculating their SHA1.
- fixed htmx:target error.
- fixed answer.clj: arity of t/log! is two.

## 0.8.3 (2025-05-10)

- fixed carmine.clj `(jt/format "yyyy-MM-dd HH:mm:ss" (jt/local-date-time))`.
  must use HH for 24hour time format.
- added `utils/remove-python-comments`.

## 0.8.2 (2025-05-09)

- display ["user1" "user2"...] as [user1 user2 ...].

## 0.8.1 (2025-05-09)

- `just fetch` fetches `db.sqlite` and `konpy.log` from app.melt.
- link wil and type?
- refactor. introduced `answer/show-answer`.

## 0.8.0 (2025-05-09)

- reversed display order of `recent-answers` and `recent-logns`.
- use Redis by `com.taoensso/carmine`.
    - kp:login:<login> iso-datetime
    - kp:answer:<login> tid
- added `carmine/put-login`, `carmine/get-logins`.

## 0.7.8 (2025-05-09)

- show identicals in `answers-others` page.

## 0.7.7 (2025-05-09)

- forgot `[:div#logins]` and `[:div#answer]`.

## 0.7.6 (2025-05-08)

- recent-logins. analyzing "log/konpy.log".
- persistent log. replaced `>` by `>>`.

## 0.7.5 (2025-05-08)

- confirm to send an answer.
- removed unnecessary parts from pom.xml
- order reversed `answer/:n/others`.
- make login form size a little smaller.

## 0.7.4 (2025-05-07)

- recent uploader using htmx.

## 0.7.3 (2025-05-07)

- add a button from `/answer/:n` to `/tasks`.
- no use to `confirm to send`. since they can resend their answers.
  usefull in `/admin` page?
- show `/tasks/all` page. unanswerable.

## 0.7.2 (2025-05-07)

- longer answers than prepared height of `pre`. textare is better than pre?
- sort by date /answers/:n/others. also /answers/:n/self.
- p-1 for class "te".

## 0.7.1 (2025-05-07)

- replaced ':p' with ':pre' in /answer/:n/self and /answer/:n/others
- nobody can see other students' answers until he answer the question
  including admin.

## 0.6.7 (2025-05-06)

- self answers
- ohter answers

## 0.6.6 - hotfix

- wrongly deleted some part of answers.clj by mistake. resumed.

## 0.6.5 (2025-05-06)

- what I want is not the "multi valued attribute".

## 0.6.4 (2025-05-06)

- can answer.

## 0.6.3 (2025-05-06)

- can create tasks.
- can update tasks.
- renamed `create-tasks!` to `upsert-tasks!`.

## 0.6.2 (2025-05-05)

- updated libraries

| :file    | :name                       | :current | :latest |
|--------- | --------------------------- | -------- | --------|
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
