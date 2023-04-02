package nl.duckstudios.pintandpillage.service;

import nl.duckstudios.pintandpillage.Exceptions.BuildingConditionsNotMetException;
import nl.duckstudios.pintandpillage.Exceptions.SettleConditionsNotMetException;
import nl.duckstudios.pintandpillage.dao.VillageDAO;
import nl.duckstudios.pintandpillage.dao.VillageDataMapper;
import nl.duckstudios.pintandpillage.entity.Coord;
import nl.duckstudios.pintandpillage.entity.Village;
import nl.duckstudios.pintandpillage.entity.VillageUnit;
import nl.duckstudios.pintandpillage.entity.WorldMap;
import nl.duckstudios.pintandpillage.entity.buildings.Building;
import nl.duckstudios.pintandpillage.entity.researching.Research;
import nl.duckstudios.pintandpillage.entity.travels.AttackCombatTravel;
import nl.duckstudios.pintandpillage.entity.travels.ReturningCombatTravel;
import nl.duckstudios.pintandpillage.helper.BuildingFactory;
import nl.duckstudios.pintandpillage.helper.ResourceManager;
import nl.duckstudios.pintandpillage.mocks.MockVillage;
import nl.duckstudios.pintandpillage.mocks.MockVillageService;
import nl.duckstudios.pintandpillage.model.ResourceType;
import nl.duckstudios.pintandpillage.testHelpers.ResearchHelper;
import nl.duckstudios.pintandpillage.testHelpers.UnitHelper;
import nl.duckstudios.pintandpillage.testHelpers.VillagesHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("VillageService")
public class TestVillageService {

    @Mock
    VillageDAO villageDAOMock;

    @Mock
    VillageDataMapper villageDataMapperMock;

    @Mock
    ResourceManager resourceManagerMock;

    @Mock
    WorldService worldServiceMock;

    @Mock
    DistanceService distanceServiceMock;

    @Mock
    WorldMap worldMap;

    BuildingFactory buildingFactory = new BuildingFactory();

    UnitHelper unitHelper = new UnitHelper();

    ResearchHelper researchHelper = new ResearchHelper();

    VillagesHelper villagesHelper = new VillagesHelper();

    MockVillage villageMock;

    MockVillageService villageServiceUnderTesting;

    @BeforeEach
    void initialize() {
        this.villageDataMapperMock = mock(VillageDataMapper.class, withSettings().useConstructor(this.villageDAOMock));
        this.villageServiceUnderTesting = new MockVillageService(this.villageDataMapperMock, this.resourceManagerMock, this.worldServiceMock, this.distanceServiceMock);
        this.villageMock = new MockVillage();
        this.villageMock.setVillageUnit(unitHelper.generateVillageUnit("Shield", 1));
    }

    private void mockVillageDAO () {
        when(this.villageDAOMock.save(any())).thenAnswer(a -> a.getArgument(0));
    }
    // updateCombatState
    @Test
    public void should_NotCallFightOrReturnUnits_when_AllTravelsAreBeforeTheCurrentDateTime() {
        MockVillageService villageServiceUnderTesting = mock(MockVillageService.class, withSettings().useConstructor(this.villageDataMapperMock, this.resourceManagerMock, this.worldServiceMock, this.distanceServiceMock));
        MockVillage mockAttackingVillage = this.villagesHelper.generateMockVillage(1);
        MockVillage mockDefendingVillage = this.villagesHelper.generateMockVillage(2);
        AttackCombatTravel attackCombatTravel = new AttackCombatTravel();
        attackCombatTravel.setAttackingVillage(mockAttackingVillage);
        attackCombatTravel.setDefendingVillage(mockDefendingVillage);
        attackCombatTravel.setTimeOfArrival(LocalDateTime.now().plusHours(10));
        List<AttackCombatTravel> outgoingCombatTravels = new ArrayList<>();
        outgoingCombatTravels.add(attackCombatTravel);
        mockAttackingVillage.setOutgoingAttacks(outgoingCombatTravels);
        doCallRealMethod().when(villageServiceUnderTesting).updateCombatState(any());
        villageServiceUnderTesting.updateCombatState(mockAttackingVillage);
        int expectedTimesCalled = 0;
        verify(villageServiceUnderTesting, times(expectedTimesCalled)).fight(any());
        verify(villageServiceUnderTesting, times(expectedTimesCalled)).returnUnits(any(), any());
    }

