(ns data-ql.entities)

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

(def position        (partial make-namespaced-map ::position))
(def description     (partial make-namespaced-map ::description))
(def name-token      (partial make-namespaced-map ::name-token))
(def any-token       (partial make-namespaced-map ::any-token))
(def list-name-token (partial make-namespaced-map ::list-name-token))

(def type-spec   (partial make-namespaced-map ::type-spec))
(def field-def   (partial make-namespaced-map ::field-def))
(def fields      (partial make-namespaced-map ::fields))
(def list-type   (partial make-namespaced-map ::list-type))
(def type-name   (partial make-namespaced-map ::type-name))

(def argument    (partial make-namespaced-map ::argument))
(def arg-list    (partial make-namespaced-map ::arg-list))
(def implements  (partial make-namespaced-map ::implements))
(def required    (partial make-namespaced-map ::required))

(def input-type-def (partial make-namespaced-map ::input-type-def))

(def input-value-def (partial make-namespaced-map ::input-value-def))
(def input-value-defs (partial make-namespaced-map ::input-value-defs))

(def make-type-def    (partial make-namespaced-map ::type-def))

(def make-graphql-schema (partial make-namespaced-map ::graphql-schema))
