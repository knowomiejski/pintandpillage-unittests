package nl.duckstudios.pintandpillage.entity;

import nl.duckstudios.pintandpillage.helper.ResourceManager;
import nl.duckstudios.pintandpillage.mocks.MockLubmeryard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@ExtendWith(MockitoExtension.class)
@Tag("Lumberyard")
public class TestLumberyard {

    @Mock
    private Village villageMock;
    private MockLubmeryard lumberyardUnderTesting;
    @Mock
    private ResourceManager mockResourceManger = new ResourceManager();

    @BeforeEach
    public void initLumberyardUnderTesting(){
        this.lumberyardUnderTesting = new MockLubmeryard();
        this.lumberyardUnderTesting.setVillage(villageMock);
        this.lumberyardUnderTesting.setResourceManager(this.mockResourceManger);
    }

    @Test
    void should_ProduceMoreResources_when_TheBuildingLeveledUp() {
        int expectedInitialProducedWood = 20;
        int expectedProducedWoodPostLevelUp = 32;

        assertThat(this.lumberyardUnderTesting.getResourcesPerHour(), is(expectedInitialProducedWood));
        // normally the level is updated after the village updates
        this.lumberyardUnderTesting.setLevel(1);
        this.lumberyardUnderTesting.updateBuilding();
        assertThat(this.lumberyardUnderTesting.getResourcesPerHour(), is(expectedProducedWoodPostLevelUp));
    }
}
