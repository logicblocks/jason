(ns jason.convenience-test
  (:require
    [clojure.test :refer :all]
    [clj-time.core :as time]

    [jason.support :refer [multiline-str]]

    [jason.convenience :as convenience])
  (:import
    [java.time ZonedDateTime ZoneOffset]))

(deftest ->wire-json
  (testing "returns nil when nil provided"
    (is (nil? (convenience/->wire-json nil))))

  (testing "returns a json string"
    (is (= (multiline-str
             "{"
             "  \"key\" : 123"
             "}")
          (convenience/->wire-json {:key 123}))))

  (testing "converts keys to camel case"
    (is (= (multiline-str
             "{"
             "  \"someKey\" : 123"
             "}")
          (convenience/->wire-json {:some-key 123}))))

  (testing "preserves meta keys"
    (is (= (multiline-str
             "{"
             "  \"_someKey\" : 123"
             "}")
          (convenience/->wire-json {:_some-key 123}))))

  (testing "converts joda dates"
    (is (= (multiline-str
             "{"
             "  \"key\" : \"2019-02-03T00:00:00.000Z\""
             "}")
          (convenience/->wire-json {:key (time/date-time 2019 2 3)}))))

  (testing "converts java.time dates"
    (let [date-time (ZonedDateTime/of 2019 2 3 0 0 0 0 ZoneOffset/UTC)]
      (is (= (multiline-str
               "{"
               "  \"key\" : \"2019-02-03T00:00:00Z\""
               "}")
            (convenience/->wire-json {:key date-time}))))))

(deftest <-wire-json
  (testing "returns nil when nil provided"
    (is (nil? (convenience/<-wire-json nil))))

  (testing "returns nil when empty string provided"
    (is (nil? (convenience/<-wire-json ""))))

  (testing "parses json"
    (is (= {:key 123}
          (convenience/<-wire-json "{\"key\": 123}"))))

  (testing "converts keys to kebab case"
    (is (= {:some-key 123}
          (convenience/<-wire-json "{\"someKey\": 123}"))))

  (testing "preserves keys prefixed with an underscore"
    (is (= {:_some-links 123}
          (convenience/<-wire-json "{\"_someLinks\": 123}")))))

(deftest ->db-json
  (testing "returns nil when nil provided"
    (is (nil? (convenience/->db-json nil))))

  (testing "returns a json string"
    (is (= (multiline-str
             "{"
             "  \"key\" : 123"
             "}")
          (convenience/->db-json {:key 123}))))

  (testing "converts keys to snake case"
    (is (= (multiline-str
             "{"
             "  \"some_key\" : 123"
             "}")
          (convenience/->db-json {:some-key 123}))))

  (testing "preserves meta keys"
    (is (= (multiline-str
             "{"
             "  \"_some_key\" : 123"
             "}")
          (convenience/->db-json {:_some-key 123}))))

  (testing "converts joda dates"
    (is (= (multiline-str
             "{"
             "  \"key\" : \"2019-02-03T00:00:00.000Z\""
             "}")
          (convenience/->db-json {:key (time/date-time 2019 2 3)}))))

  (testing "converts java.time dates"
    (let [date-time (ZonedDateTime/of 2019 2 3 0 0 0 0 ZoneOffset/UTC)]
      (is (= (multiline-str
               "{"
               "  \"key\" : \"2019-02-03T00:00:00Z\""
               "}")
            (convenience/->db-json {:key date-time}))))))

(deftest <-db-json
  (testing "returns nil when nil provided"
    (is (nil? (convenience/<-db-json nil))))

  (testing "returns nil when empty string provided"
    (is (nil? (convenience/<-db-json ""))))

  (testing "parses json"
    (is (= {:key 123}
          (convenience/<-db-json "{\"key\": 123}"))))

  (testing "converts keys to kebab case"
    (is (= {:some-key 123}
          (convenience/<-db-json "{\"some_key\": 123}"))))

  (testing "preserves keys prefixed with an underscore"
    (is (= {:_some-links 123}
          (convenience/<-wire-json "{\"_some_links\": 123}")))))

(deftest ->open-banking-json
  (testing "returns nil when nil provided"
    (is (nil? (convenience/->open-banking-json nil))))

  (testing "returns a json string"
    (is (= (multiline-str
             "{"
             "  \"Key\" : 123"
             "}")
          (convenience/->open-banking-json {:key 123}))))

  (testing "converts keys to camel case"
    (is (= (multiline-str
             "{"
             "  \"SomeKey\" : 123"
             "}")
          (convenience/->open-banking-json {:some-key 123}))))

  (testing "preserves meta keys"
    (is (= (multiline-str
             "{"
             "  \"_SomeKey\" : 123"
             "}")
          (convenience/->open-banking-json {:_some-key 123}))))

  (testing "converts joda dates"
    (is (= (multiline-str
             "{"
             "  \"Key\" : \"2019-02-03T00:00:00.000Z\""
             "}")
          (convenience/->open-banking-json {:key (time/date-time 2019 2 3)}))))

  (testing "converts java.time dates"
    (let [date-time (ZonedDateTime/of 2019 2 3 0 0 0 0 ZoneOffset/UTC)]
      (is (= (multiline-str
               "{"
               "  \"Key\" : \"2019-02-03T00:00:00Z\""
               "}")
            (convenience/->open-banking-json {:key date-time}))))))

(deftest <-open-banking-json
  (testing "returns nil when nil provided"
    (is (nil? (convenience/<-open-banking-json nil))))

  (testing "returns nil when empty string provided"
    (is (nil? (convenience/<-open-banking-json ""))))

  (testing "parses json"
    (is (= {:key 123}
          (convenience/<-open-banking-json "{\"Key\": 123}"))))

  (testing "converts keys to kebab case"
    (is (= {:some-key 123}
          (convenience/<-open-banking-json "{\"SomeKey\": 123}"))))

  (testing "preserves keys prefixed with an underscore"
    (is (= {:_some-links 123}
          (convenience/<-open-banking-json "{\"_SomeLinks\": 123}")))))
