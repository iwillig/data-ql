# data-ql

This project is a Clojure based GraphQL implementation. The goal is to
provide a light-weight collection of tools that allows you to build a
complex GraphQL API in the Clojure language.

This project is similar in scope as Lacina, but takes a different
approach to both parsing, schema definition and execution engine.

## Parsing

DataQL uses the same underlying parsing engine as Lacinia, clj-antr.

DataQL exposes both your GraphQL Schema *and* your GraphQL Operations
(Mutations and Queries) as a DataLog database. This DataLog database
provides a convent place to discover and analysis your GraphQL System.

```clojure

```

## Schema Definition

DataLog provides several methods for defining your Schema. You can
define your GraphQL Schema in the Schema language. However, since
DataQL treats your schema and queries as a database, you are equally
welcome to define your schema in a DataLog Database.

## Execution Engine

GraphQL engines inherently need the ability to manage complex async
workflows. In the Clojure ecosystem there are a couple of different
choices to handling this. The default is core.async. data-ql uses the
mailfold library. The key to any complex project is how to handle
error conditions. While core.async does provide a very basic form of
error handling, has refined the async error handling.

## Query, Mutation and Schema managment.

### Schema linter

### GraphQL Plus

The data-log project encourages you to consider how you shape your
GraphQL API. While GraphQ offers some advantages over REST, it still
leaves a lot to the implementor to define. Query, mutation and types
can be defined in almost any shape. data-log offers an advance linter
that will examine your Schema and offer advice.

### GraphQL Vs REST

GraphQL and REST are very similar. They are a lot in common, while.
