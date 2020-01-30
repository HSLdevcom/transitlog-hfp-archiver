# transitlog-hfp-archiver

[![Build Status](https://travis-ci.com/HSLdevcom/transitlog-hfp-archiver.svg?branch=master)](https://travis-ci.com/HSLdevcom/transitlog-hfp-archiver)

## Description

Converts yesterdays daily segment of HFP data into a CSV clump and archives it in Azure Blob storage.

### Locally

- ```mvn clean install``` - runs tests and builds a runnable jar  


### Docker build

- ```docker build .``` - run in project root to build a shippable docker container

### Upcoming work

- Api for reverse fetching of archived data from azure blob storage
