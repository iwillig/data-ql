(ns data-ql.datascript
  (:require [datascript.core :as d]
            [data-ql.parser   :as dql.parser]
            [data-ql.entities :as dql.entities])
  (:import
   (java.util.concurrent.atomic AtomicLong)))

(defonce ^:private temp-id
  (AtomicLong. 0))

(defn- next-temp-id []
  (.decrementAndGet ^AtomicLong temp-id))

(def schema
  {})

(defn gen-tx-data-position
  [position-temp-id position]
  (assoc position :db/id position-temp-id))

(defn gen-tx-data-graphql-schema
  [gql-temp-id position-temp-id entity]
  [{:db/id gql-temp-id
    :graphql/name ""
    :graphql/position position-temp-id}
   (gen-tx-data-position position-temp-id (:graphql-schema/position entity))])

(defmulti to-datalog
  (fn [acc gql-component]
    (dql.entities/detect-namespace gql-component)))

(defmethod to-datalog :default
  [acc args]
  acc)

(defmethod to-datalog :data-ql.entities/graphql-schema
  [acc args]
  (let [graphql-schema-temp-id (next-temp-id)
        position-temp-id (next-temp-id)]

    (update acc ::tx-data into (gen-tx-data-graphql-schema graphql-schema-temp-id position-temp-id args))
    (update acc ::temp-ids into [graphql-schema-temp-id position-temp-id])))

(defn flatten-gql-tree
  [gql-tree]
  (to-datalog {::tx-data []
               ::temp-ids []}
              gql-tree))

(comment

  (ns-unmap *ns* 'to-datalog)

  (def schema-tree (dql.parser/gql-tree
                    (dql.parser/parse-schema-file "example.graphql")))

  (flatten-gql-tree schema-tree))