    @Test
    public void should_CallFight_when_TheVillageHasAnAttackCombatTravelLaterThanTheCurrentDateTime() {
        MockVillageService villageServiceUnderTesting = mock(MockVillageService.class, withSettings().useConstructor(this.villageDataMapperMock, this.resourceManagerMock, this.worldServiceMock, this.distanceServiceMock));
        MockVillage mockAttackingVillage = this.villagesHelper.generateMockVillage(1);
        MockVillage mockDefendingVillage = this.villagesHelper.generateMockVillage(2);
        AttackCombatTravel attackCombatTravel = new AttackCombatTravel();
        attackCombatTravel.setAttackingVillage(mockAttackingVillage);
        attackCombatTravel.setDefendingVillage(mockDefendingVillage);
        attackCombatTravel.setTimeOfArrival(LocalDateTime.now().minusSeconds(10));
        List<AttackCombatTravel> outgoingCombatTravels = new ArrayList<>();
        outgoingCombatTravels.add(attackCombatTravel);
        mockAttackingVillage.setOutgoingAttacks(outgoingCombatTravels);
        doCallRealMethod().when(villageServiceUnderTesting).updateCombatState(any());
        villageServiceUnderTesting.updateCombatState(mockAttackingVillage);
        int expectedTimesFightCalled = 1;
        int expectedTimesReturnCalled = 0;
        verify(villageServiceUnderTesting, times(expectedTimesFightCalled)).fight(any());
        verify(villageServiceUnderTesting, times(expectedTimesReturnCalled)).returnUnits(any(), any());
    }

    @Test
    public void should_CallReturnUnits_when_TheVillageHasAnReturningCombatTravelLaterThanTheCurrentDateTime() {
        MockVillageService villageServiceUnderTesting = mock(MockVillageService.class, withSettings().useConstructor(this.villageDataMapperMock, this.resourceManagerMock, this.worldServiceMock, this.distanceServiceMock));
        MockVillage mockAttackingVillage = this.villagesHelper.generateMockVillage(1);
        ReturningCombatTravel returningCombatTravel = new ReturningCombatTravel();
        returningCombatTravel.setReturningToVillage(mockAttackingVillage);
        returningCombatTravel.setTimeOfArrival(LocalDateTime.now().minusSeconds(10));
        List<ReturningCombatTravel> returningCombatTravels = new ArrayList<>();
        returningCombatTravels.add(returningCombatTravel);
        mockAttackingVillage.setReturningCombatTravels(returningCombatTravels);
        doCallRealMethod().when(villageServiceUnderTesting).updateCombatState(any());
        villageServiceUnderTesting.updateCombatState(mockAttackingVillage);
        int expectedTimesFightCalled = 0;
        int expectedTimesReturnCalled = 1;
        verify(villageServiceUnderTesting, times(expectedTimesFightCalled)).fight(any());
        verify(villageServiceUnderTesting, times(expectedTimesReturnCalled)).returnUnits(any(), any());
    }


    // attack
    @Test
    public void should_CallLose_when_TheDifferenceIsInFavorOfTheAttacker() {
        MockVillageService villageServiceUnderTesting = mock(MockVillageService.class, withSettings().useConstructor(this.villageDataMapperMock, this.resourceManagerMock, this.worldServiceMock, this.distanceServiceMock));
        AttackCombatTravel attackCombatTravel = new AttackCombatTravel();
        MockVillage mockAttackingVillage = this.villagesHelper.generateMockVillage(1);
        // Shield units are level 1
        List<Research> researchList = this.researchHelper.generateResearchList();
        mockAttackingVillage.setCompletedResearches(researchList);
        MockVillage mockDefendingVillage = this.villagesHelper.generateMockVillage(2);
        mockDefendingVillage.setTotalDefence(111);
        attackCombatTravel.setAttackingVillage(mockAttackingVillage);
        attackCombatTravel.setDefendingVillage(mockDefendingVillage);

        // 20 level 1 shield units should generate: 5 * (1 + 0.1*10) * 10
        List<VillageUnit> villageUnitList = new ArrayList<>();
        VillageUnit villageUnits = unitHelper.generateVillageUnit("Shield", 10);
        villageUnitList.add(villageUnits);
        attackCombatTravel.setTravelingUnits(villageUnitList);

        doCallRealMethod().when(villageServiceUnderTesting).attack(any());
        villageServiceUnderTesting.attack(attackCombatTravel);
        verify(villageServiceUnderTesting).loseBattle(any(), eq(.5f));
    }

