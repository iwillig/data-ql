(ns data-ql.main-test
  (:require [clojure.test :as t :refer [deftest is testing]]))

(deftest test-okay
  (testing "Given:"
    (is (true? false))))

(def test-graphql
  '(:graphqlSchema
    (:typeDef
     (:description "\"\"\"\nThis is an example doc string\n\"\"\"")
     "type"
     (:anyName (:nameTokens "Query"))
     (:fieldDefs
      "{"
      (:fieldDef
       (:anyName (:nameTokens "dog"))
       ":"
       (:typeSpec (:typeName (:anyName (:nameTokens "Dog")))))
      (:fieldDef
       (:anyName (:nameTokens "findDog"))
       (:argList
        "("
        (:argument
         (:anyName (:nameTokens "searchBy"))
         ":"
         (:typeSpec (:typeName (:anyName (:nameTokens "FindDogInput")))))
        ")")
       ":"
       (:typeSpec (:typeName (:anyName (:nameTokens "Dog")))))
      "}"))
    (:typeDef
     "type"
     (:anyName (:nameTokens "AddDocResult"))
     (:fieldDefs
      "{"
      (:fieldDef
       (:anyName (:nameTokens "name"))
       ":"
       (:typeSpec (:typeName (:anyName (:nameTokens "String")))))
      (:fieldDef
       (:anyName (:nameTokens "nickName"))
       ":"
       (:typeSpec (:typeName (:anyName (:nameTokens "String")))))
      (:fieldDef
       (:anyName (:nameTokens "barkVolumn"))
       ":"
       (:typeSpec (:typeName (:anyName (:nameTokens "Int")))))
      "}"))
    (:inputTypeDef
     "input"
     (:anyName (:nameTokens "AddDogInput"))
     (:inputValueDefs
      "{"
      (:inputValueDef
       (:anyName (:nameTokens "name"))
       ":"
       (:typeSpec (:typeName (:anyName (:nameTokens "String")))))
      (:inputValueDef
       (:anyName (:nameTokens "nickName"))
       ":"
       (:typeSpec (:typeName (:anyName (:nameTokens "String")))))
      (:inputValueDef
       (:anyName (:nameTokens "barkVolumn"))
       ":"
       (:typeSpec (:typeName (:anyName (:nameTokens "Int")))))
      "}"))
    (:typeDef
     "type"
     (:anyName (:nameTokens "Mutation"))
     (:fieldDefs
      "{"
      (:fieldDef
       (:anyName (:nameTokens "addDog"))
       (:argList
        "("
        (:argument
         (:anyName (:nameTokens "inputs"))
         ":"
         (:typeSpec (:typeName (:anyName (:nameTokens "AddDogInput")))))
        ")")
       ":"
       (:typeSpec (:typeName (:anyName (:nameTokens "AddDocResult")))))
      "}"))
    (:enumDef
     (:description "\"\"\"\nAnother doc string\n\"\"\"")
     "enum"
     (:anyName (:nameTokens "DogCommand"))
     (:enumValueDefs
      "{"
      (:enumValueDef (:nameTokens "SIT"))
      (:enumValueDef (:nameTokens "DOWN"))
      (:enumValueDef (:nameTokens "HEEL"))
      "}"))
    (:typeDef
     (:description "\"\"\"\nA dog implements the pet\n\"\"\"")
     "type"
     (:anyName (:nameTokens "Dog"))
     (:implementationDef "implements" "Pet" "&" "NickName")
     (:fieldDefs
      "{"
      (:fieldDef
       (:anyName (:nameTokens "name"))
       ":"
       (:typeSpec
        (:typeName (:anyName (:nameTokens "String")))
        (:required "!")))
      (:fieldDef
       (:anyName (:nameTokens "nickname"))
       ":"
       (:typeSpec (:typeName (:anyName (:nameTokens "String")))))
      (:fieldDef
       (:anyName (:nameTokens "barkVolume"))
       ":"
       (:typeSpec (:typeName (:anyName (:nameTokens "Int")))))
      (:fieldDef
       (:anyName (:nameTokens "doesKnowCommand"))
       (:argList
        "("
        (:argument
         (:anyName (:nameTokens "dogCommand"))
         ":"
         (:typeSpec
          (:typeName (:anyName (:nameTokens "DogCommand")))
          (:required "!")))
        ")")
       ":"
       (:typeSpec
        (:typeName (:anyName (:nameTokens "Boolean")))
        (:required "!")))
      (:fieldDef
       (:anyName (:nameTokens "isHouseTrained"))
       (:argList
        "("
        (:argument
         (:anyName (:nameTokens "atOtherHomes"))
         ":"
         (:typeSpec (:typeName (:anyName (:nameTokens "Boolean")))))
        ")")
       ":"
       (:typeSpec
        (:typeName (:anyName (:nameTokens "Boolean")))
        (:required "!")))
      (:fieldDef
       (:anyName (:nameTokens "owner"))
       ":"
       (:typeSpec (:typeName (:anyName (:nameTokens "Human")))))
      "}"))
    (:interfaceDef
     "interface"
     (:anyName (:nameTokens "Sentient"))
     (:fieldDefs
      "{"
      (:fieldDef
       (:anyName (:nameTokens "name"))
       ":"
       (:typeSpec
        (:typeName (:anyName (:nameTokens "String")))
        (:required "!")))
      "}"))
    (:interfaceDef
     "interface"
     (:anyName (:nameTokens "Pet"))
     (:fieldDefs
      "{"
      (:fieldDef
       (:anyName (:nameTokens "name"))
       ":"
       (:typeSpec
        (:typeName (:anyName (:nameTokens "String")))
        (:required "!")))
      "}"))
    (:interfaceDef
     "interface"
     (:anyName (:nameTokens "NickName"))
     (:fieldDefs
      "{"
      (:fieldDef
       (:anyName (:nameTokens "nickName"))
       ":"
       (:typeSpec (:typeName (:anyName (:nameTokens "String")))))
      "}"))
    (:typeDef
     "type"
     (:anyName (:nameTokens "Alien"))
     (:implementationDef "implements" "Sentient")
     (:fieldDefs
      "{"
      (:fieldDef
       (:anyName (:nameTokens "name"))
       ":"
       (:typeSpec
        (:typeName (:anyName (:nameTokens "String")))
        (:required "!")))
      (:fieldDef
       (:anyName (:nameTokens "homePlanet"))
       ":"
       (:typeSpec (:typeName (:anyName (:nameTokens "String")))))
      "}"))
    (:typeDef
     "type"
     (:anyName (:nameTokens "Human"))
     (:implementationDef "implements" "Sentient")
     (:fieldDefs
      "{"
      (:fieldDef
       (:anyName (:nameTokens "name"))
       ":"
       (:typeSpec
        (:typeName (:anyName (:nameTokens "String")))
        (:required "!")))
      (:fieldDef
       (:anyName (:nameTokens "pets"))
       ":"
       (:typeSpec
        (:listType
         "["
         (:typeSpec
          (:typeName (:anyName (:nameTokens "Pet")))
          (:required "!"))
         "]")))
      "}"))
    (:enumDef
     "enum"
     (:anyName (:nameTokens "CatCommand"))
     (:enumValueDefs "{" (:enumValueDef (:nameTokens "JUMP")) "}"))
    (:typeDef
     "type"
     (:anyName (:nameTokens "Cat"))
     (:implementationDef "implements" "Pet")
     (:fieldDefs
      "{"
      (:fieldDef
       (:anyName (:nameTokens "name"))
       ":"
       (:typeSpec
        (:typeName (:anyName (:nameTokens "String")))
        (:required "!")))
      (:fieldDef
       (:anyName (:nameTokens "nickname"))
       ":"
       (:typeSpec (:typeName (:anyName (:nameTokens "String")))))
      (:fieldDef
       (:anyName (:nameTokens "doesKnowCommand"))
       (:argList
        "("
        (:argument
         (:anyName (:nameTokens "catCommand"))
         ":"
         (:typeSpec
          (:typeName (:anyName (:nameTokens "CatCommand")))
          (:required "!")))
        ")")
       ":"
       (:typeSpec
        (:typeName (:anyName (:nameTokens "Boolean")))
        (:required "!")))
      (:fieldDef
       (:anyName (:nameTokens "meowVolume"))
       ":"
       (:typeSpec (:typeName (:anyName (:nameTokens "Int")))))
      "}"))
    (:unionDef
     "union"
     (:anyName (:nameTokens "CatOrDog"))
     "="
     (:unionTypes
      (:anyName (:nameTokens "Cat"))
      "|"
      (:anyName (:nameTokens "Dog"))))
    (:unionDef
     "union"
     (:anyName (:nameTokens "DogOrHuman"))
     "="
     (:unionTypes
      (:anyName (:nameTokens "Dog"))
      "|"
      (:anyName (:nameTokens "Human"))))
    (:unionDef
     "union"
     (:anyName (:nameTokens "HumanOrAlien"))
     "="
     (:unionTypes
      (:anyName (:nameTokens "Human"))
      "|"
      (:anyName (:nameTokens "Alien"))))
    (:inputTypeDef
     "input"
     (:anyName (:nameTokens "FindDogInput"))
     (:inputValueDefs
      "{"
      (:inputValueDef
       (:anyName (:nameTokens "name"))
       ":"
       (:typeSpec (:typeName (:anyName (:nameTokens "String")))))
      (:inputValueDef
       (:anyName (:nameTokens "owner"))
       ":"
       (:typeSpec (:typeName (:anyName (:nameTokens "String")))))
      "}"))))

(deftest test-type-def-with-description

  (testing ""
    (is (= {} {}))))
