(ns data-ql.parser
  (:require
   [clj-antlr.core :as antlr]
   [clojure.java.io :as io]))

(set! *warn-on-reflection* true)

(def default-antlr-options
  {:throw? false})

(defn make-namespace-map
  [namespace values]
  (persistent!
   (reduce-kv (fn [acc key value]
                (assoc! acc (keyword
                             (name namespace)
                             (name key))
                        value))
              (transient {})
              values)))

(def graphql-schema (partial make-namespace-map :graphql-schema))
(def position       (partial make-namespace-map :position))
(def type-spec       (partial make-namespace-map :type-spec))

;; Taken from Lacinia
(defn compile-grammar
  [path]
  (->
   (io/resource path)
   (slurp)
   (antlr/parser)))

(def graphql-grammar (compile-grammar "Graphql.g4"))
(def schema-grammar (compile-grammar "schema.g4"))

(defn parse
  "Given a Grammar file and a GraphQL String
   Rerturns a parsed expression"
  [grammar gql-string]
  (antlr/parse grammar default-antlr-options gql-string))

(defn parse-schema
  [schema-string]
  (parse schema-grammar schema-string))

(defn parse-schema-file
  [path]
  (parse-schema
   (slurp (io/resource path))))

(defn- get-antrl-position
  [antrl-prod]
  (let [antlr-meta (meta antrl-prod)]
    (when (:clj-antlr/position antlr-meta)
      (position antlr-meta))))

(defmulti antlr->map #'first)

(defmethod antlr->map :default
  [antrl-prod]
  antrl-prod)

(defmethod antlr->map :typeSpec
  [[:as args]]
  (let []))

(defmethod antlr->map :graphqlSchema
  [[:as args]]
  (let [graphql-forms (mapv antlr->map (rest args))]
    (println graphql-forms)
    (graphql-schema
     {:position (get-antrl-position args)})))

(comment

  (antlr->map
   (parse-schema-file "example.graphql")))

(defn parse-file
  [path])
