# jason

[![Clojars Project](https://img.shields.io/clojars/v/io.logicblocks/jason.svg)](https://clojars.org/io.logicblocks/jason)
[![Clojars Downloads](https://img.shields.io/clojars/dt/io.logicblocks/jason.svg)](https://clojars.org/io.logicblocks/jason)
[![GitHub Contributors](https://img.shields.io/github/contributors-anon/logicblocks/jason.svg)](https://github.com/logicblocks/jason/graphs/contributors)


JSON encoding and decoding function construction with support for configurable 
key conversion.

Supports:
- configuration of all key conversions
- preservation of metadata fields (e.g., as in HAL) during conversion

Includes convenience coders for wire (i.e., service to service) JSON and
database JSON:

For wire JSON:
- converts from kebab-case keywords to camelCase JSON and back again

For database JSON:
- converts from kebab-case keywords to snake_case JSON and back again

## Install

Add the following to your `project.clj` file:

```clj
[io.logicblocks/jason "1.0.0"]
```

## Documentation

* [API Docs](http://logicblocks.github.io/jason)

## Usage

```clojure
(require '[jason.core :refer [defcoders] :as jason])
(require '[camel-snake-kebab.core :refer [->snake_case_string
                                          ->snake_case_keyword]])

(defcoders standard)
(defcoders db
  :encode-key-fn (jason/->encode-key-fn ->snake_case_string)
  :decode-key-fn (jason/->decode-key-fn ->snake_case_keyword))

(->standard-json {:first-name "Jess"})
;; => "{\"first-name\": \"Jess\"}"

(->db-json {:first-name "Jess"})
;; => "{\"first_name\": \"Jess\"}"

(<-standard-json "{\"firstName\": \"Jess\"}")
;; => {"firstName" "Jess"}

(<-db-json "{\"first_name\": \"Jess\"}")
;; => {:first_name "Jess"}
```

See the 
[Getting Started](https://logicblocks.github.io/jason/getting-started.html) 
guide for more details.

## License

Copyright &copy; 2023 LogicBlocks Maintainers

Distributed under the terms of the 
[MIT License](http://opensource.org/licenses/MIT).
