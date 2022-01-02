(ns jason.support
  (:require
   [clojure.string :as string]))

(defn multiline-str [& args]
  (string/join "\n" args))
