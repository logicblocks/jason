(ns jason.core
  "JSON encoding and decoding function construction with support for
  configurable key conversion."
  (:require
   [clojure.string :refer [starts-with?]]

   [jsonista.core :as jsonista]

   [camel-snake-kebab.core
    :refer [->camelCaseString
            ->snake_case_string
            ->kebab-case-keyword]]))

(def ^:dynamic *meta-prefix*
  "Meta key prefix used to detect and preserve meta fields. Defaults to '_'."
  "_")

(defn- is-meta-key? [k]
  (starts-with? (name k) *meta-prefix*))

(defn- if-metadata
  [meta-key-fn standard-key-fn]
  (fn [k]
    (if (is-meta-key? k)
      (meta-key-fn k)
      (standard-key-fn k))))

(defn- ->meta-key-fn [standard-key-fn]
  (fn [k]
    (let [converted (standard-key-fn (subs (name k) (count *meta-prefix*)))
          finaliser (if (keyword? converted) keyword name)]
      (finaliser (str *meta-prefix* (name converted))))))

(defn- ->key-fn
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
  any fields with leading meta prefix will retain their meta prefix.

  With a function argument, uses that function to convert both standard and
  meta keys while retaining leading meta prefix.

  Also accepts a map option argument in place of the function which can contain:

    - `:standard-key-fn`:  the key function to use for standard fields, also
                           used for meta fields if no :meta-key-fn provided.
    - `:meta-key-fn`:      the key function to use for meta fields, overriding
                           all meta handling."
  ([] (->encode-key-fn {}))
  ([fn-or-opts]
   (->key-fn
     (if (map? fn-or-opts) fn-or-opts {:standard-key-fn fn-or-opts})
     ->camelCaseString)))

(defn ->decode-key-fn
  "Constructs a function to decode JSON keys.

  With no arguments, decodes to kebab-case keywords and is `_meta` aware, i.e.,
  any fields with leading meta prefix will retain their meta prefix.

  With a function argument, uses that function to convert both standard and
  meta keys while retaining leading meta prefix.

  Also accepts a map option argument in place of the function which can contain:

    - `:standard-key-fn`:  the key function to use for standard fields, also
                           used for meta fields if no `:meta-key-fn` provided.
    - `:meta-key-fn`:      the key function to use for meta fields, overriding
                           all meta handling."
  ([] (->decode-key-fn {}))
  ([fn-or-opts]
   (->key-fn
     (if (map? fn-or-opts) fn-or-opts {:standard-key-fn fn-or-opts})
     ->kebab-case-keyword)))

(defn new-object-mapper
  "Constructs a Jackson `ObjectMapper`.

  With no arguments, the returned object mapper encodes and decodes keys exactly
  as provided, does not produce pretty JSON and includes no additional modules.

  The optional first parameter is a map of options. The following options are
  supported:

  | Mapper options      |                                  |
  | ------------------- | -------------------------------- |
  | `:modules`          | vector of `ObjectMapper` modules |

  &nbsp;

  | Encoding options    |                                                     |
  | ------------------- | --------------------------------------------------- |
  | `:pretty`           | set to `true` use Jackson's pretty-printing defaults (default: `true`) |
  | `:escape-non-ascii` | set to `true` to escape non-ASCII characters |
  | `:date-format`      | string for custom date formatting (default: `yyyy-MM-dd'T'HH:mm:ss'Z'`) |
  | `:encode-key-fn`    | `true` to coerce keyword keys to strings, `false` to leave them as keywords, or a function to provide custom coercion (default: the default of [[->encode-key-fn]]) |
  | `:encoders`         | a map of custom encoders where keys should be types and values should be encoder functions |

  Encoder functions take two parameters: the value to be encoded and a
  `JsonGenerator` object. The function should call `JsonGenerator` methods to
  emit the desired JSON.

  | Decoding options    |                                                     |
  | ------------------- | --------------------------------------------------- |
  | `:decode-key-fn`    |  `true` to coerce keys to keywords, false to leave them as strings, or a function to provide custom coercion (default: the default of [[->decode-key-fn]]) |
  | `:bigdecimals`      |  `true` to decode doubles as BigDecimals (default: `false`) |

  See https://metosin.github.io/jsonista for further details of the underlying
  JSON library, `jsonista`."
  ([] (new-object-mapper {}))
  ([options]
   (jsonista/object-mapper options)))

(def ^:dynamic *default-object-mapper*
  "Default ObjectMapper instance used when none provided. Has the same
  configuration as when [[new-object-mapper]] is called with no argument."
  (new-object-mapper))

(defn new-json-encoder
  "Constructs a JSON encoder function. With no argument, uses the default
  object mapper defined in [[*default-object-mapper*]]. Optionally, takes
  an `ObjectMapper` to use instead.

  The returned encoder returns nil on a nil value, otherwise JSON encodes it."
  ([] (new-json-encoder (new-object-mapper)))
  ([object-mapper]
   (fn [value]
     (when value
       (jsonista/write-value-as-string value object-mapper)))))

(defn new-json-decoder
  "Constructs a JSON decoder function. With no argument, uses the default
  object mapper defined in [[*default-object-mapper*]]. Optionally, takes
  an `ObjectMapper` to use instead.

  The returned decoder returns nil on a nil or empty string value, otherwise
  JSON decodes it."
  ([] (new-json-decoder (new-object-mapper)))
  ([object-mapper]
   (fn [value]
     (when value
       (when-not (and (string? value) (empty? value))
         (jsonista/read-value value object-mapper))))))

(defn new-json-coders
  "Constructs a pair of JSON encode / decode functions, at keys `:->json` and
  `:<-json` in the returned map.

  With no arguments, uses the default `ObjectMapper` as returned by
  [[new-object-mapper]]. The optional argument is the same map of options as
  described in the documentation for [[new-object-mapper]]."
  ([] (new-json-coders {}))
  ([options]
   (let [object-mapper (new-object-mapper options)]
     {:->json (new-json-encoder object-mapper)
      :<-json (new-json-decoder object-mapper)})))

(defmacro defcoders
  "Defines a pair of JSON encode / decode functions in the current namespace
  using the provided type, `-><type>-json` and `<-<type>-json`.

  The key-value arguments are the same as the options exposed on
  [[new-json-coders]] allowing full control over the generated functions."
  [t & {:as options}]
  `(let [{->json# :->json <-json# :<-json}
         (new-json-coders ~options)]
     (def ~(symbol (str "->" (name t) "-json")) ->json#)
     (def ~(symbol (str "<-" (name t) "-json")) <-json#)))
