(ns jason.convenience
  (:require
   [camel-snake-kebab.core
    :refer [->camelCaseString
            ->snake_case_string
            ->PascalCaseString
            ->kebab-case-keyword]]

   [jason.core :refer [defcoders] :as jason]))

(declare
  ->wire-json
  <-wire-json
  ->db-json
  <-db-json)

(let [coders (jason/new-json-coders
               {:decode-key-fn (jason/->decode-key-fn keyword)
                :pretty        true})]
  (def <-json (:<-json coders))
  (def ->json (:->json coders)))

(defcoders wire
  :encode-key-fn (jason/->encode-key-fn ->camelCaseString)
  :decode-key-fn (jason/->decode-key-fn ->kebab-case-keyword)
  :pretty true)

(defcoders db
  :encode-key-fn (jason/->encode-key-fn ->snake_case_string)
  :decode-key-fn (jason/->decode-key-fn ->kebab-case-keyword)
  :pretty true)