    @Test
    public void should_CallWinBattle_when_TheDifferenceIsInFavorOfTheAttacker() {
        MockVillageService villageServiceUnderTesting = mock(MockVillageService.class, withSettings().useConstructor(this.villageDataMapperMock, this.resourceManagerMock, this.worldServiceMock, this.distanceServiceMock));
        AttackCombatTravel attackCombatTravel = new AttackCombatTravel();
        MockVillage mockAttackingVillage = this.villagesHelper.generateMockVillage(1);
        // Shield units are level 1
        List<Research> researchList = this.researchHelper.generateResearchList();
        mockAttackingVillage.setCompletedResearches(researchList);
        MockVillage mockDefendingVillage = this.villagesHelper.generateMockVillage(2);
        mockDefendingVillage.setTotalDefence(27);
        attackCombatTravel.setAttackingVillage(mockAttackingVillage);
        attackCombatTravel.setDefendingVillage(mockDefendingVillage);

        // 20 level 1 shield units should generate: 5 * (1 + 0.1*10) * 10
        List<VillageUnit> villageUnitList = new ArrayList<>();
        VillageUnit villageUnits = unitHelper.generateVillageUnit("Shield", 10);
        villageUnitList.add(villageUnits);
        attackCombatTravel.setTravelingUnits(villageUnitList);

        doCallRealMethod().when(villageServiceUnderTesting).attack(any());
        villageServiceUnderTesting.attack(attackCombatTravel);
        verify(villageServiceUnderTesting).winBattle(any(), eq(.5f));
    }

    // win
    @Test
    public void should_PillageFiveHundredWoodFiveHundredStoneZeroBeer_when_TheVDefendingVillageHadOneThousandWoodOneThousandStoneAndOneBeer () {
        MockVillageService villageServiceUnderTesting = mock(MockVillageService.class, withSettings().useConstructor(this.villageDataMapperMock, this.resourceManagerMock, this.worldServiceMock, this.distanceServiceMock));
        AttackCombatTravel attackCombatTravel = new AttackCombatTravel();

        MockVillage mockAttackingVillage = this.villagesHelper.generateMockVillage(1);
        MockVillage mockDefendingVillage = this.villagesHelper.generateMockVillage(2);
        Map<String, Integer> defendingVillageResources = new HashMap<>();
        defendingVillageResources.put("Wood", 1000);
        defendingVillageResources.put("Stone", 1000);
        defendingVillageResources.put("Beer", 1);
        mockDefendingVillage.setVillageResources(defendingVillageResources);

        attackCombatTravel.setAttackingVillage(mockAttackingVillage);
        attackCombatTravel.setDefendingVillage(mockDefendingVillage);
        List<VillageUnit> villageUnitList = new ArrayList<>();
        VillageUnit transportShips = unitHelper.generateVillageUnit("TransportShip", 2);
        VillageUnit battleShip = unitHelper.generateVillageUnit("BattleShip", 1);
        villageUnitList.add(transportShips);
        villageUnitList.add(battleShip);
        attackCombatTravel.setTravelingUnits(villageUnitList);

        Map<String, Integer> expectedPillagedResources = new HashMap<>();
        expectedPillagedResources.put("Wood", 500);
        expectedPillagedResources.put("Stone", 500);
        // Since it's an integer the value 0.5 should get truncated to 0
        expectedPillagedResources.put("Beer", 0);
        float percentSurvived = 100f;
        ReturningCombatTravel returningCombatTravel = spy(ReturningCombatTravel.class);

        doCallRealMethod().when(villageServiceUnderTesting).winBattle(attackCombatTravel, percentSurvived);
        when(villageServiceUnderTesting.createReturnTravel(attackCombatTravel)).thenReturn(returningCombatTravel);
        villageServiceUnderTesting.winBattle(attackCombatTravel, percentSurvived);
        verify(returningCombatTravel, times(1)).setPillagedResources(expectedPillagedResources);
    }

    @Test
    public void should_PillageOneHundredWoodOneHundredStoneZeroBeer_when_AttackIsLimitedByThreeHundredPillageCapacity () {
        MockVillageService villageServiceUnderTesting = mock(MockVillageService.class, withSettings().useConstructor(this.villageDataMapperMock, this.resourceManagerMock, this.worldServiceMock, this.distanceServiceMock));
        AttackCombatTravel attackCombatTravel = new AttackCombatTravel();

        MockVillage mockAttackingVillage = this.villagesHelper.generateMockVillage(1);
        MockVillage mockDefendingVillage = this.villagesHelper.generateMockVillage(2);
        Map<String, Integer> defendingVillageResources = new HashMap<>();
        defendingVillageResources.put("Wood", 10000);
        defendingVillageResources.put("Stone", 10000);
        defendingVillageResources.put("Beer", 1);
        mockDefendingVillage.setVillageResources(defendingVillageResources);

        attackCombatTravel.setAttackingVillage(mockAttackingVillage);
        attackCombatTravel.setDefendingVillage(mockDefendingVillage);
        List<VillageUnit> villageUnitList = new ArrayList<>();
        VillageUnit transportShips = unitHelper.generateVillageUnit("TransportShip", 2);
        VillageUnit battleShip = unitHelper.generateVillageUnit("BattleShip", 2);
        villageUnitList.add(transportShips);
        villageUnitList.add(battleShip);
        attackCombatTravel.setTravelingUnits(villageUnitList);

        Map<String, Integer> expectedPillagedResources = new HashMap<>();
        expectedPillagedResources.put("Wood", 1000);
        expectedPillagedResources.put("Stone", 1000);
        // Since it's an integer the value 0.5 should get truncated to 0
        expectedPillagedResources.put("Beer", 0);
        float percentSurvived = 1f;
        ReturningCombatTravel returningCombatTravel = spy(ReturningCombatTravel.class);

        doCallRealMethod().when(villageServiceUnderTesting).winBattle(attackCombatTravel, percentSurvived);
        when(villageServiceUnderTesting.createReturnTravel(attackCombatTravel)).thenReturn(returningCombatTravel);
        villageServiceUnderTesting.winBattle(attackCombatTravel, percentSurvived);
        verify(returningCombatTravel, times(1)).setPillagedResources(expectedPillagedResources);
    }

