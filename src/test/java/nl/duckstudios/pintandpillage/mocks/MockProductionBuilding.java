package nl.duckstudios.pintandpillage.mocks;

import nl.duckstudios.pintandpillage.entity.buildings.ProductionBuilding;
import nl.duckstudios.pintandpillage.helper.ResourceManager;

public class MockProductionBuilding extends ProductionBuilding {
    @Override
    public void updateBuilding() {
        super.setConstructionTimeSeconds(10);
    }
    public void setResourceManager(ResourceManager resourceManager){
        super.resourceManager = resourceManager;
    }

}
