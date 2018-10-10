# Splunk Homework

## Description

Splunk has decided to diversify its product line and go into the movie data business. We are collecting movie
information from a variety of sources and exposing REST API to enable anyone to be able fetch these details.
In addition, users can also submit movie information. Splunk is using some fancy ML algorithms to process the
submission and add to our movie database.

This project verifies that APis to search and create movies are working correctly. The cases are written in BDD style
and saved in test/resources. The test runner is RunCucumberTest. And the steps are defined in StepDefinitions class.

## How to run

To run the tests you'll need Maven, Git client and Java (version 8 or above) installed on your machine.
To run the tests just execute `mvn test` from the project root.

If you don't want to install the tools on your machine, then it is possible to run with docker (you would still need
docker to be installed). To run with docker execute `docker build .` from the project root.

## Results

The test run results are printed out to console. You will see statistics for failed and passed tests.
Also in case you run the tests from your machine (not with docker), you can find test report in html form at target/cucumber-reports.