    // returnUnits
    @Test
    public void should_CallAddResourcesThreeTimes_when_TheTravelReturnsThreeTypesOfResourcesToTheVillage () {
        MockVillageService villageServiceUnderTesting = mock(MockVillageService.class, withSettings().useConstructor(this.villageDataMapperMock, this.resourceManagerMock, this.worldServiceMock, this.distanceServiceMock));
        ReturningCombatTravel returningCombatTravel = new ReturningCombatTravel();
        Map<String, Integer> pillagedResources = new HashMap<>();
        pillagedResources.put("Wood", 1000);
        pillagedResources.put("Stone", 1000);
        pillagedResources.put("Beer", 1);
        returningCombatTravel.setPillagedResources(pillagedResources);
        MockVillage mockAttackingVillage = this.villagesHelper.generateMockVillage(1);

        String expectedWood = "Wood";
        int expectedWoodAmount = 1000;
        String expectedStone = "Stone";
        int expectedStoneAmount = 1000;
        String expectedBeer = "Beer";
        int expectedBeerAmount = 1;

        doCallRealMethod().when(villageServiceUnderTesting).returnUnits(mockAttackingVillage, returningCombatTravel);
        villageServiceUnderTesting.returnUnits(mockAttackingVillage, returningCombatTravel);
        verify(this.resourceManagerMock, times(1)).addResources(mockAttackingVillage, expectedWoodAmount, expectedWood);
        verify(this.resourceManagerMock, times(1)).addResources(mockAttackingVillage, expectedStoneAmount, expectedStone);
        verify(this.resourceManagerMock, times(1)).addResources(mockAttackingVillage, expectedBeerAmount, expectedBeer);
        verify(this.villageDataMapperMock, times(1)).update(mockAttackingVillage);
    }


    @Test
    public void should_ThrowSettleConditionsNotMetExceptionWithNoJarl_when_ThereIsNoJarlInTheVillageAndcheckIsValidCreatingSpotIsCalled() {
        Coord providedCoord = new Coord(3, 3);
        SettleConditionsNotMetException expectedException = new SettleConditionsNotMetException("To create a new village you need a jarl");
        Exception thrownException = assertThrows(expectedException.getClass(), () -> this.villageServiceUnderTesting.checkIsValidCreatingSpot(villageMock, providedCoord));
        assertThat(thrownException.getMessage(), is(expectedException.getMessage()));
    }

    @Test
    public void should_ThrowSettleConditionsNotMetExceptionWithTooMuchDistance_when_TheCoordinatesAreNotGivenAndcheckIsValidCreatingSpotIsCalled() {
        this.villageMock.setVillageUnit(unitHelper.generateVillageUnit("Jarl", 1));
        SettleConditionsNotMetException expectedException = new SettleConditionsNotMetException("Too much distance between your village and the new village");
        Exception thrownException = assertThrows(expectedException.getClass(), () -> this.villageServiceUnderTesting.checkIsValidCreatingSpot(villageMock, null));
        assertThat(thrownException.getMessage(), is(expectedException.getMessage()));
    }

    @Test
    public void should_ThrowSettleConditionsNotMetExceptionWithTooMuchDistance_when_TheDistanceBetweenVillagesIsTooBigAndcheckIsValidCreatingSpotIsCalled() {
        this.villageMock.setVillageUnit(unitHelper.generateVillageUnit("Jarl", 1));
        when(this.distanceServiceMock.calculateDistance(any(), any())).thenReturn(10);
        Coord providedCoord = new Coord(3, 3);
        SettleConditionsNotMetException expectedException = new SettleConditionsNotMetException("Too much distance between your village and the new village");
        Exception thrownException = assertThrows(expectedException.getClass(), () -> this.villageServiceUnderTesting.checkIsValidCreatingSpot(villageMock, providedCoord));
        assertThat(thrownException.getMessage(), is(expectedException.getMessage()));
    }

