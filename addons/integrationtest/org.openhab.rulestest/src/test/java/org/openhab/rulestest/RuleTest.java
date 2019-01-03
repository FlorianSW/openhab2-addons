package org.openhab.rulestest;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.smarthome.core.items.Item;
import org.eclipse.smarthome.core.items.ItemRegistry;
import org.eclipse.smarthome.core.library.items.SwitchItem;
import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.types.State;
import org.eclipse.smarthome.model.core.ModelRepository;
import org.eclipse.smarthome.test.java.JavaOSGiTest;
import org.hamcrest.Matcher;
import org.junit.Before;

public abstract class RuleTest extends JavaOSGiTest {
    private static final String startupRule = "rule \"Integration Test startup rule\"\n" + "when\n"
            + "    System started\n" + "then\n" + "    startupFinished.sendCommand(ON)\n" + "end";
    private static final String startupItem = "Switch startupFinished";
    private ItemRegistry itemRegistry;

    @Before
    public void setUp() {
        itemRegistry = getService(ItemRegistry.class);
        assertNotNull(itemRegistry);
        injectTestModels();

        List<String> requiredItems = new ArrayList<>();
        requiredItems.add("startupFinished");
        requiredItems.addAll(requestedItems());

        waitForAssert(() -> requiredItems.forEach(
                item -> assertThat("Requested item '" + item + "' does not exist!", getItem(item), not(nullValue()))));
        waitForAssert(() -> assertThat(getItem("startupFinished").getState(), is(OnOffType.ON)));
    }

    private void injectTestModels() {
        ModelRepository modelRepository = getService(ModelRepository.class);
        assertNotNull(modelRepository);

        modelRepository.addOrRefreshModel("startup_finish_test.rules",
                new ByteArrayInputStream(startupRule.getBytes()));
        modelRepository.addOrRefreshModel("startup_finish_test.items",
                new ByteArrayInputStream(startupItem.getBytes()));
    }

    protected void assertThatItemState(Item item, Matcher<State> matcher) {
        waitForAssert(() -> assertThat(item.getState(), matcher), 1000, 50);
    }

    protected Item getItem(String itemName) {
        return itemRegistry.get(itemName);
    }

    protected SwitchItem getSwitchItem(String itemName) {
        Item item = getItem(itemName);
        assertThat(item, instanceOf(SwitchItem.class));

        return (SwitchItem) item;
    }

    protected List<String> requestedItems() {
        return Collections.emptyList();
    }
}
