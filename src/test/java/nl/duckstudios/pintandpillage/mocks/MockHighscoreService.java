package nl.duckstudios.pintandpillage.mocks;

import nl.duckstudios.pintandpillage.dao.UserDAO;
import nl.duckstudios.pintandpillage.service.HighscoreService;
import nl.duckstudios.pintandpillage.service.VillageService;

public class MockHighscoreService extends HighscoreService {
    public MockHighscoreService(UserDAO userDAO, VillageService villageService) {
        super(userDAO, villageService);
    }
}
