package nl.duckstudios.pintandpillage.entity;

import nl.duckstudios.pintandpillage.helper.ResourceManager;
import nl.duckstudios.pintandpillage.mocks.MockStorage;
import nl.duckstudios.pintandpillage.model.ResourceType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@ExtendWith(MockitoExtension.class)
@Tag("Storage")
public class TestStorage {

    @Mock
    private Village villageMock;
    private MockStorage storageUnderTesting;
    @Mock
    private ResourceManager mockResourceManger = new ResourceManager();

    @BeforeEach
    public void initStorageUnderTesting(){
        this.storageUnderTesting = new MockStorage();
        this.storageUnderTesting.setVillage(villageMock);
        this.storageUnderTesting.setResourceManager(this.mockResourceManger);
    }

    @Test
    public void should_CostMoreStone_when_UpdateBuildingIsCalledAtAHigherLevel() {
        int expectedInitialStoneRequired = 625;
        int expectedStoneRequiredPostUpdateBuilding = 1250;
        assertThat(this.storageUnderTesting.getResourcesRequiredLevelUp().get(ResourceType.Stone.name()), is(expectedInitialStoneRequired));
        // normally the level is updated after the village updates
        this.storageUnderTesting.setLevel(1);
        this.storageUnderTesting.updateBuilding();
        assertThat(this.storageUnderTesting.getResourcesRequiredLevelUp().get(ResourceType.Stone.name()), is(expectedStoneRequiredPostUpdateBuilding));

    }


    @Test
    public void should_HaveALongerConstructionTimeInSeconds_when_UpdateBuildingIsCalledAtAHigherLevel() {
        LocalTime expectedInitialConstructionTimeInSeconds = LocalTime.of(0, 0, 0).plusSeconds(6);
        LocalTime expectedConstructionTimeInSecondsPostUpdateBuilding = LocalTime.of(0, 0, 0).plusSeconds(36);
        assertThat(this.storageUnderTesting.getConstructionTime(), is(expectedInitialConstructionTimeInSeconds));
        // normally the level is updated after the village updates
        this.storageUnderTesting.setLevel(1);
        this.storageUnderTesting.updateBuilding();
        assertThat(this.storageUnderTesting.getConstructionTime(), is(expectedConstructionTimeInSecondsPostUpdateBuilding));
    }
}
