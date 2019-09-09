(ns jason.core-test
  (:require
    [clojure.test :refer :all]
    [clojure.string :as string]

    [camel-snake-kebab.core
     :refer [->camelCaseString]]

    [jason.core :as jason]))

(defn- long-str [& args]
  (string/join "\n" args))

(deftest json
  (testing "wire-json->map"
    (testing "parses json"
      (is (=
            {:key 123}
            (jason/wire-json->map "{\"key\": 123}"))))

    (testing "converts keys to kebab case"
      (is (=
            {:some-key 123}
            (jason/wire-json->map "{\"someKey\": 123}"))))

    (testing "preserves keys prefixed with an underscore"
      (is (=
            {:_someLinks 123}
            (jason/wire-json->map "{\"_someLinks\": 123}")))))

  (testing "map->wire-json"
    (testing "returns a json string"
      (is (=
            (long-str
              "{"
              "  \"key\" : 123"
              "}")
            (jason/map->wire-json {:key 123}))))

    #_(testing "converts dates"
      (is (=
            (long-str
              "{"
              "  \"key\" : \"2019-02-03T00:00:00.000Z\""
              "}")
            (jason/map->wire-json {:key (date/date-time 2019 2 3)}))))

    (testing "converts keys to kebab case"
      (is (=
            (long-str
              "{"
              "  \"someKey\" : 123"
              "}")
            (jason/map->wire-json {:some-key 123}))))

    (testing "preserves meta keys"
      (is (=
            (long-str
              "{"
              "  \"_some-key\" : 123"
              "}")
            (jason/map->wire-json {:_some-key 123})))))

  (testing "db-json->map"
    (testing "parses json"
      (is (=
            {:key 123}
            (jason/db-json->map "{\"key\": 123}"))))

    (testing "converts keys to kebab case"
      (is (=
            {:some-key 123}
            (jason/db-json->map "{\"some_key\": 123}"))))

    (testing "preserves keys prefixed with an underscore"
      (is (=
            {:_some_links 123}
            (jason/db-json->map "{\"_some_links\": 123}")))))

  (testing "json->map"
    (testing "parses json"
      (is (=
            {:key 123}
            (jason/json->map "{\"key\": 123}" {}))))

    (testing "converts keys to kebab case"
      (is (=
            {:some-key 123}
            (jason/json->map "{\"some_key\": 123}" {}))))

    (testing "preserves keys prefixed with an underscore"
      (is (=
            {:_some_links 123}
            (jason/json->map "{\"_some_links\": 123}" {})))))

  (testing "map->db-json"
    (testing "returns a json string"
      (is (=
            (long-str
              "{"
              "  \"key\" : 123"
              "}")
            (jason/map->db-json {:key 123}))))

    #_(testing "converts dates"
      (is (=
            (long-str
              "{"
              "  \"key\" : \"2019-02-03T00:00:00.000Z\""
              "}")
            (jason/map->db-json {:key (date/date-time 2019 2 3)}))))

    (testing "converts keys to snake case"
      (is (=
            (long-str
              "{"
              "  \"some_key\" : 123"
              "}")
            (jason/map->db-json {:some-key 123}))))

    (testing "preserves meta keys"
      (is (=
            (long-str
              "{"
              "  \"_some-key\" : 123"
              "}")
            (jason/map->db-json {:_some-key 123}))))))