    @Test
    public void should_ThrowSettleConditionsNotMetExceptionWithInvalidSpot_when_TheBuildingSpotIsInValidAndcheckIsValidCreatingSpotIsCalled() {
        this.villageMock.setVillageUnit(unitHelper.generateVillageUnit("Jarl", 1));
        when(this.distanceServiceMock.calculateDistance(any(), any())).thenReturn(1);
        when(this.worldServiceMock.getWorldMap()).thenReturn(this.worldMap);
        when(this.worldMap.isValidToBuildNewVillage(any())).thenReturn(false);
        Coord providedCoord = new Coord(3, 3);
        SettleConditionsNotMetException expectedException = new SettleConditionsNotMetException("Invalid build spot for a new village");
        Exception thrownException = assertThrows(expectedException.getClass(), () -> this.villageServiceUnderTesting.checkIsValidCreatingSpot(villageMock, providedCoord));
        assertThat(thrownException.getMessage(), is(expectedException.getMessage()));
    }


    @Test
    public void should_RunWithNoErrors_when_CheckIsValidCreatingSpotIsCalled() {
        VillageService villageServiceUnderTesting = mock(MockVillageService.class, withSettings().useConstructor(this.villageDataMapperMock, this.resourceManagerMock, this.worldServiceMock, this.distanceServiceMock));
        this.villageMock.setVillageUnit(unitHelper.generateVillageUnit("Jarl", 1));
        when(this.distanceServiceMock.calculateDistance(any(), any())).thenReturn(1);
        when(this.worldServiceMock.getWorldMap()).thenReturn(this.worldMap);
        when(this.worldMap.isValidToBuildNewVillage(any())).thenReturn(true);
        Coord providedCoord = new Coord(3, 3);
        doCallRealMethod().when(villageServiceUnderTesting).checkIsValidCreatingSpot(villageMock, providedCoord);
        villageServiceUnderTesting.checkIsValidCreatingSpot(villageMock, providedCoord);
        verify(villageServiceUnderTesting, times(1)).checkIsValidCreatingSpot(villageMock, providedCoord);
    }

    @Test
    public void should_BeAbleToBuildAHarbor_when_TheBuildPositionIsSpecificForAHarbor() {
        this.mockVillageDAO();
        MockVillage buildSpotVillageUnderTesting = this.villagesHelper.generateMockVillageWithBuildableHarborPlace();
        buildSpotVillageUnderTesting = this.villagesHelper.setupHarborRequirements(buildSpotVillageUnderTesting);
        doCallRealMethod().when(this.villageDataMapperMock).createBuilding(any(), any());

        Building harbor = this.buildingFactory.getBuilding("Harbor", new Coord(1,1));

        MockVillage returnedVillage = (MockVillage) this.villageServiceUnderTesting.createBuilding(buildSpotVillageUnderTesting, harbor);
        Set<Building> returnedBuildings = returnedVillage.getBuildings();
        assertThat(returnedBuildings.contains(harbor), is(true));
    }

    @Test
    public void should_BeAbleToBuildAHarbor_when_TheBuildPositionIsForAnyBuilding() {
        this.mockVillageDAO();
        MockVillage buildSpotVillageUnderTesting = this.villagesHelper.generateMockVillageWithBuildableHarborPlace();
        buildSpotVillageUnderTesting = this.villagesHelper.setupHarborRequirements(buildSpotVillageUnderTesting);
        Building harbor = this.buildingFactory.getBuilding("Harbor", new Coord(2,2));
        doCallRealMethod().when(this.villageDataMapperMock).createBuilding(any(), any());
        MockVillage returnedVillage = (MockVillage) this.villageServiceUnderTesting.createBuilding(buildSpotVillageUnderTesting, harbor);
        Set<Building> returnedBuildings = returnedVillage.getBuildings();
        assertThat(returnedBuildings.contains(harbor), is(true));
    }

