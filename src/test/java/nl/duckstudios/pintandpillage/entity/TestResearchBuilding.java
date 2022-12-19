package nl.duckstudios.pintandpillage.entity;

import nl.duckstudios.pintandpillage.Exceptions.ResearchConditionsNotMetException;
import nl.duckstudios.pintandpillage.entity.researching.Research;
import nl.duckstudios.pintandpillage.helper.ResourceManager;
import nl.duckstudios.pintandpillage.mocks.*;
import nl.duckstudios.pintandpillage.model.ResourceType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("Research")
public class TestResearchBuilding {

    @Mock
    private MockVillage villageMock;

    private MockResearch researchMock;

    private MockSpearResearch spearResearchUnderTesting;

    private MockResearchBuilding researchBuildingUnderTesting;

    @Mock
    private ResourceManager mockResourceManger = new ResourceManager();

    @BeforeEach
    public void setupResearchBuildingTests() {
        this.villageMock = new MockVillage();
        this.researchMock = new MockResearch();
        this.researchBuildingUnderTesting = new MockResearchBuilding();
        this.researchBuildingUnderTesting.setResourceManager(mockResourceManger);
        this.researchBuildingUnderTesting.setVillage(villageMock);
        this.researchBuildingUnderTesting.setLevel(5);

        this.spearResearchUnderTesting = new MockSpearResearch();
    }

    @Test
    public void should_ThrowResearchConditionsNotMetExceptionWithResearchInProgressMessage_when_AResearchIsAlreadyInProgress() {
        this.researchBuildingUnderTesting.setResearchInProgress(true);
        ResearchConditionsNotMetException thrown = assertThrows(ResearchConditionsNotMetException.class,
                () -> this.researchBuildingUnderTesting.startResearch(this.researchMock));
        assertThat(thrown.getMessage(), is("A research is already in progress"));
    }

    @Test
    public void should_ThrowResearchConditionsNotMetExceptionWithNoResourcesMessage_when_AVillageHasNotEnoughResources() {
        this.researchBuildingUnderTesting.setResearchInProgress(false);
        when(this.mockResourceManger.hasEnoughResourcesAvailable(any(), any())).thenReturn(false);
        ResearchConditionsNotMetException thrown = assertThrows(ResearchConditionsNotMetException.class,
                () -> this.researchBuildingUnderTesting.startResearch(this.researchMock));
        assertThat(thrown.getMessage(), is("Not enough resources available"));
    }

    @Test
    public void should_ThrowResearchConditionsNotMetExceptionWithTooLowLevelMessage_when_AResearchBuildingIsOfATooLowLevel() {
        this.researchMock.setBuildingLevelRequirement(99);
        this.researchBuildingUnderTesting.setResearchInProgress(false);
        when(this.mockResourceManger.hasEnoughResourcesAvailable(any(), any())).thenReturn(true);
        ResearchConditionsNotMetException thrown = assertThrows(ResearchConditionsNotMetException.class,
                () -> this.researchBuildingUnderTesting.startResearch(this.researchMock));
        assertThat(thrown.getMessage(), is("Research building level is not high enough"));
    }

    @Test
    public void should_SubtractResources_when_AValidResearchIsStarted() {
        this.researchMock.setBuildingLevelRequirement(1);
        this.researchBuildingUnderTesting.setResearchInProgress(false);
        when(this.mockResourceManger.hasEnoughResourcesAvailable(any(), any())).thenReturn(true);
        this.researchBuildingUnderTesting.startResearch(this.researchMock);
        verify(this.mockResourceManger, times(1)).subtractResources(any(), any());
    }

