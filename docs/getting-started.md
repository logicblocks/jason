# Getting Started

`jason` is a thin wrapper around 
[`jsonista`](https://metosin.github.io/jsonista), a 
[`jackson`](https://github.com/FasterXML/jackson) based JSON encoding and 
decoding library, providing sensible default but fully configurable key 
conversion facilities.

`jackson` utilises an `ObjectMapper` instance for all JSON serialisation and
deserialisation. `ObjectMapper` instances are expensive to create and should
be reused as much as possible. As such, `jason` aims to minimise the number of
`ObjectMapper` instances required.

## Building coder functions

To build an encoder and decoder:

```clojure
(require '[jason.core :as jason])

(def default-object-mapper (jason/new-object-mapper))
(def ->default-json (jason/new-json-encoder default-object-mapper))
(def <-default-json (jason/new-json-decoder default-object-mapper))

(->default-json {:first-name "Jane"})
;; => "{\"first-name\":\"Jane\"}"

(<-default-json "{\"firstName\":\"Jane\"}")
;; => {"firstName" "Jane"}
```

As shown, by default, the encoder will leave keys untouched during encoding and
decoding.

### Basic configuration

To change the conversions:

```clojure
(require '[camel-snake-kebab.core 
           :refer [->snake_case_string
                   ->snake_case_keyword]])

(def custom-object-mapper (jason/new-object-mapper
                            {:encode-key-fn 
                             (jason/->encode-key-fn ->snake_case_string)
                             :decode-key-fn
                             (jason/->decode-key-fn ->snake_case_keyword)}))
(def ->custom-json (jason/new-json-encoder custom-object-mapper))
(def <-custom-json (jason/new-json-decoder custom-object-mapper))

(->custom-json {:first-name "Jane"})
;; => "{\"first_name\":\"Jane\"}"

(<-custom-json "{\"firstName\":\"Jane\"}")
;; => {:first_name "Jane"}
```

### Meta handling

In addition to key conversion for standard keys, `jason` is aware of metadata
keys. By default these are keys prefixed with an underscore, e.g., `_links`.
By default metadata keys are left untouched and the conversion functions 
returned by `->encode-key-fn` and `->decode-key-fn` convert the key whilst 
retaining the metadata prefix:

```clojure
(def default-encode-key-fn (jason/->encode-key-fn))
(def default-decode-key-fn (jason/->decode-key-fn))

(default-encode-key-fn :_meta-data)
;; => "_metaData"

(default-decode-key-fn "_metaData")
;; => :_meta-data

(def custom-encode-key-fn (jason/->encode-key-fn ->snake_case_string))
(def custom-decode-key-fn (jason/->decode-key-fn ->snake_case_keyword))

(custom-encode-key-fn :_meta-data)
;; => "_meta_data"

(custom-decode-key-fn "_metaData")
;; => :_meta_data
```

To fully override the metadata handling:

```clojure
(def heterogeneous-encode-key-fn (jason/->encode-key-fn 
                                   {:standard-key-fn ->snake_case_string
                                    :meta-key-fn name}))
(def heterogeneous-decode-key-fn (jason/->decode-key-fn
                                   {:standard-key-fn ->snake_case_keyword
                                    :meta-key-fn keyword}))

(heterogeneous-encode-key-fn :_meta-data)
;; => "_meta-data"
(heterogeneous-encode-key-fn :other-field)
;; => "other_field"

(heterogeneous-decode-key-fn "_metaData")
;; => :_metaData
(heterogeneous-decode-key-fn "otherField")
;; => :other_field
```

### Additional configuration

In addition to key function configuration, [[jason.core/new-object-mapper]]
exposes all configuration options of the underlying `jsonista` library.

See [[jason.core/new-object-mapper]] for more details.

## Defining coder functions using `new-json-coders` and `defcoders`

The [[jason.core/new-json-coders]] function builds both coders at once and 
returns them in a map, hiding the details of the `ObjectMapper`:

```clojure
(let [{:keys [->json <-json]} 
      (jason/new-json-coders
        :encode-key-fn (jason/->encode-key-fn)
        :decode-key-fn (jason/->decode-key-fn))]
  (->json {:first-name "Jess"})
  ;; => "{\"firstName\":\"Jess\"}"

  (<-json "{\"lastName\":\"Jacobs\"}")
  ;; => {:last-name "Jacobs"}
  )

(let [{:keys [->json <-json]} 
      (jason/new-json-coders
        {:encode-key-fn (jason/->encode-key-fn ->snake_case_string)
         :decode-key-fn (jason/->decode-key-fn ->snake_case_keyword)})]
  (->json {:first-name "Jane"})
  ;; => "{\"first_name\":\"Jane\"}"
  
  (<-json "{\"firstName\":\"Jane\"}")
  ;; => {:first_name "Jane"}
  )
```

Usually, you'll want to hang on to the resulting coders for later use. This
can be achieved with:

```clojure
(let [{:keys [->json <-json]} 
      (jason/new-json-coders
        {:encode-key-fn (jason/->encode-key-fn ->snake_case_string)
         :decode-key-fn (jason/->decode-key-fn ->snake_case_keyword)})]
  (def ->db-json ->json)
  (def <-db-json <-json))
```

However, for convenience, the `defcoders` macro performs this task for you:

```clojure
(require '[jason.core :refer [defcoders]])

(defcoders db
  :encode-key-fn (jason/->encode-key-fn ->snake_case_string)
  :decode-key-fn (jason/->decode-key-fn ->snake_case_keyword))

(->db-json {:first-name "Jane"})
;; => "{\"first_name\":\"Jane\"}"
  
(<-db-json "{\"firstName\":\"Jane\"}")
;; => {:first_name "Jane"}
```

All options that can be passed to `new-json-coders` or `new-object-mapper`
also apply to `defcoders`.

## Convenience coder functions

`jason` includes convenience coder functions for wire JSON (e.g., from service 
to service) and database JSON (e.g., for databases that support JSON types).

The wire JSON coders:
- convert from kebab-case keywords to camelCase JSON and back again
- includes serialisation of `org.joda.time` and `java.time` types

To use the wire JSON coders:

```clojure
(require '[jason.convenience :as conv])

(conv/->wire-json {:first-name "Jane"})
;; => "{\n  \"firstName\" : \"Jane\"\n}"
  
(conv/<-wire-json "{\n  \"firstName\" : \"Jane\"\n}")
;; => {:first-name "Jane"}
```

The database JSON coders:
- convert from kebab-case keywords to snake_case JSON and back again
- includes serialisation of `org.joda.time` and `java.time` types

To use the database JSON coders:

```clojure
(require '[jason.convenience :as conv])

(conv/->db-json {:first-name "Jane"})
;; => "{\n  \"first_name\" : \"Jane\"\n}"
  
(conv/<-db-json "{\n  \"first_name\" : \"Jane\"\n}")
;; => {:first-name "Jane"}
```