    @Test
    public void should_NotBeAbleToBuildAHarbor_when_TheBuildPositionIsForASmith() {
        MockVillage initialBuildSpotVillageUnderTesting = this.villagesHelper.generateMockVillageWithBuildableHarborPlace();
        MockVillage buildSpotVillageUnderTesting = this.villagesHelper.setupHarborRequirements(initialBuildSpotVillageUnderTesting);

        Building harbor = this.buildingFactory.getBuilding("Harbor", new Coord(3,3));
        doCallRealMethod().when(this.villageDataMapperMock).createBuilding(any(), any());

        // The exception or message might change depending on the implementation
        BuildingConditionsNotMetException expectedException = new BuildingConditionsNotMetException("You can\'t build this building on this spot");
        Exception thrownException = assertThrows(expectedException.getClass(), () -> this.villageServiceUnderTesting.createBuilding(buildSpotVillageUnderTesting, harbor));
        assertThat(thrownException.getMessage(), is(expectedException.getMessage()));
    }

    @Test
    public void should_NotBeAbleToBuildASmith_when_OnANonBuildingCoordinate() {
        MockVillage initialBuildSpotVillageUnderTesting = this.villagesHelper.generateMockVillageWithBuildableHarborPlace();
        MockVillage buildSpotVillageUnderTesting = this.villagesHelper.setupHarborRequirements(initialBuildSpotVillageUnderTesting);
        Map<String, Integer> villageResources = new HashMap<>();
        villageResources.put("Wood", 99999999);
        villageResources.put("Stone", 99999999);
        buildSpotVillageUnderTesting.setVillageResources(villageResources);
        Building smith = this.buildingFactory.getBuilding("Smith", new Coord(99,99));
        doCallRealMethod().when(this.villageDataMapperMock).createBuilding(any(), any());

        // The exception or message might change depending on the implementation
        BuildingConditionsNotMetException expectedException = new BuildingConditionsNotMetException("You can\'t build this building on this spot");
        Exception thrownException = assertThrows(expectedException.getClass(), () -> this.villageServiceUnderTesting.createBuilding(buildSpotVillageUnderTesting, smith));
        assertThat(thrownException.getMessage(), is(expectedException.getMessage()));
    }


    @Test
    public void should_NotBeAbleToBuildAHeadquarters_when_ItsAlreadyPresentInAVillage() {
        this.mockVillageDAO();
        MockVillage buildSpotVillageUnderTesting = this.villagesHelper.generateMockVillage(1);
        Map<String, Integer> villageResources = new HashMap<>();
        villageResources.put("Wood", 99999999);
        villageResources.put("Stone", 99999999);
        buildSpotVillageUnderTesting.setVillageResources(villageResources);
        Building hq = this.buildingFactory.getBuilding("Headquarters", new Coord(6,6));
        doCallRealMethod().when(this.villageDataMapperMock).createBuilding(any(), any());
        MockVillage firstHeadquartersBuildVillage = (MockVillage) this.villageServiceUnderTesting.createBuilding(buildSpotVillageUnderTesting, hq);
        Set<Building> firstHeadquartersVillageReturnedBuildings = firstHeadquartersBuildVillage.getBuildings();
        assertThat(firstHeadquartersVillageReturnedBuildings.contains(hq), is(true));

        // The exception or message might change depending on the implementation
        BuildingConditionsNotMetException expectedException = new BuildingConditionsNotMetException("You can\'t have more than 1 Headquarter");
        Exception thrownException = assertThrows(expectedException.getClass(), () -> this.villageServiceUnderTesting.createBuilding(firstHeadquartersBuildVillage, hq));
        assertThat(thrownException.getMessage(), is(expectedException.getMessage()));
    }

    @Test
    public void should_NotBeAbleToBuildAHeadquarters_when_TheCoordsAreNotX6Y6() {
        this.mockVillageDAO();
        MockVillage buildSpotVillageUnderTesting = this.villagesHelper.generateMockVillage(1);
        Map<String, Integer> villageResources = new HashMap<>();
        villageResources.put("Wood", 99999999);
        villageResources.put("Stone", 99999999);
        buildSpotVillageUnderTesting.setVillageResources(villageResources);
        Building hq = this.buildingFactory.getBuilding("Headquarters", new Coord(99,99));
        doCallRealMethod().when(this.villageDataMapperMock).createBuilding(any(), any());

        // The exception or message might change depending on the implementation
        BuildingConditionsNotMetException expectedException = new BuildingConditionsNotMetException("You can\'t build a Headquarter on another spot than 6, 6");
        Exception thrownException = assertThrows(expectedException.getClass(), () -> this.villageServiceUnderTesting.createBuilding(buildSpotVillageUnderTesting, hq));
        assertThat(thrownException.getMessage(), is(expectedException.getMessage()));
    }

