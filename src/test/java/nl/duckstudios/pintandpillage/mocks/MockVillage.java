package nl.duckstudios.pintandpillage.mocks;

import nl.duckstudios.pintandpillage.entity.Village;
import nl.duckstudios.pintandpillage.entity.VillageUnit;
import nl.duckstudios.pintandpillage.entity.buildings.Building;
import nl.duckstudios.pintandpillage.entity.researching.Research;

import java.util.List;
import java.util.Set;

public class MockVillage extends Village {
    public MockVillage() {
        super();
    }

    public void setVillageUnit (VillageUnit vu) {
        super.getUnitsInVillage().add(vu);
    }

    public Set<Building> getBuildings() {
        return super.buildings;
    }
}
