# jason

JSON encoding and decoding function construction with support for configurable 
key conversion.

## Install

Add the following to your `project.clj` file:

```clj
[b-social/jason "0.0.3"]
```

## Documentation

* [API Docs](http://b-social.github.io/jason)

## Usage

Mapper functions are constructed as:

```clojure
(require '[jason.core :as jason])
(let [{:keys [->json <-json]} (jason/new-json-mappers)]
  (->json {:first-name "Jess"})
  ;; => "{\"firstName\": \"Jess\"}"

  (<-json "{\"lastName\": \"Jacobs\"}")
  ;; => {:last-name "Jacobs"}
  )
```

### Configuration

Mappers can take custom key functions for encode and decode, constructed using
`->encode-key-fn` and `->decode-key-fn`:

```clojure
(require '[camel-snake-kebab.core :refer [->snake_case_string
                                          ->kebab-case-keyword]])
(let [{:keys [->json <-json]}
      (jason/new-json-mappers
        {:encode-key-fn (jason/->encode-key-fn ->snake_case_string)
         :decode-key-fn (jason/->decode-key-fn ->kebab-case-keyword)})]
  (->json {:first-name "Jess"})
  ;; => "{\"first_name\": \"Jess\"}"

  (<-json "{\"last_name\": \"Jacobs\"}")
  ;; => {:last-name "Jacobs"}
  )
```

## License

Copyright © 2018 B-Social Ltd.

Distributed under the terms of the 
[MIT License](http://opensource.org/licenses/MIT).