    @Test
    public void should_NotBeAbleToBuildAWall_when_TheCoordsAreNotX0Y0() {
        MockVillage buildSpotVillageUnderTesting = this.villagesHelper.generateMockVillage(1);
        Map<String, Integer> villageResources = new HashMap<>();
        villageResources.put("Wood", 99999999);
        villageResources.put("Stone", 99999999);
        villageResources.put("Beer", 99999999);
        buildSpotVillageUnderTesting.setVillageResources(villageResources);
        buildSpotVillageUnderTesting.setPopulationLeft(99999);
        Building wall = this.buildingFactory.getBuilding("Wall", new Coord(99,99));
        doCallRealMethod().when(this.villageDataMapperMock).createBuilding(any(), any());

        // The exception or message might change depending on the implementation
        BuildingConditionsNotMetException expectedException = new BuildingConditionsNotMetException("You can\'t build a Wall on another spot than 0, 0");
        Exception thrownException = assertThrows(expectedException.getClass(), () -> this.villageServiceUnderTesting.createBuilding(buildSpotVillageUnderTesting, wall));
        assertThat(thrownException.getMessage(), is(expectedException.getMessage()));
    }

    @Test
    public void should_NotShareResourcesWithOtherVillages_when_AUserHasMoreThanOneVillage() {
        this.mockVillageDAO();
        MockVillage village1UnderTesting = this.villagesHelper.generateMockVillage(1);
        MockVillage village2UnderTesting = this.villagesHelper.generateMockVillage(2);
        village2UnderTesting.setUser(village1UnderTesting.getUser());

        int expectedStoneAmountBeforeBuilding = 1000;
        int expectedStoneAmountAfterBuilding = 950;

        Map<String, Integer> initialVillageResources = new HashMap<>();
        initialVillageResources.put("Stone", expectedStoneAmountBeforeBuilding);
        village1UnderTesting.setVillageResources(initialVillageResources);
        village2UnderTesting.setVillageResources(initialVillageResources);
        village1UnderTesting.setPopulationLeft(1000);
        village2UnderTesting.setPopulationLeft(1000);

        Building lumberyard = this.buildingFactory.getBuilding("Lumberyard", new Coord(1,1));
        doCallRealMethod().when(this.villageDataMapperMock).createBuilding(any(), any());

        Village returnedVillage1 = this.villageServiceUnderTesting.createBuilding(village1UnderTesting, lumberyard);
        Village returnedVillage2 = this.villageServiceUnderTesting.createBuilding(village2UnderTesting, lumberyard);
        assertThat(returnedVillage1.getVillageResources().get(ResourceType.Stone.name()), is(expectedStoneAmountAfterBuilding));
        assertThat(returnedVillage2.getVillageResources().get(ResourceType.Stone.name()), is(expectedStoneAmountAfterBuilding));
    }

    @Test
    public void should_SubtractPopulation_when_ABuildingIsBuild() {
        this.mockVillageDAO();
        MockVillage villageUnderTesting = this.villagesHelper.generateMockVillage(1);
        int populationLeftBeforeBuilding = 1000;

        Map<String, Integer> initialVillageResources = new HashMap<>();
        initialVillageResources.put("Stone", 1000);
        villageUnderTesting.setVillageResources(initialVillageResources);
        villageUnderTesting.setPopulationLeft(populationLeftBeforeBuilding);

        Building lumberyard = this.buildingFactory.getBuilding("Lumberyard", new Coord(1,1));
        doCallRealMethod().when(this.villageDataMapperMock).createBuilding(any(), any());

        Village returnedVillage = this.villageServiceUnderTesting.createBuilding(villageUnderTesting, lumberyard);
        assertThat(returnedVillage.getPopulationLeft(), lessThan(populationLeftBeforeBuilding));
    }

    @Test
    public void should_ThrowAnBuildingConditionsNotMetExceptionWhenAVillageTriesToBuildBarracks_when_ALevel5HeadquartersIsNotPresent() {
        MockVillage villageUnderTesting = this.villagesHelper.generateMockVillage(1);
        int populationLeftBeforeBuilding = 1000;

        Map<String, Integer> initialVillageResources = new HashMap<>();
        initialVillageResources.put("Wood", 99999999);
        initialVillageResources.put("Stone", 99999999);
        villageUnderTesting.setVillageResources(initialVillageResources);
        villageUnderTesting.setPopulationLeft(populationLeftBeforeBuilding);

        Building barracks = this.buildingFactory.getBuilding("Barracks", new Coord(1,1));
        doCallRealMethod().when(this.villageDataMapperMock).createBuilding(any(), any());

        // The exception or message might change depending on the implementation
        BuildingConditionsNotMetException expectedException = new BuildingConditionsNotMetException("Your Headquarters must at least be level 5");
        Exception thrownException = assertThrows(expectedException.getClass(), () -> this.villageServiceUnderTesting.createBuilding(villageUnderTesting, barracks));
        assertThat(thrownException.getMessage(), is(expectedException.getMessage()));
    }

