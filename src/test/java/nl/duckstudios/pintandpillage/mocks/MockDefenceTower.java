package nl.duckstudios.pintandpillage.mocks;

import nl.duckstudios.pintandpillage.entity.buildings.DefenceTower;
import nl.duckstudios.pintandpillage.helper.ResourceManager;

public class MockDefenceTower extends DefenceTower {
    public MockDefenceTower () {
            super();
    }
    public void setResourceManager(ResourceManager resourceManager){
            super.resourceManager = resourceManager;
    }
}
