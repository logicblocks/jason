(ns jason.core
  "JSON encoding and decoding function construction with support for
  configurable key conversion.

  Mapper functions are constructed as:

    (require '[jason.core :as jason])
    (let [{:keys [->json <-json]} (jason/new-json-mappers)]
      (->json {:first-name \"Jess\"})
      ;; => \"{\\\"firstName\\\": \\\"Jess\\\"}\"

      (<-json \"{\\\"lastName\\\": \\\"Jacobs\\\"}\")
      ;; => {:last-name \"Jacobs\"}
      )

  ### Configuration

  Mappers can take custom key functions for encode and decode, constructed using
  `->encode-key-fn` and `->decode-key-fn`:

    (require '[camel-snake-kebab.core :refer [->snake_case_string]])
    (let [{keys [->json <-json]}
          (jason/new-json-mappers
            {:encode-key-fn (jason/->encode-key-fn ->snake_case_string)
             :decode-key-fn (jason/->decode-key-fn ->kebab-case-keyword)})]
      (->json {:first-name \"Jess\"})
      ;; => \"{\\\"first_name\\\": \\\"Jess\\\"}\"

      (<-json \"{\\\"last_name\\\": \\\"Jacobs\\\"}\")
      ;; => {:last-name \"Jacobs\"}

  TODO: document meta handling
  TODO: document additional options

  ### `defmappers`

  TODO: document `defmappers`"
  (:require
    [clojure.string :refer [starts-with?]]

    [jsonista.core :as jsonista]

    [camel-snake-kebab.core
     :refer [->camelCaseString
             ->snake_case_string
             ->kebab-case-keyword]])
  (:import
    [com.fasterxml.jackson.datatype.joda JodaModule]
    [com.fasterxml.jackson.datatype.jsr310 JavaTimeModule]))

(def ^:dynamic *meta-prefix*
  "Default meta key prefix used to detect and preserve meta fields."
  "_")

(defn- ^:no-doc is-meta-key? [k]
  (starts-with? (name k) *meta-prefix*))

(defn- ^:no-doc if-metadata
  [meta-key-fn standard-key-fn]
  (fn [k]
    (if (is-meta-key? k)
      (meta-key-fn k)
      (standard-key-fn k))))

(defn- ^:no-doc ->meta-key-fn [standard-key-fn]
  (fn [k]
    (let [converted (standard-key-fn (subs (name k) (count *meta-prefix*)))
          finaliser (if (keyword? converted) keyword name)]
      (finaliser (str *meta-prefix* (name converted))))))

(defn- ^:no-doc ->key-fn
  ([default-key-fn] (->key-fn {} default-key-fn))
  ([options default-key-fn]
    (let [standard-key-fn
          (get options :standard-key-fn default-key-fn)
          meta-key-fn
          (get options :meta-key-fn (->meta-key-fn standard-key-fn))]
      (if-metadata meta-key-fn standard-key-fn))))

(defn ->encode-key-fn
  "Constructs a function to encode JSON keys.

  With no arguments, encodes to camelCase strings and is _meta aware, i.e.,
  any fields with leading underscores will retain their leading underscore.

  With a function argument, uses that function to convert both standard and
  meta keys while retaining leading meta underscore.

  Also accepts a map option argument in place of the function which can contain:

    :standard-key-fn  the key function to use for standard fields, also used
                      for meta fields if no :meta-key-fn provided.
    :meta-key-fn      the key function to use for meta fields, overriding all
                      meta handling."
  ([] (->encode-key-fn {}))
  ([fn-or-opts]
    (->key-fn
      (if (map? fn-or-opts) fn-or-opts {:standard-key-fn fn-or-opts})
      ->camelCaseString)))

(defn ->decode-key-fn
  "Constructs a function to decode JSON keys.

  With no arguments, decodes to kebab-case keywords and is _meta aware, i.e.,
  any fields with leading underscores will retain their leading underscore.

  With a function argument, uses that function to convert both standard and
  meta keys while retaining leading meta underscore.

  Also accepts a map option argument in place of the function which can contain:

    :standard-key-fn  the key function to use for standard fields, also used
                      for meta fields if no :meta-key-fn provided.
    :meta-key-fn      the key function to use for meta fields, overriding all
                      meta handling."
  ([] (->decode-key-fn {}))
  ([fn-or-opts]
    (->key-fn
      (if (map? fn-or-opts) fn-or-opts {:standard-key-fn fn-or-opts})
      ->kebab-case-keyword)))

(defn new-json-mappers
  "Constructs a pair of JSON encode / decode functions, at keys `:->json` and
  `:<-json` in the returned map.

  With no arguments, the returned functions use the default encode / decode
  key functions, include support for both `org.joda.time` and `java.time`
  objects and produce pretty JSON.

  The optional first parameter is a map of options. The following options are
  available:

  | Mapper options      |                                |
  | ------------------- | ------------------------------ |
  | `:modules`          | vector of ObjectMapper modules |

  | Encoding options    |                                                     |
  | ------------------- | --------------------------------------------------- |
  | `:pretty`           | set to true use Jackson's pretty-printing defaults
                          (default: true) |
  | `:escape-non-ascii` | set to true to escape non ascii characters |
  | `:date-format`      | string for custom date formatting
                          (default: `yyyy-MM-dd'T'HH:mm:ss'Z'`) |
  | `:encode-key-fn`    | true to coerce keyword keys to strings, false to
                          leave them as keywords, or a function to provide
                          custom coercion
                          (default: the default of `->encode-key-fn`) |
  | `:encoders`         | a map of custom encoders where keys should be types
                          and values should be encoder functions |

  Encoder functions take two parameters: the value to be encoded and a
  JsonGenerator object. The function should call JsonGenerator methods to emit
  the desired JSON.

  | Decoding options    |                                                     |
  | ------------------- | --------------------------------------------------- |
  | `:decode-key-fn`    |  true to coerce keys to keywords, false to leave
                           them as strings, or a function to provide custom
                           coercion
                           (default: the default of `->decode-key-fn`) |
  | `:bigdecimals`      |  true to decode doubles as BigDecimals
                           (default: false) |

  See https://metosin.github.io/jsonista/jsonista.core.html#var-object-mapper
  for further details of the underlying JSON library, `jsonista`."
  ([] (new-json-mappers {}))
  ([options]
    (let [object-mapper
          (jsonista/object-mapper
            (merge
              {:encode-key-fn (->encode-key-fn)
               :decode-key-fn (->decode-key-fn)
               :modules       [(JodaModule.) (JavaTimeModule.)]
               :pretty true}
              options))]
      {:->json (fn [value] (jsonista/write-value-as-string value object-mapper))
       :<-json (fn [value] (jsonista/read-value value object-mapper))})))

(defmacro defmappers
  "Defines a pair of JSON encode / decode functions in the current namespace
  using the provided type, `-><type>-json` and `<-<type>-json`.

  The key-value arguments are the same as the options exposed on
  `new-json-mappers` allowing full control over the generated functions."
  [t & {:as options}]
  `(let [{->json# :->json <-json# :<-json}
         (new-json-mappers ~options)]
     (def ~(symbol (str "->" (name t) "-json")) ->json#)
     (def ~(symbol (str "<-" (name t) "-json")) <-json#)))
