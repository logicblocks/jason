(ns jason.core
  (:require
    [clojure.string :refer [starts-with?]]

    [jsonista.core :as jsonista]

    [camel-snake-kebab.core
     :refer [->camelCaseString
             ->snake_case_string
             ->kebab-case-keyword]])
  (:import [com.fasterxml.jackson.datatype.joda JodaModule]))

(defn- if-metadata
  [key-fn case-key-fn]
  (fn [key]
    (if (starts-with? (name key) "_")
      (key-fn key)
      (case-key-fn key))))

(defn json->map [string options]
  (let [meta-key-fn (or (:meta-key-fn options) keyword)
        standard-key-fn (or (:standard-key-fn options) ->kebab-case-keyword)
        key-fn (if-metadata meta-key-fn standard-key-fn)

        mapper (jsonista/object-mapper
                 {:decode-key-fn key-fn
                  :modules       [(JodaModule.)]
                  :pretty        true})]
    (jsonista/read-value string mapper)))

(defn map->json [m options]
  (let [meta-key-fn (or (:meta-key-fn options) name)
        standard-key-fn (or (:standard-key-fn options) ->camelCaseString)
        key-fn (if-metadata meta-key-fn standard-key-fn)

        mapper (jsonista/object-mapper
                 {:encode-key-fn key-fn
                  :modules       [(JodaModule.)]
                  :pretty        true})]
    (jsonista/write-value-as-string m mapper)))

(defn wire-json->map [string & {:as options}]
  (json->map string options))

(defn db-json->map [string & {:as options}]
  (json->map string options))

(defn map->wire-json [m & {:as options}]
  (map->json m (merge {:standard-key-fn ->camelCaseString} options)))

(defn map->db-json [m & {:as options}]
  (map->json m (merge {:standard-key-fn ->snake_case_string} options)))