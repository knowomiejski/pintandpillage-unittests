package nl.duckstudios.pintandpillage.entity;

import nl.duckstudios.pintandpillage.helper.ResourceManager;
import nl.duckstudios.pintandpillage.mocks.MockResourceBuilding;
import nl.duckstudios.pintandpillage.model.ResourceType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.HashMap;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("ResourceBuilding")
public class TestResourceBuilding {

    @Mock
    private Village villageMock;

    @Mock
    private HashMap<String, Integer> mockedResources;

    private MockResourceBuilding resourceBuildingUnderTesting;

    @Mock
    private ResourceManager mockResourceManger = new ResourceManager();

    @BeforeEach
    public void setupResourceBuildingTests () {
        this.resourceBuildingUnderTesting = new MockResourceBuilding();
        this.resourceBuildingUnderTesting.setVillage(villageMock);
        this.resourceBuildingUnderTesting.setGeneratesResource(ResourceType.Wood);
        this.resourceBuildingUnderTesting.setResourceManager(mockResourceManger);
    }

    @Test
    public void should_ReturnBeforeAddingResources_when_CollectResourcesIsCalledAndLastCollectedIsLaterThanTheCurrentTime(){
        LocalDateTime timeNow = LocalDateTime.now();
        LocalDateTime mockTimeAfterNow = timeNow.plusHours(1);
        this.resourceBuildingUnderTesting.setLastCollected(mockTimeAfterNow);
        this.resourceBuildingUnderTesting.collectResources();
        verify(this.mockResourceManger, times(0)).addResources(any(), anyInt(), anyString());
    }

    @Test
    public void should_ReturnBeforeAddingResources_when_ResourcesGeneratedIsZero(){
        LocalDateTime timeNow = LocalDateTime.now();
        LocalDateTime timeBeforeNow = timeNow.minusHours(1);
        this.resourceBuildingUnderTesting.setLastCollected(timeBeforeNow);
        this.resourceBuildingUnderTesting.setResourcesPerHour(0);
        this.resourceBuildingUnderTesting.collectResources();
        verify(this.mockResourceManger, times(0)).addResources(any(), anyInt(), anyString());
    }

    @Test
    public void should_SubtractResources_when_RequiresResourcesIsPresent(){
        when(this.villageMock.getVillageResources()).thenReturn(mockedResources);
        when(this.mockedResources.get(any())).thenReturn(999);
        LocalDateTime timeNow = LocalDateTime.now();
        LocalDateTime timeBeforeNow = timeNow.minusHours(1);
        this.resourceBuildingUnderTesting.setLastCollected(timeBeforeNow);
        this.resourceBuildingUnderTesting.setResourcesPerHour(1);
        this.resourceBuildingUnderTesting.setRequiresResources(ResourceType.Stone);
        this.resourceBuildingUnderTesting.collectResources();
        verify(this.mockResourceManger, times(1)).subtractResources(any(),any());
    }

    @Test
    public void should_NotSubtractResources_when_RequiresResourcesIsNotPresent(){
        LocalDateTime timeNow = LocalDateTime.now();
        LocalDateTime timeBeforeNow = timeNow.minusHours(1);
        this.resourceBuildingUnderTesting.setLastCollected(timeBeforeNow);
        this.resourceBuildingUnderTesting.setResourcesPerHour(1);
        this.resourceBuildingUnderTesting.collectResources();
        verify(this.mockResourceManger, times(0)).subtractResources(any(),any());
    }

    @Test
    public void should_AssignLastCollected_when_theVillageUpdatesItsState(){
        LocalDateTime timeNow = LocalDateTime.now();
        LocalDateTime timeBeforeNow = timeNow.minusHours(1);
        this.resourceBuildingUnderTesting.setLastCollected(timeBeforeNow);
        this.resourceBuildingUnderTesting.setLevelupFinishedTime(timeNow);
        this.resourceBuildingUnderTesting.updateVillageState();
        assertThat(this.resourceBuildingUnderTesting.getLastCollected(), is(timeNow));
    }

}
