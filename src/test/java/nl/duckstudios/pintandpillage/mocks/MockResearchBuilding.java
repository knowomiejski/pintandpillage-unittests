package nl.duckstudios.pintandpillage.mocks;

import nl.duckstudios.pintandpillage.entity.buildings.ResearchBuilding;
import nl.duckstudios.pintandpillage.helper.ResourceManager;

public class MockResearchBuilding extends ResearchBuilding {
    @Override
    public void updateBuilding() {
        //
    }

    public void updateVillageState() {
        super.updateVillageState();
    }
    public void setResourceManager (ResourceManager resourceManager) {
        super.resourceManager = resourceManager;
    }
}
