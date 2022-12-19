package nl.duckstudios.pintandpillage.mocks;

import nl.duckstudios.pintandpillage.controller.CombatController;
import nl.duckstudios.pintandpillage.dao.TravelDao;
import nl.duckstudios.pintandpillage.service.*;

public class MockCombatController extends CombatController {
    public MockCombatController(AuthenticationService authenticationService, AccountService accountService, VillageService villageService, CombatService combatService, TravelDao travelDao, DistanceService distanceService) {
        super(authenticationService, accountService, villageService, combatService, travelDao, distanceService);
    }
}
