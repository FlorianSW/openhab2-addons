package org.openhab.rulestest;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.smarthome.core.items.Item;
import org.eclipse.smarthome.core.items.ItemRegistry;
import org.eclipse.smarthome.core.library.items.NumberItem;
import org.eclipse.smarthome.core.library.items.SwitchItem;
import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.thing.link.ItemChannelLink;
import org.eclipse.smarthome.core.thing.link.ItemChannelLinkRegistry;
import org.eclipse.smarthome.core.types.State;
import org.eclipse.smarthome.model.core.ModelRepository;
import org.eclipse.smarthome.test.java.JavaOSGiTest;
import org.hamcrest.Matcher;
import org.junit.Before;

public abstract class RuleTest extends JavaOSGiTest {
    private static final String startupRule = "rule \"Integration Test startup rule\"\n" + "when\n"
            + "    System started\n" + "then\n" + "    startupFinished.sendCommand(ON)\n" + "end";
    private static final String startupItem = "Switch startupFinished";
    protected ItemRegistry itemRegistry;

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
        waitForAssert(() -> assertThat(getItem("startupFinished").getState(), is(OnOffType.ON)), 60000, 100);

        disableItemChannelAutoupdate();
    }

	private void disableItemChannelAutoupdate() {
		ItemChannelLinkRegistry itemChannelLinkRegistry = getService(ItemChannelLinkRegistry.class);
        Collection<ItemChannelLink> itemChannelLinks = itemChannelLinkRegistry.getAll();
        for (ItemChannelLink itemChannelLink : itemChannelLinks) {
        	System.out.println("Removing item channel link:" + itemChannelLink.getUID());
        	// WTF - the ItemChannelLink is returned from getAll and #get() returns the same item, but remove returns null as if the item does not exist
        	// working around this stupidness by adding the ItemChannelLink before removing it, even if that sounds stupid.
        	itemChannelLinkRegistry.add(itemChannelLink);
        	itemChannelLinkRegistry.remove(itemChannelLink.getUID());
        }
        assertTrue(itemChannelLinkRegistry.getAll().isEmpty());
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
    
    protected NumberItem getNumberItem(String itemName) {
    	Item item = getItem(itemName);
    	assertThat(item, instanceOf(NumberItem.class));
    	
    	return (NumberItem) item;
    }

    protected List<String> requestedItems() {
        return Collections.emptyList();
    }
}
