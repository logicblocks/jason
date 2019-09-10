# jason

JSON encoding and decoding function construction with support for configurable 
key conversion.

Supports:
- conversion from kebab-case keywords to camel case JSON and back again by
  default
- correct key conversion for JSON payloads including metadata, 
  (e.g., as in HAL), indicated by a (configurable) underscore prefix
- configuration of all key conversions
- serialisation of `org.joda.time` and `java.time` types

## Install

Add the following to your `project.clj` file:

```clj
[b-social/jason "0.1.1"]
```

## Documentation

* [API Docs](http://b-social.github.io/jason)

## Usage

```clojure
(require '[jason.core :refer [defcoders] :as jason])
(require '[camel-snake-kebab.core :refer [->snake_case_string
                                          ->snake_case_keyword]])

(defcoders wire)
(defcoders db
  :encode-key-fn (jason/->encode-key-fn ->snake_case_string)
  :decode-key-fn (jason/->decode-key-fn ->snake_case_keyword))

(->wire-json {:first-name "Jess"})
;; => "{\"firstName\": \"Jess\"}"

(->db-json {:first-name "Jess"})
;; => "{\"first_name\": \"Jess\"}"

(<-wire-json "{\"firstName\": \"Jess\"}")
;; => {:first-name "Jess"}

(<-db-json "{\"first_name\": \"Jess\"}")
;; => {:first_name "Jess"}
```

See the [Getting Started](https://b-social.github.io/jason/getting-started.html) 
guide for more details.

## License

Copyright © 2018 B-Social Ltd.

Distributed under the terms of the 
[MIT License](http://opensource.org/licenses/MIT).
