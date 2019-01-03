package org.openhab.rulestest;

import static org.hamcrest.CoreMatchers.is;

import java.util.Arrays;
import java.util.List;

import org.eclipse.smarthome.core.library.types.OnOffType;
import org.junit.Test;
import org.openhab.rulestest.manipulator.TestSwitchItem;

public class ExampleTest extends RuleTest {
    private static final List<String> requestedItems = Arrays.asList("Test", "RuleTriggered");

    @Test
    public void changesToOnWhenOff() throws Exception {
        getSwitchItem("RuleTriggered").setState(OnOffType.OFF);

        TestSwitchItem.send(getSwitchItem("Test"), OnOffType.ON);

        assertThatItemState(getItem("RuleTriggered"), is(OnOffType.ON));
    }

    @Test
    public void changesToOffWhenOn() throws Exception {
        getSwitchItem("RuleTriggered").setState(OnOffType.ON);

        TestSwitchItem.send(getSwitchItem("Test"), OnOffType.OFF);

        assertThatItemState(getItem("RuleTriggered"), is(OnOffType.OFF));
    }

    @Override
    protected List<String> requestedItems() {
        return requestedItems;
    }
}
