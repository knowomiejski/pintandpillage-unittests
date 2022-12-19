package nl.duckstudios.pintandpillage.testHelpers;

import nl.duckstudios.pintandpillage.entity.Coord;
import nl.duckstudios.pintandpillage.entity.Village;
import nl.duckstudios.pintandpillage.entity.buildings.Building;
import nl.duckstudios.pintandpillage.entity.buildings.Lumberyard;
import nl.duckstudios.pintandpillage.entity.travels.AttackCombatTravel;
import nl.duckstudios.pintandpillage.helper.BuildingFactory;
import nl.duckstudios.pintandpillage.mocks.MockUser;
import nl.duckstudios.pintandpillage.mocks.MockVillage;
import nl.duckstudios.pintandpillage.mocks.MockWorldVillage;
import nl.duckstudios.pintandpillage.model.BuildPosition;
import nl.duckstudios.pintandpillage.model.WorldVillage;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VillagesHelper {

    private BuildingFactory buildingFactory = new BuildingFactory();

    public MockVillage generateMockVillage (int villageNumber) {
        MockUser mockUser = new MockUser();
        mockUser.setUsername("mockeduser" + villageNumber);
        mockUser.setId((long) villageNumber);
        MockVillage mockVillage = new MockVillage();
        mockVillage.setVillageId(villageNumber);
        mockVillage.setUser(mockUser);
        mockVillage.setPositionX(villageNumber);
        mockVillage.setPositionY(villageNumber);
        mockVillage.setName("mockedvillage" + villageNumber);
        mockVillage.setVillagePoints(villageNumber);
        return mockVillage;
    }

//    public AttackCombatTravel generateAttackCombatTravel () {
//        AttackCombatTravel attackCombatTravel = new AttackCombatTravel();
//        attackCombatTravel.
//    }


    public List<Village> getMockedVillages () {
        List<Village> allVillages = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            allVillages.add(this.generateMockVillage(i));
        }
        return allVillages;
    }

    public List<MockWorldVillage> getMockWorldVillages () {
        List<Village> allVillages = getMockedVillages();
        List<MockWorldVillage> worldVillages = new ArrayList<>();
        for (Village village : allVillages) {
            worldVillages.add(new MockWorldVillage(
                    village.getVillageId(),
                    village.getUser().getUsername(),
                    new Coord(village.getPositionX(), village.getPositionY()),
                    village.getName(),
                    village.getUser().getId(),
                    village.getVillagePoints(),
                    village.getUser()
            ));
        }
        return worldVillages;
    }

    public List<WorldVillage> convertMockWorldVillagesToWorldVillages (List<MockWorldVillage> mockWorldVillages) {
        List<WorldVillage> worldVillages = new ArrayList<>();
        for (MockWorldVillage village : mockWorldVillages) {
            worldVillages.add(new WorldVillage(
                    village.villageId,
                    village.getVillageUser().getUsername(),
                    new Coord(village.position.getX(), village.position.getY()),
                    village.name,
                    village.getVillageUser().getId(),
                    village.points
            ));
        }
        return worldVillages;
    }

    public MockVillage generateMockVillageWithBuildableHarborPlace() {
        MockVillage mockVillage = new MockVillage();
        BuildPosition buildPosition1 = new BuildPosition(1, 1, "Harbor");
        BuildPosition buildPosition2 = new BuildPosition(2, 2);
        BuildPosition buildPosition3 = new BuildPosition(3, 3, "Smith");
        BuildPosition[] buildPositions = { buildPosition1, buildPosition2, buildPosition3 };
        mockVillage.setValidBuildPositions(buildPositions);
        Map<String, Integer> villageResources = new HashMap<>();
        villageResources.put("Wood", 99999999);
        villageResources.put("Stone", 99999999);
        villageResources.put("Beer", 99999999);
        mockVillage.setVillageResources(villageResources);
        mockVillage.setPopulation(99999999);
        mockVillage.setPopulationLeft(99999999);
        return mockVillage;
    }

    public MockVillage setupHarborRequirements(MockVillage mockVillage) {
        Building hq = this.buildingFactory.getBuilding("Headquarters", new Coord(1,2));
        hq.setLevel(10);
        mockVillage.getBuildings().add(hq);
        Building lumberyard = this.buildingFactory.getBuilding("Lumberyard", new Coord(1,3));
        lumberyard.setLevel(10);
        mockVillage.getBuildings().add(lumberyard);
        Building smith = this.buildingFactory.getBuilding("Smith", new Coord(1,4));
        smith.setLevel(8);
        mockVillage.getBuildings().add(smith);
        return mockVillage;
    }

    public MockVillage setupInsufficientHarborRequirements(MockVillage mockVillage) {
        Building hq = this.buildingFactory.getBuilding("Headquarters", new Coord(1,2));
        hq.setLevel(1);
        hq.setVillage(mockVillage);
        mockVillage.getBuildings().add(hq);
        Building lumberyard = this.buildingFactory.getBuilding("Lumberyard", new Coord(1,3));
        lumberyard.setLevel(1);
        lumberyard.setVillage(mockVillage);
        lumberyard.updateBuilding();
        mockVillage.getBuildings().add(lumberyard);
        Building smith = this.buildingFactory.getBuilding("Smith", new Coord(1,4));
        smith.setLevel(1);
        smith.setVillage(mockVillage);
        mockVillage.getBuildings().add(smith);
        return mockVillage;
    }

    public MockVillage setupInsufficientDefenceTowerRequirements(MockVillage mockVillage) {
        Building hq = this.buildingFactory.getBuilding("Headquarters", new Coord(1,2));
        hq.setLevel(1);
        hq.setVillage(mockVillage);
        mockVillage.getBuildings().add(hq);
        Building barracks = this.buildingFactory.getBuilding("Barracks", new Coord(1,3));
        barracks.setLevel(1);
        barracks.setVillage(mockVillage);
        barracks.updateBuilding();
        mockVillage.getBuildings().add(barracks);
        Building smith = this.buildingFactory.getBuilding("Smith", new Coord(1,4));
        smith.setLevel(1);
        smith.setVillage(mockVillage);
        mockVillage.getBuildings().add(smith);
        return mockVillage;
    }

    public MockVillage setupVillageWithStorage(MockVillage mockVillage) {
        Building hq = this.buildingFactory.getBuilding("Headquarters", new Coord(1,2));
        hq.setLevel(1);
        hq.setVillage(mockVillage);
        mockVillage.getBuildings().add(hq);
        Lumberyard lumberyard = (Lumberyard) this.buildingFactory.getBuilding("Lumberyard", new Coord(1,3));
        lumberyard.setLevel(1);
        // generates 43200 wood
        lumberyard.setLastCollected(LocalDateTime.now().minusDays(90));
        lumberyard.setVillage(mockVillage);
        mockVillage.getBuildings().add(lumberyard);
        Building storage = this.buildingFactory.getBuilding("Storage", new Coord(1,4));
        storage.setLevel(1);
        storage.setVillage(mockVillage);
        mockVillage.getBuildings().add(storage);
        return mockVillage;
    }

}
