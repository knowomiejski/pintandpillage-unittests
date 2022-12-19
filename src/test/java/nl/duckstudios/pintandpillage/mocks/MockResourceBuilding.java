package nl.duckstudios.pintandpillage.mocks;

import nl.duckstudios.pintandpillage.entity.buildings.ResourceBuilding;
import nl.duckstudios.pintandpillage.helper.ResourceManager;

public class MockResourceBuilding extends ResourceBuilding {
    @Override
    public void updateBuilding() {}

    public void setResourceManager(ResourceManager resourceManager){
        super.resourceManager = resourceManager;
    }
}
