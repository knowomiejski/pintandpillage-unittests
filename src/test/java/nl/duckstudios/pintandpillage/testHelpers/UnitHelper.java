package nl.duckstudios.pintandpillage.testHelpers;

import nl.duckstudios.pintandpillage.entity.VillageUnit;
import nl.duckstudios.pintandpillage.entity.production.Unit;
import nl.duckstudios.pintandpillage.helper.UnitFactory;
import nl.duckstudios.pintandpillage.model.UnitType;
import nl.duckstudios.pintandpillage.model.Unlock;

import java.util.ArrayList;
import java.util.List;

public class UnitHelper extends UnitFactory {
    public List<Unlock> generateUnitsUnlockedAtList () {
        List<Unlock> unlockList = new ArrayList<Unlock>();
        unlockList.add(new Unlock(UnitType.Spear, 0));
        unlockList.add(new Unlock(UnitType.Axe, 1));
        unlockList.add(new Unlock(UnitType.Shield, 5));
        return unlockList;
    }

    public VillageUnit generateVillageUnit(String type, int amount) {
        Unit newUnit = super.getUnit(type);
        return new VillageUnit(newUnit, amount);
    }
}
