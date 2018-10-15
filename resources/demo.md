---
key: value
list:
  - value 1
  - value 2
literal: |
  this is literal value.

  literal values 2
another_list: [1 2 3 4]
---

commonmark-java
===============

Java library for parsing and rendering [Markdown] text according to the
[CommonMark] specification (and some extensions).

[![Maven Central status](https://img.shields.io/maven-central/v/com.atlassian.commonmark/commonmark.svg)](https://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.atlassian.commonmark%22)
[![javadoc](https://www.javadoc.io/badge/com.atlassian.commonmark/commonmark.svg?color=blue)](https://www.javadoc.io/doc/com.atlassian.commonmark/commonmark)
[![Build status](https://travis-ci.org/atlassian/commonmark-java.svg?branch=master)](https://travis-ci.org/atlassian/commonmark-java)
[![codecov](https://codecov.io/gh/atlassian/commonmark-java/branch/master/graph/badge.svg)](https://codecov.io/gh/atlassian/commonmark-java)

Introduction
------------

Provides classes for parsing input to an abstract syntax tree of nodes
(AST), visiting and manipulating nodes, and rendering to HTML. It
started out as a port of [commonmark.js], but has since evolved into a
full library with a nice API and the following features:

* Small (core has no dependencies, extensions in separate artifacts)
* Fast (10-20 times faster than pegdown, see benchmarks in repo)
* Flexible (manipulate the AST after parsing, customize HTML rendering)
* Extensible (tables, strikethrough, autolinking and more, see below)

The library is supported on Java 8 and Java 9. It should work on Java 7
and Android too, but that is on a best-effort basis, please report
problems. For Android the minimum API level is 15, see the
[commonmark-android-test](commonmark-android-test) directory.

Coordinates for core library (see all on [Maven Central]):

```xml
<dependency>
    <groupId>com.atlassian.commonmark</groupId>
    <artifactId>commonmark</artifactId>
    <version>0.11.0</version>
</dependency>
```
