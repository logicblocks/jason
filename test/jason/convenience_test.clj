(ns jason.convenience-test
  (:require
   [clojure.test :refer :all]

   [jason.support :refer [multiline-str]]

   [jason.convenience :as convenience]))

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
          (convenience/->wire-json {:_some-key 123})))))

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
          (convenience/->db-json {:_some-key 123})))))

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