    @Test
    public void should_ThrowAnBuildingConditionsNotMetExceptionWhenAVillageTriesToBuildAHarbor_when_TheBuildingRequirementsAreNotMet() {
        this.mockVillageDAO();
        MockVillage initialVillageUnderTesting = this.villagesHelper.generateMockVillageWithBuildableHarborPlace();
        MockVillage villageUnderTesting = this.villagesHelper.setupInsufficientHarborRequirements(initialVillageUnderTesting);
        doCallRealMethod().when(this.villageDataMapperMock).createBuilding(any(), any());

        Building harbor = this.buildingFactory.getBuilding("Harbor", new Coord(1,1));

        // The exception or message might change depending on the implementation
        BuildingConditionsNotMetException expectedException = new BuildingConditionsNotMetException("Your Headquarters must at least be level 10, Lumberyard level 10 and Smith level 8");
        Exception thrownException = assertThrows(expectedException.getClass(), () -> this.villageServiceUnderTesting.createBuilding(villageUnderTesting, harbor));
        assertThat(thrownException.getMessage(), is(expectedException.getMessage()));
    }

    @Test
    public void should_ThrowAnBuildingConditionsNotMetExceptionWhenAVillageTriesToBuildATavern_when_ALevel1FarmIsNotPresent() {
        MockVillage villageUnderTesting = this.villagesHelper.generateMockVillage(1);
        int populationLeftBeforeBuilding = 1000;

        Map<String, Integer> initialVillageResources = new HashMap<>();
        initialVillageResources.put("Wood", 99999999);
        initialVillageResources.put("Stone", 99999999);
        villageUnderTesting.setVillageResources(initialVillageResources);
        villageUnderTesting.setPopulationLeft(populationLeftBeforeBuilding);

        Building tavern = this.buildingFactory.getBuilding("Tavern", new Coord(1,1));
        doCallRealMethod().when(this.villageDataMapperMock).createBuilding(any(), any());

        // The exception or message might change depending on the implementation
        BuildingConditionsNotMetException expectedException = new BuildingConditionsNotMetException("Your Farm must at least be level 1");
        Exception thrownException = assertThrows(expectedException.getClass(), () -> this.villageServiceUnderTesting.createBuilding(villageUnderTesting, tavern));
        assertThat(thrownException.getMessage(), is(expectedException.getMessage()));
    }

    @Test
    public void should_ThrowAnBuildingConditionsNotMetExceptionWhenAVillageTriesToBuildAWall_when_ALevel3HeadquartersIsNotPresent() {
        MockVillage villageUnderTesting = this.villagesHelper.generateMockVillage(1);
        int populationLeftBeforeBuilding = 1000;

        Map<String, Integer> initialVillageResources = new HashMap<>();
        initialVillageResources.put("Wood", 99999999);
        initialVillageResources.put("Stone", 99999999);
        initialVillageResources.put("Beer", 99999999);
        villageUnderTesting.setVillageResources(initialVillageResources);
        villageUnderTesting.setPopulationLeft(populationLeftBeforeBuilding);

        Building wall = this.buildingFactory.getBuilding("Wall", new Coord(0,0));
        doCallRealMethod().when(this.villageDataMapperMock).createBuilding(any(), any());

        // The exception or message might change depending on the implementation
        BuildingConditionsNotMetException expectedException = new BuildingConditionsNotMetException("Your Headquarters must at least be level 3");
        Exception thrownException = assertThrows(expectedException.getClass(), () -> this.villageServiceUnderTesting.createBuilding(villageUnderTesting, wall));
        assertThat(thrownException.getMessage(), is(expectedException.getMessage()));
    }

    @Test
    public void should_ThrowAnBuildingConditionsNotMetExceptionWhenAVillageTriesToBuildADefenceTower_when_TheBuildingRequirementsAreNotMet() {
        MockVillage initialVillageUnderTesting = this.villagesHelper.generateMockVillageWithBuildableHarborPlace();
        MockVillage villageUnderTesting = this.villagesHelper.setupInsufficientDefenceTowerRequirements(initialVillageUnderTesting);
        doCallRealMethod().when(this.villageDataMapperMock).createBuilding(any(), any());

        Building defenceTower = this.buildingFactory.getBuilding("DefenceTower", new Coord(1,1));

        // The exception or message might change depending on the implementation
        BuildingConditionsNotMetException expectedException = new BuildingConditionsNotMetException("Your Headquarters must at least be level 5, Barracks level 5 and Smith level 5");
        Exception thrownException = assertThrows(expectedException.getClass(), () -> this.villageServiceUnderTesting.createBuilding(villageUnderTesting, defenceTower));
        assertThat(thrownException.getMessage(), is(expectedException.getMessage()));
    }
}
