(ns data-ql.parser
  (:require
   [clojure.string :as str]
   [clj-antlr.core :as antlr]
   [fipp.clojure :as fipp]
   [clojure.java.io :as io]))

(defn pp
  [& args]
  (let [[arg rest-args] args]

    (println)
    (fipp/pprint (if-not (seq rest-args)
                   arg
                   args))
    (println)))

(set! *warn-on-reflection* true)

(def default-antlr-options
  {:throw? false})

(defn detect-namespace
  [x]
  (::namespace (meta x)))

(defn make-namespaced-map
  [namespace values]
  (with-meta
    (reduce-kv (fn [acc key value]
                 (assoc acc (keyword
                             (name namespace)
                             (name key))
                        value))
               {}
               values)
    {::namespace namespace}))

(def position        (partial make-namespaced-map :position))
(def description     (partial make-namespaced-map :description))
(def name-token      (partial make-namespaced-map :name-token))
(def any-token       (partial make-namespaced-map :any-token))
(def list-name-token (partial make-namespaced-map :list-name-token))

(def type-spec   (partial make-namespaced-map :type-spec))
(def field-def   (partial make-namespaced-map :field-def))
(def fields      (partial make-namespaced-map :fields))
(def list-type   (partial make-namespaced-map :list-type))
(def type-name   (partial make-namespaced-map :type-name))
(def type-def    (partial make-namespaced-map :type-def))
(def argument    (partial make-namespaced-map :argument))
(def arg-list    (partial make-namespaced-map :arg-list))
(def implements  (partial make-namespaced-map :implements))
(def required    (partial make-namespaced-map :required))

(def input-type-def (partial make-namespaced-map :input-type-def))

(def input-value-def (partial make-namespaced-map :input-value-def))
(def input-value-defs (partial make-namespaced-map :input-value-defs))

(def make-graphql-schema (partial make-namespaced-map ::graphql-schema))

;; Taken from Lacinia
(defn compile-grammar
  [path]
  (->
   (io/resource path)
   (slurp)
   (antlr/parser)))

(def graphql-grammar (compile-grammar "Graphql.g4"))
(def schema-grammar (compile-grammar "schema.g4"))

(def ^:private ignored-terminals
  "Textual fragments which are to be immediately discarded as they have no
  relevance to a formed parse tree."
  #{"'{'" "'}'" "'('" "')'" "'['" "']'" "'...'" "'fragment'" "'on'" "type"
    "':'" "'='" "'$'" "'!'" "\"" "'@'"})

(defn ignored-terminal?
  [token-name]
  (contains? ignored-terminals token-name))

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
      (position
       (:clj-antlr/position antlr-meta)))))

(defmulti antlr->map #'first)

(def transform-xform
  (comp
   (remove keyword?)
   (remove string?)
   (remove ignored-terminal?)
   (map antlr->map)
   (remove nil?)))

(defn- trim-description-value
  "Given an antlr production of a description
   Returns a trimed a string value"
  [^String string-value]
  (str/trim
   (subs string-value 3 (- (.length string-value) 3))))

(defn prepare-parsed-production
  [form]
  (group-by detect-namespace
            (into []
                  transform-xform
                  form)))

(defmethod antlr->map :default
  [[:as antrl-prod]]
  antrl-prod)

(defmethod antlr->map :nameTokens
  [[_ token :as args]]
  (name-token
   {:name token
    :position (get-antrl-position args)}))

(defmethod antlr->map :description
  [[_ description-value :as args]]
  (description
   {:value
    (trim-description-value description-value)
    :position (get-antrl-position args)}))

(defmethod antlr->map :anyName
  [[_ name-token :as args]]
  (any-token {:name (antlr->map name-token)}))

(defmethod antlr->map :required
  [[:as args]]
  (required {:position (get-antrl-position args)}))

(defmethod antlr->map :implementationDef
  [[:as args]]
  (implements {:position (get-antrl-position args)}))

(defmethod antlr->map :typeName
  [[:as args]]
  (let [{:keys [any-token] :as info} (prepare-parsed-production args)]
    (type-name {:position (get-antrl-position args)
                :any-token any-token})))

(defmethod antlr->map :typeSpec
  [[:as args]]
  (let [{:keys [type-name required] :as info} (prepare-parsed-production args)]
    (type-spec
     (merge
      {:position (get-antrl-position args)
       :type-name type-name}
      (when require
        {:required required})))))

(defmethod antlr->map :argument
  [[:as args]]
  (argument {:position (get-antrl-position args)}))

(defmethod antlr->map :argList
  [[:as args]]
  (arg-list {:position (get-antrl-position args)}))

(defmethod antlr->map  :fieldDef
  [[:as args]]
  (let [{:keys [any-token type-spec arg-list] :as _info} (prepare-parsed-production args)]
    (field-def {:position  (get-antrl-position args)
                :any-token (first any-token)
                :arg-list  arg-list
                :type-spec (first type-spec)})))

(defmethod antlr->map  :fieldDefs
  [[:as args]]
  (let [{:keys [field-def] :as info} (prepare-parsed-production args)]
    (fields {:position (get-antrl-position args)
             :fields   field-def})))

(defmethod antlr->map :listType
  [[:as args]]
  (list-type {:position (get-antrl-position args)}))

(defmethod antlr->map :inputValueDefs
  [[:as args]]
  (input-value-defs {:position (get-antrl-position args)}))

(defmethod antlr->map :inputValueDef
  [[:as args]]
  (input-value-def {:position (get-antrl-position args)}))

(defmethod antlr->map :inputTypeDef
  [[:as args]]
  (let [{:as forms} (prepare-parsed-production args)]
    (input-type-def {:position (get-antrl-position args)})))

(defmethod antlr->map :typeDef
  [[:as args]]
  (let [{:keys [fields any-token description]}
        (prepare-parsed-production args)]
    (type-def
     (merge
      {:fields      fields
       :any-name    any-token}
      (when description
        {:description description})))))

(defmethod antlr->map :graphqlSchema
  [[_graphql-schema & forms :as args]]
  (let [{:keys [type-def input-value-def]} (prepare-parsed-production forms)]
    (make-graphql-schema {:position (get-antrl-position args)
                          :input-value-def input-value-def
                          :type-def        type-def})))

(comment

  (ns-unmap *ns* 'antlr->map)

  (antlr->map
   (parse-schema-file "example.graphql")))

(defn parse-file
  [path])
