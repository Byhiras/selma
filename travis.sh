#!/bin/bash

env

if [ ${TRAVIS_PULL_REQUEST} = 'false' ]; then
    mvn clean deploy --settings target/travis/settings.xml
else
    mvn clean verify --settings target/travis/settings.xml
fi
