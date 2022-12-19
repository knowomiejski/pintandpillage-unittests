package nl.duckstudios.pintandpillage.entity;

import nl.duckstudios.pintandpillage.Exceptions.SettleConditionsNotMetException;
import nl.duckstudios.pintandpillage.entity.buildings.Lumberyard;
import nl.duckstudios.pintandpillage.helper.ResourceManager;
import nl.duckstudios.pintandpillage.mocks.MockHouse;
import nl.duckstudios.pintandpillage.mocks.MockLubmeryard;
import nl.duckstudios.pintandpillage.mocks.MockVillage;
import nl.duckstudios.pintandpillage.testHelpers.VillagesHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
@Tag("Village")
public class TestVillage {

    private MockLubmeryard lubmeryardMock;

    @Mock
    private ResourceManager mockResourceManger = new ResourceManager();

    private VillagesHelper villagesHelper = new VillagesHelper();

    @Test
    public void should_ThrowAnException_when_TheStorageOfAVillageIsFull() {
        MockVillage initialGeneratedVillage = this.villagesHelper.generateMockVillage(1);
        MockVillage villageUnderTesting = this.villagesHelper.setupVillageWithStorage(initialGeneratedVillage);

        Map<String, Integer> villageResources = new HashMap<>();
        villageResources.put("Wood", 10000);
        villageResources.put("Stone", 10000);
        villageResources.put("Beer", 10000);
        villageUnderTesting.setVillageResources(villageResources);
        Exception expectedException = new Exception("The storage is already full!");
        Exception thrownException = assertThrows(expectedException.getClass(), () -> villageUnderTesting.updateVillageState());
        assertThat(thrownException.getMessage(), is(expectedException.getMessage()));
    }

}
