# UNDER DEVELOPMENT. THIS IS A WiP [NOT READY FOR PRODUCTION USE]

# Solr Lucene Analyzer Sudachi
<img align="center" src="https://cdn.jsdelivr.net/gh/WorksApplications/sudachi@develop/docs/Sudachi.png" width="3%" height="3%" /> [Sudachi](https://github.com/WorksApplications/Sudachi) を活用した日本語解析のための Lucene プラグイン <img align="center" src="https://cdn.jsdelivr.net/gh/WorksApplications/sudachi@develop/docs/Sudachi.png" width="3%" height="3%" /> A Lucene-based plugin on [Sudachi](https://github.com/WorksApplications/Sudachi) <img align="center" src="https://cdn.jsdelivr.net/gh/WorksApplications/sudachi@develop/docs/Sudachi.png" width="3%" height="3%" />

## Table of Contents
<!-- TOC -->
* [Plugin philosophy and background](#plugin-philosophy-and-background)
* [Prerequisites](#prerequisites)
  * [Downloading a Sudachi dictionary](#downloading-a-sudachi-dictionary)
* [Local Development](#local-development)
  * [System Requirements](#system-requirements)
  * [Build System](#build-system)
    * [List of Gradle tasks](#list-of-gradle-tasks)
    * [Building](#building)
    * [Formatting](#formatting)
  * [Testing](#testing)
    * [Unit tests](#unit-tests)
    * [Integration tests](#integration-tests)
* [Licenses](#licenses)
  * [Sudachi and Sudachi Logo](#sudachi-and-sudachi-logo)
  * [Lucene](#lucene)
  * [Solr Lucene Analyzer Sudachi](#solr-lucene-analyzer-sudachi)
<!-- TOC -->

## Plugin philosophy and background

The plugin is largely based on the other good work by the [Sudachi](https://github.com/WorksApplications/Sudachi) owners, the [elasticsearch-sudachi](https://github.com/WorksApplications/elasticsearch-sudachi) plugin. In addition, the current plugin drew inspiration from the past work by Sho Nakamura, the [solr-sudachi](https://github.com/sh0nk/solr-sudachi).

In terms of the filter configuration parity with the Lucene's built-n Kuromoji analyzer plugin, the Solr Lucene Analyzer plugin filter configuration matches [the current default in the built-in Kuromoji](https://github.com/apache/lucene/blob/305d9ebb86b74dea725ed38f2ae3d8bc1b107ed5/lucene/analysis/kuromoji/src/java/org/apache/lucene/analysis/ja/JapaneseAnalyzer.java#L109-L116)

[`Back to top`](#table-of-contents)

## Prerequisites

### Downloading a Sudachi dictionary

The plugin needs a dictionary in order to run the tests, thus, it needs to be downloaded using the following command:

```bash
./gradlew configureDictionariesLocally
```

The above command does the following:
1. Downloads a system dictionary `sudachi-dictionary-20230711-core` ZIP from AWS and unpacks it under the `/tmp/sudachi/`
2. Copies the [user-dictionary/user_lexicon.csv](user-dictionary/user_lexicon.csv) under the `/tmp/sudachi/`. The CSV is used to create a User dictionary. Although user defined dictionary is not really needed here, this sets an example how to add user entries to a dictionary.
3. Builds a Sudachi user dictionary from the CSV under the `/tmp/sudachi/`

[`Back to top`](#table-of-contents)

## Local Development

### System Requirements

- The plugin keeps Java 8 source compatibility at the moment

### Build System

The plugin uses [Gradle](https://gradle.org/) for as a build system.

#### List of Gradle tasks

For list of all the available Gradle tasks, run the following command:

```bash
./gradlew tasks
```

#### Building

Building and packaging can be done with the following command:

```bash
./gradlew build
```

As per [https://github.com/WorksApplications/Sudachi#dictionaries](https://github.com/WorksApplications/Sudachi#dictionaries), the above command will download a `system_core.dic` and will place it under [src/main/resources/system-dict/](src/main/resources/system-dict)

#### Formatting

The project leverages the [Spotless Gradle plugin](https://github.com/diffplug/spotless/tree/main/plugin-gradle) and follows the [palantir-java-format](https://github.com/palantir/palantir-java-format) style guide.

To format the sources, run the following command:

```bash
./gradlew spotlessApply
```

To note: Spotless Gradle plugin is invoked implicitly when running the `./gradlew build` command.

### Testing

#### Unit tests

To run unit tests, run the following command:

```bash
./gradlew test
```

#### Integration tests

The meaning of `integration` is that the test sources extend from `org.apache.lucene.analysis.BaseTokenStreamTestCase` in order to spin-up the Lucene ecosystem.

To run integration tests, run the following command:

```bash
./gradlew integrationTest
```

[`Back to top`](#table-of-contents)

## Licenses

### Sudachi and Sudachi Logo

Sudachi by Works Applications Co., Ltd. is licensed under the [Apache License, Version2.0](http://www.apache.org/licenses/LICENSE-2.0.html). See [https://github.com/WorksApplications/Sudachi#licenses](https://github.com/WorksApplications/Sudachi#licenses)

Sudachi logo by Works Applications Co., Ltd. is licensed under the [Apache License, Version2.0](http://www.apache.org/licenses/LICENSE-2.0.html). See [https://github.com/WorksApplications/Sudachi#logo](https://github.com/WorksApplications/Sudachi#logo)

### Lucene

Lucene, a high-performance, full-featured text search engine library written in Java is licensed under the [Apache License, Version2.0](http://www.apache.org/licenses/LICENSE-2.0.html). See [https://lucene.apache.org/core/documentation.html](https://lucene.apache.org/core/documentation.html)


### Solr Lucene Analyzer Sudachi

The Lucene-based Solr plugin leveraging [Sudachi](https://github.com/WorksApplications/Sudachi) by Alexander Zagniotov is licensed under the [Apache License, Version2.0](http://www.apache.org/licenses/LICENSE-2.0.html)

```
Copyright (c) 2023 Alexander Zagniotov

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
[`Back to top`](#table-of-contents)
