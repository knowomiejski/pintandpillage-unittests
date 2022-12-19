package nl.duckstudios.pintandpillage.entity;

import nl.duckstudios.pintandpillage.helper.ResourceManager;
import nl.duckstudios.pintandpillage.mocks.MockHouse;
import nl.duckstudios.pintandpillage.mocks.MockSpearResearch;
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
@Tag("Research")
public class TestResearch {
    @Mock
    private Village villageMock;
    private MockSpearResearch spearResearchUnderTesting;
    @Mock
    private ResourceManager mockResourceManger = new ResourceManager();

    @BeforeEach
    public void initSpearResearchUnderTesting() {
        this.spearResearchUnderTesting = new MockSpearResearch();
    }

    @Test
    public void should_CostMoreResourcesToLevelUp_when_ResearchLevelIncreases() {
//        System.out.println(this.spearResearchUnderTesting.toString());
        int expectedInitialWoodCost = 0;
        int expectedWoodCostPostLevelUp = 80;
        int expectedInitialStoneCost = 150;
        int expectedStoneCostPostLevelUp = 200;
        int expectedInitialBeerCost = 150;
        int expectedBeerCostPostLevelUp = 200;

        assertThat(this.spearResearchUnderTesting.getResourcesRequiredToResearch().get(ResourceType.Wood.name()), is(expectedInitialWoodCost));
        assertThat(this.spearResearchUnderTesting.getResourcesRequiredToResearch().get(ResourceType.Stone.name()), is(expectedInitialStoneCost));
        assertThat(this.spearResearchUnderTesting.getResourcesRequiredToResearch().get(ResourceType.Beer.name()), is(expectedInitialBeerCost));

        this.spearResearchUnderTesting.setResearchLevel(1);

        assertThat(this.spearResearchUnderTesting.getResourcesRequiredToResearch().get(ResourceType.Wood.name()), is(expectedWoodCostPostLevelUp));
        assertThat(this.spearResearchUnderTesting.getResourcesRequiredToResearch().get(ResourceType.Stone.name()), is(expectedStoneCostPostLevelUp));
        assertThat(this.spearResearchUnderTesting.getResourcesRequiredToResearch().get(ResourceType.Beer.name()), is(expectedBeerCostPostLevelUp));
    }
}
