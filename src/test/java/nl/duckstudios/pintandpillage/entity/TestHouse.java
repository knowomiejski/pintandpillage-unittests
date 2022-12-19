package nl.duckstudios.pintandpillage.entity;


import nl.duckstudios.pintandpillage.helper.ResourceManager;
import nl.duckstudios.pintandpillage.mocks.MockHouse;
import nl.duckstudios.pintandpillage.model.ResourceType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@ExtendWith(MockitoExtension.class)
@Tag("House")
public class TestHouse {
    @Mock
    private Village villageMock;
    private MockHouse houseUnderTesting;
    @Mock
    private ResourceManager mockResourceManger = new ResourceManager();

    @BeforeEach
    public void initLumberyardUnderTesting() {
        this.houseUnderTesting = new MockHouse();
        this.houseUnderTesting.setVillage(villageMock);
        this.houseUnderTesting.setResourceManager(this.mockResourceManger);
    }

    @Test
    public void should_IncreasePopulationCapacity_when_TheBuildingLeveledUp() {
        int expectedInitialPopulationCapacity = 0;
        int expectedPopulationCapacityPostLevelUp = 18;
        assertThat(this.houseUnderTesting.getPopulationCapacity(), is(expectedInitialPopulationCapacity));
        // normally the level is updated after the village updates
        this.houseUnderTesting.setLevel(1);
        this.houseUnderTesting.updateBuilding();
        assertThat(this.houseUnderTesting.getPopulationCapacity(), is(expectedPopulationCapacityPostLevelUp));
    }

    @Test
    public void should_CostNoPopulation_when_TheBuildingLeveledUp() {
        int expectedPopulationRequirementPreAndPostLevelUp = 0;
        assertThat(this.houseUnderTesting.getPopulationRequiredNextLevel(), is(expectedPopulationRequirementPreAndPostLevelUp));
        this.houseUnderTesting.setLevel(90);
        this.houseUnderTesting.updateBuilding();
        assertThat(this.houseUnderTesting.getPopulationRequiredNextLevel(), is(expectedPopulationRequirementPreAndPostLevelUp));
    }
}
