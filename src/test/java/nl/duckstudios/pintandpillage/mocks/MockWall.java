package nl.duckstudios.pintandpillage.mocks;

import nl.duckstudios.pintandpillage.entity.buildings.Wall;
import nl.duckstudios.pintandpillage.helper.ResourceManager;

public class MockWall extends Wall {
    public MockWall () {
        super();
    }
    public void setResourceManager(ResourceManager resourceManager){
        super.resourceManager = resourceManager;
    }
}
