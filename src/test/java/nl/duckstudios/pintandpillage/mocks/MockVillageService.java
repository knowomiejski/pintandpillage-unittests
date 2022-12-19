package nl.duckstudios.pintandpillage.mocks;

import nl.duckstudios.pintandpillage.dao.VillageDataMapper;
import nl.duckstudios.pintandpillage.helper.ResourceManager;
import nl.duckstudios.pintandpillage.service.DistanceService;
import nl.duckstudios.pintandpillage.service.VillageService;
import nl.duckstudios.pintandpillage.service.WorldService;

public class MockVillageService extends VillageService {
    public MockVillageService(VillageDataMapper villageDataMapper, ResourceManager resourceManager, WorldService worldService, DistanceService distanceService) {
        super(villageDataMapper, resourceManager, worldService, distanceService);
    }
}
