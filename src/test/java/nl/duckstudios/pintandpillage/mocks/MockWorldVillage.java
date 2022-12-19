package nl.duckstudios.pintandpillage.mocks;

import nl.duckstudios.pintandpillage.entity.Coord;
import nl.duckstudios.pintandpillage.entity.User;
import nl.duckstudios.pintandpillage.model.WorldVillage;

public class MockWorldVillage extends WorldVillage {

    // used for mocking the
    private User user;

    public MockWorldVillage(long villageId, String villageOwnerName, Coord position, String name, long userId, int points, User user) {
        super(villageId, villageOwnerName, position, name, userId, points);
        this.user = user;
    }

    public User getVillageUser () {
        return user;
    }

    public void setVillageUser (User user) {
        this.user = user;
    }
}
