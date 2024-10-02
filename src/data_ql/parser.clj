(ns data-ql.parser
  (:require
   [data-ql.entities :as dql.entities]
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
  #{"'{'" "'}'" "'('" "')'" "'['" "']'" "'...'" "'fragment'" "'on'" "type" "&"
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
      (dql.entities/position (:clj-antlr/position antlr-meta)))))

(defmulti gql-tree
  "Given an antlr parse tree
   Returns a normalized and structured tree"
  #'first)

(def transform-xform
  (comp
   (remove keyword?)
   (remove string?)
   (remove ignored-terminal?)
   (map gql-tree)
   (remove nil?)))

(defn- trim-description-value
  "Given an antlr production of a description
   Returns a trimed a string value"
  [^String string-value]
  (str/trim
   (subs string-value 3 (- (.length string-value) 3))))

(defn prepare-parsed-production
  [form]
  (group-by dql.entities/detect-namespace
            (into []
                  transform-xform
                  form)))

(defmethod gql-tree :default
  [[:as antrl-prod]]
  antrl-prod)

(defmethod gql-tree :nameTokens
  [[_ token :as args]]
  (dql.entities/name-token
   {:name token
    :position (get-antrl-position args)}))

(defmethod gql-tree :description
  [[_ description-value :as args]]
  (dql.entities/description
   {:value
    (trim-description-value description-value)
    :position (get-antrl-position args)}))

(comment '(:nameTokens "name"))
(defmethod gql-tree :anyName
  [[_ name-token :as args]]
  (dql.entities/any-name {:name (gql-tree name-token)}))

(defmethod gql-tree :required
  [[:as args]]
  (dql.entities/required {:position (get-antrl-position args)}))

(comment '(:implementationDef "implements" "Pet" "&" "NickName"))
(defmethod gql-tree :implementationDef
  [[_ & implements-values :as args]]
  (dql.entities/implements
   {:values (remove ignored-terminal? implements-values)
    :position (get-antrl-position args)}))

(comment '(:typeName (:anyName (:nameTokens "String"))))
(defmethod gql-tree :typeName
  [[:as args]]
  (let [{:data-ql.entities/keys [any-name] :as info} (prepare-parsed-production args)]
    (dql.entities/type-name
     {:name (first any-name)
      :position (get-antrl-position args)})))

(defmethod gql-tree :typeSpec
  [[:as args]]
  (let [{:data-ql.entities/keys [type-name required] :as info} (prepare-parsed-production args)]
    (dql.entities/type-spec
     (merge
      {:position (get-antrl-position args)
       :type-name (first type-name)}
      (when required
        {:required required})))))

(defmethod gql-tree :argument
  [[:as args]]
  (dql.entities/argument {:position (get-antrl-position args)}))

(defmethod gql-tree :argList
  [[:as args]]
  (dql.entities/arg-list {:position (get-antrl-position args)}))

(defmethod gql-tree  :fieldDef
  [[:as args]]
  (let [{:data-ql.entities/keys [any-name type-spec arg-list required] :as info} (prepare-parsed-production args)]
    (pp (keys info))
    (dql.entities/field-def
     (merge
      {:position  (get-antrl-position args)
       :name (first any-name)
       :required required
       :type-spec type-spec}
      (when arg-list
        {:arg-list arg-list})))))

(defmethod gql-tree :fieldDefs
  [[:as args]]
  (let [{:dql.entities/keys [_field-def] :as info} (prepare-parsed-production args)]

    (dql.entities/fields {:position (get-antrl-position args)
                          :fields   (:data-ql.entities/field-def info)})))

(defmethod gql-tree :listType
  [[:as args]]
  (dql.entities/list-type {:position (get-antrl-position args)}))

(defmethod gql-tree :inputValueDefs
  [[:as args]]
  (dql.entities/input-value-defs {:position (get-antrl-position args)}))

(defmethod gql-tree :inputValueDef
  [[:as args]]
  (dql.entities/input-value-def {:position (get-antrl-position args)}))

(defmethod gql-tree :inputTypeDef
  [[:as args]]
  (let [{:as forms} (prepare-parsed-production args)]
    (dql.entities/input-type-def {:position (get-antrl-position args)})))

(defmethod gql-tree :typeDef
  [[:as args]]
  (let [{::dql.entities/keys [fields any-name description] :as info}
        (prepare-parsed-production args)]
    (dql.entities/make-type-def
     (merge
      {:fields      fields
       :name    (first any-name)}
      (when description
        {:description description})))))

(defmethod gql-tree :graphqlSchema
  [[_graphql-schema & forms :as args]]
  (let [{::dql.entities/keys [type-def input-type-def] :as info} (prepare-parsed-production forms)]
    (dql.entities/make-graphql-schema
     {:position       (get-antrl-position args)
      ;;:input-type-def input-type-def
      :type-defs       type-def})))

(comment

  (ns-unmap *ns* 'gql-tree)

  (gql-tree
   (parse-schema-file "example.graphql"))

  (ns-unmap *ns* 'gql-tree))

(defn parse-file
  [path])
