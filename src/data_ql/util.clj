(ns data-ql.util
  (:require [fipp.clojure :as fipp]))

(defn pp
  [& args]
  (let [[arg rest-args] args]

    (println)
    (fipp/pprint (if-not (seq rest-args)
                   arg
                   args))
    (println)))
