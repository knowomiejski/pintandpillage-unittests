package nl.duckstudios.pintandpillage.mocks;

import nl.duckstudios.pintandpillage.entity.buildings.House;
import nl.duckstudios.pintandpillage.helper.ResourceManager;

public class MockHouse extends House {
    public MockHouse () {
        super();
    }
    public void setResourceManager(ResourceManager resourceManager){
        super.resourceManager = resourceManager;
    }
}
