package nl.duckstudios.pintandpillage.mocks;

import nl.duckstudios.pintandpillage.entity.buildings.Headquarters;
import nl.duckstudios.pintandpillage.helper.ResourceManager;

public class MockHeadquarters extends Headquarters {
    public MockHeadquarters() {
        super();
    }
    public void setResourceManager(ResourceManager resourceManager){
        super.resourceManager = resourceManager;
    }
}
