package nl.duckstudios.pintandpillage.entity.buildings;

public class ResourceVault extends Building{
    private String name = "Vault";

    public ResourceVault () {
        setConstructionTimeGivenLevel(10);
    }

    @Override
    public void updateBuilding() {

    }

    @Override
    public void setLevel(int level) {
        super.setLevel(level);
        setConstructionTimeGivenLevel(level);
    }

    public String getName() {
        return name;
    }

    public void setConstructionTimeGivenLevel(int level) {
        super.setConstructionTimeSeconds(20 * level + 10);
    }

}
