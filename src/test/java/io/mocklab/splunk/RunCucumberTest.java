package io.mocklab.splunk;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
    plugin = {"pretty", "html:target/cucumber-reports"},
    features = "src/test/resources",
    monochrome = true
)
public class RunCucumberTest {
}