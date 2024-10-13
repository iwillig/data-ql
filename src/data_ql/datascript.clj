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

;; String, Int, ID
(def schema
  {:type-def/name       {:db/type :db.type/keyword}
   :type-def/fields     {:db/type        :db.type/ref
                         :db/cardinality :db.cardinality/many}
   :field/name          {:db/type :db.type/keyword}
   :field/type-sec      {:db/type :db.type/ref}
   :type-spec/type      {:db/type :db.type/ref}
   :type-spec/required? {:db/type :db.type/boolean}


   })

(comment

  (:typeSpec
   (:listType
    (:typeSpec
     (:typeName (:anyName (:nameTokens "ID")))
     (:required "!")))
   (:required "!"))


  {:type-spec/type {:type-def/name :ID}
   :type-spec/required? true
   :type-spec/inner-required? true
   :type-spec/cardinality :type-spec.cardinality/many}

  )




(comment
  '(tree-seq branch?
             childern
             root)

  )

(defn procese-tree
  [graphql-tree]
  (reduce (fn []

            )
          {:refs {:type {}
                  :input {:FindDogInput :temp-id}}
           :tx-data [[:db/add ]]}

          graphql-tree))




(comment



  (ns-unmap *ns* 'to-datalog)


  )
