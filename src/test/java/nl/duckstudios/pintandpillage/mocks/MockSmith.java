package nl.duckstudios.pintandpillage.mocks;

import nl.duckstudios.pintandpillage.entity.buildings.Smith;
import nl.duckstudios.pintandpillage.helper.ResourceManager;

public class MockSmith extends Smith {
    public MockSmith() {
        super();
    }
    public void setResourceManager (ResourceManager resourceManager) {
        super.resourceManager = resourceManager;
    }
}
