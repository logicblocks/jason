(ns jason.convenience
  (:require
    [camel-snake-kebab.core
     :refer [->camelCaseString
             ->snake_case_string
             ->PascalCaseString
             ->kebab-case-keyword]]

    [jason.core :refer [defcoders] :as jason])
  (:import [com.fasterxml.jackson.datatype.joda JodaModule]
    [com.fasterxml.jackson.datatype.jsr310 JavaTimeModule]))

(declare
  ->wire-json
  <-wire-json
  ->db-json
  <-db-json)

(defcoders wire
  :encode-key-fn (jason/->encode-key-fn ->camelCaseString)
  :decode-key-fn (jason/->decode-key-fn ->kebab-case-keyword)
  :modules [(JodaModule.) (JavaTimeModule.)]
  :pretty true)

(defcoders db
  :encode-key-fn (jason/->encode-key-fn ->snake_case_string)
  :decode-key-fn (jason/->decode-key-fn ->kebab-case-keyword)
  :modules [(JodaModule.) (JavaTimeModule.)]
  :pretty true)