    @Test
    public void should_TakeMoreTimeToResearch_when_TheResearchLevelIncreases() {
        LocalTime expectedInitialTimeToResearchInSeconds = (LocalTime.of(0, 0, 0).plusSeconds(this.spearResearchUnderTesting.getBaseSecondsToResearch()));
        when(this.mockResourceManger.hasEnoughResourcesAvailable(any(), any())).thenReturn(true);

        this.researchBuildingUnderTesting.setResearchInProgress(false);
        this.researchBuildingUnderTesting.startResearch(this.spearResearchUnderTesting);
        assertThat(this.researchBuildingUnderTesting.getCurrentResearch().getSecondsToResearch(), is(expectedInitialTimeToResearchInSeconds));

        this.researchBuildingUnderTesting.setLevel(99);
        this.researchBuildingUnderTesting.setResearchInProgress(false);
        this.researchBuildingUnderTesting.startResearch(this.spearResearchUnderTesting);
        assertThat(this.spearResearchUnderTesting.getSecondsToResearch(), greaterThan(expectedInitialTimeToResearchInSeconds));

    }

    @Test
    public void should_TakeLessTimeToResearch_when_TheResearchBuildingLevelIncreases() {
        LocalTime expectedInitialTimeToResearchInSeconds = (LocalTime.of(0, 0, 0).plusSeconds(this.spearResearchUnderTesting.getBaseSecondsToResearch()));
        when(this.mockResourceManger.hasEnoughResourcesAvailable(any(), any())).thenReturn(true);

        this.researchBuildingUnderTesting.setResearchInProgress(false);
        this.researchBuildingUnderTesting.startResearch(this.spearResearchUnderTesting);
        assertThat(this.researchBuildingUnderTesting.getCurrentResearch().getSecondsToResearch(), is(expectedInitialTimeToResearchInSeconds));

        this.researchBuildingUnderTesting.setResearchInProgress(false);
        this.researchBuildingUnderTesting.setLevel(99);
        this.researchBuildingUnderTesting.startResearch(this.spearResearchUnderTesting);
        assertThat(this.spearResearchUnderTesting.getSecondsToResearch(), lessThan(expectedInitialTimeToResearchInSeconds));

    }

    @Test
    public void should_CostMoreResourcesToLevelUp_when_ResearchLevelIncreases() {
        //Simulates the first ever research
        this.researchBuildingUnderTesting.setCurrentResearch(this.spearResearchUnderTesting);
        this.researchBuildingUnderTesting.setResearchInProgress(true);
        this.researchBuildingUnderTesting.setCurrentResearchFinishTime(LocalDateTime.MIN);
        this.researchBuildingUnderTesting.updateVillageState();

        int expectedInitialWoodCost = 0;
        int expectedWoodCostPostLevelUp = 80;
        int expectedInitialStoneCost = 150;
        int expectedStoneCostPostLevelUp = 200;
        int expectedInitialBeerCost = 150;
        int expectedBeerCostPostLevelUp = 200;

        assertThat(this.spearResearchUnderTesting.getResourcesRequiredToResearch().get(ResourceType.Wood.name()), is(expectedInitialWoodCost));
        assertThat(this.spearResearchUnderTesting.getResourcesRequiredToResearch().get(ResourceType.Stone.name()), is(expectedInitialStoneCost));
        assertThat(this.spearResearchUnderTesting.getResourcesRequiredToResearch().get(ResourceType.Beer.name()), is(expectedInitialBeerCost));

        //Simulates the second research
        this.researchBuildingUnderTesting.setCurrentResearch(this.spearResearchUnderTesting);
        this.researchBuildingUnderTesting.setResearchInProgress(true);
        this.researchBuildingUnderTesting.setCurrentResearchFinishTime(LocalDateTime.MIN);
        this.researchBuildingUnderTesting.updateVillageState();


        assertThat(this.spearResearchUnderTesting.getResourcesRequiredToResearch().get(ResourceType.Wood.name()), is(expectedWoodCostPostLevelUp));
        assertThat(this.spearResearchUnderTesting.getResourcesRequiredToResearch().get(ResourceType.Stone.name()), is(expectedStoneCostPostLevelUp));
        assertThat(this.spearResearchUnderTesting.getResourcesRequiredToResearch().get(ResourceType.Beer.name()), is(expectedBeerCostPostLevelUp));
    }
}
