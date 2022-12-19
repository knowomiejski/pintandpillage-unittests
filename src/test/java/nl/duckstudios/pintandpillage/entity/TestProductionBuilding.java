package nl.duckstudios.pintandpillage.entity;

import nl.duckstudios.pintandpillage.Exceptions.ProductionConditionsNotMetException;
import nl.duckstudios.pintandpillage.entity.production.Axe;
import nl.duckstudios.pintandpillage.entity.production.Shield;
import nl.duckstudios.pintandpillage.entity.production.Spear;
import nl.duckstudios.pintandpillage.entity.production.Unit;
import nl.duckstudios.pintandpillage.helper.ResourceManager;
import nl.duckstudios.pintandpillage.mocks.MockBarracks;
import nl.duckstudios.pintandpillage.mocks.MockHarbor;
import nl.duckstudios.pintandpillage.mocks.MockProductionBuilding;
import nl.duckstudios.pintandpillage.mocks.MockVillage;
import nl.duckstudios.pintandpillage.model.ResearchType;
import nl.duckstudios.pintandpillage.model.ResourceType;
import nl.duckstudios.pintandpillage.testHelpers.ResearchHelper;
import nl.duckstudios.pintandpillage.testHelpers.ResourceHelper;
import nl.duckstudios.pintandpillage.testHelpers.UnitHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Tag("ProductionBuilding")
public class TestProductionBuilding {

    @Mock
    private Village villageMock;

    private ResourceHelper resourceHelper = new ResourceHelper();

    private MockProductionBuilding productionBuildingUnderTesting;

    private MockBarracks barracksUnderTesting;

    private MockHarbor harborUnderTesting;

    private UnitHelper unitHelper = new UnitHelper();

    private ResearchHelper researchHelper = new ResearchHelper();

    private Unit shieldUnit = new Shield();

    private Unit spearUnit = new Spear();

    private Unit axeUnit = new Axe();

    @Mock
    private ResourceManager mockResourceManger = new ResourceManager();

    public void setupVillageStub(){
        this.productionBuildingUnderTesting.setResourcesRequiredLevelUp(this.resourceHelper.generateResource(ResourceType.Stone, 1));
        this.productionBuildingUnderTesting.setVillage(villageMock);
        this.productionBuildingUnderTesting.setLevel(4);
        this.productionBuildingUnderTesting.setResourceManager(this.mockResourceManger);

        when(this.villageMock.getCompletedResearches()).thenReturn(this.researchHelper.generateResearchList());
    }

    @BeforeEach
    public void initMockProductionBuilding () {
        this.spearUnit.setResearchRequired(ResearchType.Spear);
        this.productionBuildingUnderTesting = new MockProductionBuilding();
        this.productionBuildingUnderTesting.setUnitsUnlockedAtLevel(this.unitHelper.generateUnitsUnlockedAtList());
        this.productionBuildingUnderTesting.setVillage(villageMock);
    }

    @Test
    public void should_ThrowProductionConditionsNotMetExceptionWithBuildingCantProduceUnit_when_AUnitIsNotUnlocked(){
        ProductionConditionsNotMetException thrown = assertThrows( ProductionConditionsNotMetException.class,
                () -> this.productionBuildingUnderTesting.produceUnit(shieldUnit, 1));
        assertThat(thrown.getMessage(), is("Building can't produce unit"));
    }

    @Test
    void should_ThrowProductionConditionsNotMetExceptionWithUnitNotResearched_when_AUnitIsNotResearched(){
        this.setupVillageStub();
        ProductionConditionsNotMetException thrown = assertThrows( ProductionConditionsNotMetException.class,
                () -> this.productionBuildingUnderTesting.produceUnit(spearUnit, 1));
        assertThat(thrown.getMessage(), is("Unit not researched"));
    }

    @Test
    public void should_ThrowProductionConditionsNotMetExceptionWithNotEnoughPopulation_when_AUnitCostsMoreThanTheCurrentPopulation(){
        this.setupVillageStub();
        when(this.villageMock.hasEnoughPopulation(anyInt(), eq(999))).thenReturn(false);
        ProductionConditionsNotMetException thrown = assertThrows( ProductionConditionsNotMetException.class,
                () -> this.productionBuildingUnderTesting.produceUnit(axeUnit, 999));
        assertThat(thrown.getMessage(), is("Not enough population"));
    }

    @Test
    public void should_ThrowProductionConditionsNotMetExceptionWithNotEnoughResources_when_ThereAreNotEnoughResources(){
        this.setupVillageStub();
        when(this.villageMock.hasEnoughPopulation(anyInt(), eq(1))).thenReturn(true);
        when(this.mockResourceManger.hasEnoughResourcesAvailable(any(), any(), anyInt())).thenReturn(false);
        ProductionConditionsNotMetException thrown = assertThrows( ProductionConditionsNotMetException.class,
                () -> this.productionBuildingUnderTesting.produceUnit(axeUnit, 1));
        assertThat(thrown.getMessage(), is("Not enough resources for all the units"));
    }

    @Test
    public void should_SubtractResources_when_AllProductionConditionsAreMet(){
        this.setupVillageStub();
        when(this.villageMock.hasEnoughPopulation(anyInt(), eq(1))).thenReturn(true);
        when(this.mockResourceManger.hasEnoughResourcesAvailable(any(), any(), anyInt())).thenReturn(true);
        this.productionBuildingUnderTesting.produceUnit(axeUnit, 1);
        verify(this.mockResourceManger, times(1)).subtractResources(any(), any());
    }

    @Test
    public void should_IncreaseQueueLimitByOneEveryFiveLevels_when_TheBarracksBuildingLevelsUp() {
        this.barracksUnderTesting = new MockBarracks();
        this.barracksUnderTesting.setLevel(0);
        int expectedInitialQueueLimit = this.barracksUnderTesting.getQueueLimit();

        assertThat(this.barracksUnderTesting.getQueueLimit(), is(expectedInitialQueueLimit));
        for (int i = 0; i < 6; i++) {
            this.barracksUnderTesting.updateBuilding();
        }
        assertThat(this.barracksUnderTesting.getQueueLimit(), greaterThan(expectedInitialQueueLimit));
    }
}
