package nl.duckstudios.pintandpillage.mocks;

import nl.duckstudios.pintandpillage.entity.buildings.Lumberyard;
import nl.duckstudios.pintandpillage.helper.ResourceManager;

public class MockLubmeryard extends Lumberyard {
    public MockLubmeryard() {
        super();
    }
    public void setResourceManager(ResourceManager resourceManager){
        super.resourceManager = resourceManager;
    }
}
