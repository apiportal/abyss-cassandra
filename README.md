[![Build Status](https://travis-ci.org/apiportal/abyss-cassandra.svg?branch=master)](https://travis-ci.org/apiportal/abyss-cassandra)
# Abyss Cassandra

This is the repository for Abyss Cassandra 

This module defines common classes and methods for Abyss projects:

- Cqls
- API traffic log message codec and consumer
- Cassandra logger verticle

## Build and Run
In order to create a fat jar package, install jdk >= 8 and Maven; afterwards, run this command:

```bash
mvn clean package
```

## Publish sites with Maven scm publish plugin

Execute following commands to publish sites:
```
mvn clean install site site:stage scm-publish:publish-scm
```