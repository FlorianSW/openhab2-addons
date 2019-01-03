# openHAB Integration test - rulestest

The `rulestest` package allows a user to add JUnit like tests to interact with the openHAB event bus and, using it, test the functioning of the provided rules and items.
You can find an example test (to see how such a test might look like) in this package in `ExampleTest.java`.
This test requires you to have an openHAB configuration folder with at lest two items and one rule as shown below:

The rule need to look like:

```
rule "Trigger Switch"
when
    Item Test received command
then
    if (RuleTriggered.state == ON) {
        RuleTriggered.sendCommand(OFF);
    } else {
        RuleTriggered.sendCommand(ON);
    }
end
```

The items need to look like:

```
Switch Test
Switch RuleTriggered
```

## Usage

This package is not meant to be used directly, instead, copy it's contents into an openHAB development area of your choice and write your tests there.
You can then run the provided launch configuration to run the tests contained in this module.
