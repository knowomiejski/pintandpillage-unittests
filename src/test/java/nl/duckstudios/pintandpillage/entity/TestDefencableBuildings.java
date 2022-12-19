package nl.duckstudios.pintandpillage.entity;

import nl.duckstudios.pintandpillage.helper.ResourceManager;
import nl.duckstudios.pintandpillage.mocks.MockDefenceTower;
import nl.duckstudios.pintandpillage.mocks.MockHouse;
import nl.duckstudios.pintandpillage.mocks.MockWall;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@ExtendWith(MockitoExtension.class)
@Tag("DefencableBuilding")
public class TestDefencableBuildings {
    @Mock
    private Village villageMock;
    private MockWall wallUnderTesting;
    private MockDefenceTower defenceTowerUnderTesting;
    @Mock
    private ResourceManager mockResourceManger = new ResourceManager();

    @BeforeEach
    public void initLumberyardUnderTesting() {
        this.wallUnderTesting = new MockWall();
        this.wallUnderTesting.setVillage(villageMock);
        this.wallUnderTesting.setResourceManager(this.mockResourceManger);

        this.defenceTowerUnderTesting = new MockDefenceTower();
        this.defenceTowerUnderTesting.setVillage(villageMock);
        this.defenceTowerUnderTesting.setResourceManager(this.mockResourceManger);
    }

    @Test
    public void should_GiveMoreDefencePoints_when_BuildingHasLeveledUpOnWall() {
        int expectedInitialDefencePoints = 51;
        int expectedDefencePointsPostLevelUp = 76;
        assertThat(this.wallUnderTesting.getDefenceBonus(), is(expectedInitialDefencePoints));
        this.wallUnderTesting.setLevel(1);
        this.wallUnderTesting.updateBuilding();
        assertThat(this.wallUnderTesting.getDefenceBonus(), is(expectedDefencePointsPostLevelUp));
    }

    @Test
    public void should_GiveMoreDefencePoints_when_BuildingHasLeveledUpOnDefenceTower() {
        int expectedInitialDefencePoints = 100;
        int expectedDefencePointsPostLevelUp = 175;
        assertThat(this.defenceTowerUnderTesting.getDefenceBonus(), is(expectedInitialDefencePoints));
        this.defenceTowerUnderTesting.setLevel(1);
        this.defenceTowerUnderTesting.updateBuilding();
        assertThat(this.defenceTowerUnderTesting.getDefenceBonus(), is(expectedDefencePointsPostLevelUp));
    }
}
