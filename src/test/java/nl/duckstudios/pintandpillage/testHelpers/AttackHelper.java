package nl.duckstudios.pintandpillage.testHelpers;

import nl.duckstudios.pintandpillage.entity.VillageUnit;
import nl.duckstudios.pintandpillage.helper.UnitFactory;
import nl.duckstudios.pintandpillage.model.AttackUnitData;
import nl.duckstudios.pintandpillage.model.AttackVillageData;
import nl.duckstudios.pintandpillage.model.UnitType;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AttackHelper {

    public AttackUnitData generateShieldAttackUnit() {
        int randomAmount = new Random().nextInt(1, 20);
        return new AttackUnitData(UnitType.Shield, randomAmount);
    }

    public AttackUnitData generateRandomAttackUnit() {
        UnitType randomUnitType = UnitType.values()[new Random().nextInt(UnitType.values().length)];
        // Exclude Scout unit since it's not in the UnitFactory.getStaticUnit
        while (randomUnitType.name() == "Scout") {
            randomUnitType = UnitType.values()[new Random().nextInt(UnitType.values().length)];
        }
        int randomAmount = new Random().nextInt(1, 20);
        return new AttackUnitData(randomUnitType, randomAmount);
    }

    public List<AttackUnitData> generateAttackUnitList(boolean random) {
        int randomListLength = new Random().nextInt(2, 9);
        List<AttackUnitData> attackUnitDataList = new ArrayList<AttackUnitData>();

        if (!random) {
            attackUnitDataList.add(generateShieldAttackUnit());
            randomListLength--;
        }

        while (randomListLength > 0) {
            AttackUnitData generatedAttackUnit = generateRandomAttackUnit();
            if (attackUnitDataList.stream().filter(attackUnitData -> attackUnitData.unitType.equals(generatedAttackUnit.unitType)).findAny().isEmpty()) {
                attackUnitDataList.add(generatedAttackUnit);
                randomListLength--;
            }
        }
        return attackUnitDataList;
    }

    public AttackVillageData generateAttackVillageData(long toVillageId, long fromVillageId) {
        AttackVillageData attackVillageData = new AttackVillageData();
        attackVillageData.toVillageId = toVillageId;
        attackVillageData.fromVillageId = fromVillageId;
        attackVillageData.units = new ArrayList<>();
        attackVillageData.units.add(generateShieldAttackUnit());
        attackVillageData.units.add(generateShieldAttackUnit());
        return attackVillageData;
    }

    public AttackVillageData generateAttackVillageData(boolean random, long toVillageId, long fromVillageId) {
        AttackVillageData attackVillageData = new AttackVillageData();
        attackVillageData.toVillageId = toVillageId;
        attackVillageData.fromVillageId = fromVillageId;
        attackVillageData.units = generateAttackUnitList(random);
        return attackVillageData;
    }

    public List<VillageUnit> generateVillageUnits () {
        List<VillageUnit> attackingUnits = new ArrayList<>();
        AttackVillageData data = generateAttackVillageData(false, 2, 1);
        for (AttackUnitData attackUnitData : data.units) {
            VillageUnit unitIntList = attackingUnits.stream().filter(a -> a.getUnit().getUnitName() == attackUnitData.unitType).findFirst().orElse(null);
            if (unitIntList != null) {
                unitIntList.setAmount(unitIntList.getAmount() + attackUnitData.amount);
                continue;
            }

            VillageUnit villageUnit = new VillageUnit(UnitFactory.getUnitStatic(attackUnitData.unitType.name()), attackUnitData.amount);
            attackingUnits.add(villageUnit);
        }
        return attackingUnits;
    }
}
