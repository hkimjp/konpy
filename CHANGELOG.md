## Unreleased

- schema
  :db/cardinarity :db/cardinarity/many
- routes


## 0.4.0-SNAPSHOT (2025-05-02)

- (def schema {:identical {:db/cardinality :db.cardinality/many}})
- added db/entity - how it works?
- gitignored `.env`.
- **BREAKING** resources/public/assets/css

## [0.3.2] (2025-05-01)

- copy konpy.clj to hkimjp/datascript.clj
- login
- not found page, more precisely.
- assignments
- answers

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

[0.4.0]: https://github.com/hkimjp/konpy/compare/0.3.2...0.4.0
[0.3.2]: https://github.com/hkimjp/konpy/compare/0.2.0...0.3.2
[0.2.0]: https://github.com/hkimjp/konpy/compare/0.1.0...0.2.0
