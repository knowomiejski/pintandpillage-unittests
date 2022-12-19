package nl.duckstudios.pintandpillage.mocks;

import nl.duckstudios.pintandpillage.entity.buildings.Storage;
import nl.duckstudios.pintandpillage.helper.ResourceManager;

public class MockStorage extends Storage {
    public MockStorage() {
        super();
    }
    public void setResourceManager(ResourceManager resourceManager){
        super.resourceManager = resourceManager;
    }
}
