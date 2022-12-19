package nl.duckstudios.pintandpillage.service;


import nl.duckstudios.pintandpillage.Exceptions.AttackingConditionsNotMetException;
import nl.duckstudios.pintandpillage.entity.VillageUnit;
import nl.duckstudios.pintandpillage.entity.production.*;
import nl.duckstudios.pintandpillage.helper.UnitFactory;
import nl.duckstudios.pintandpillage.mocks.MockVillage;
import nl.duckstudios.pintandpillage.model.AttackUnitData;
import nl.duckstudios.pintandpillage.model.AttackVillageData;
import nl.duckstudios.pintandpillage.model.UnitType;
import nl.duckstudios.pintandpillage.testHelpers.AttackHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("CombatService")
public class TestCombatService {

    @Mock
    MockVillage mockVillage = new MockVillage();

    CombatService combatServiceUnderTesting;

    AttackHelper attackHelper = new AttackHelper();

    AttackVillageData attackVillageData;


    @BeforeEach
    void generateAttackVillageData () {
        this.combatServiceUnderTesting = new CombatService();
        this.attackVillageData = attackHelper.generateAttackVillageData(true, 2, 1);
    }

    @Test
    void should_ThrowAttackingConditionsNotMetExceptionWithAtLeastOneUnitMessage_when_AnEmptyUnitListIsProvided() {
        AttackVillageData emptyAttackVillageData = new AttackVillageData();
        emptyAttackVillageData.units = new ArrayList<>();
        AttackingConditionsNotMetException expectedException = new AttackingConditionsNotMetException("To attack you need to send at least one unit");
        Exception thrownException = assertThrows(expectedException.getClass(), () -> this.combatServiceUnderTesting.convertToVillageUnits(emptyAttackVillageData));
        assertThat(thrownException.getMessage(), is(expectedException.getMessage()));
    }

    @Test
    void should_MergeAttackingUnits_when_AUnitOfTheSameTypeIsAlreadyPresentInAttackingList() {
        AttackVillageData providedAttackVillageData = new AttackVillageData();
        ArrayList<AttackUnitData> units = new ArrayList<>();
        AttackUnitData unit1 = attackHelper.generateShieldAttackUnit();
        AttackUnitData unit2 = attackHelper.generateShieldAttackUnit();
        units.add(unit1);
        units.add(unit2);
        providedAttackVillageData.units = units;
        int expectedUnitAmount = unit1.amount + unit2.amount;

        List<VillageUnit> expectedUnits = new ArrayList<>();
        expectedUnits.add(new VillageUnit(UnitFactory.getUnitStatic(UnitType.Shield.name()), 1));

        List<VillageUnit> returnedConvertedVillages = this.combatServiceUnderTesting.convertToVillageUnits(providedAttackVillageData);

        assertThat(returnedConvertedVillages.size(), is(1));
        int totalReturnedUnits = 0;
        for (VillageUnit vu : returnedConvertedVillages){
            totalReturnedUnits = totalReturnedUnits + vu.getAmount();
        }
        assertThat(totalReturnedUnits, is(expectedUnitAmount));
    }

    @Test
    void should_ReturnTheListOfAttackingVillageUnits_when_ConvertToVillageUnitsIsSuccessful() {
        AttackVillageData providedAttackVillageData = attackHelper.generateAttackVillageData(true, 2, 1);
        int expectedTotalProvidedUnits = 0;
        for (AttackUnitData unit : providedAttackVillageData.units){
            expectedTotalProvidedUnits = expectedTotalProvidedUnits + unit.amount;
        }

        List<VillageUnit> expectedUnits = new ArrayList<>();
        expectedUnits.add(new VillageUnit(UnitFactory.getUnitStatic(UnitType.Shield.name()), 1));

        List<VillageUnit> returnedConvertedVillages = this.combatServiceUnderTesting.convertToVillageUnits(providedAttackVillageData);

        assertThat(returnedConvertedVillages.size(), is(providedAttackVillageData.units.size()));
        int totalReturnedUnits = 0;
        for (VillageUnit vu : returnedConvertedVillages){
            totalReturnedUnits = totalReturnedUnits + vu.getAmount();
        }
        assertThat(totalReturnedUnits, is(expectedTotalProvidedUnits));
    }

    @Test
    void should_ThrowAttackingConditionsNotMetExceptionWithNotEnoughUnits_when_checkHasEnoughUnitsToAttackIsCalledWithoutUnitInVillage() {
        List<VillageUnit> providedVillageUnits = new ArrayList<>();
        providedVillageUnits.add(new VillageUnit(UnitFactory.getUnitStatic(UnitType.Shield.name()), 1));
        MockVillage providedAttackingVillage = new MockVillage();
        AttackingConditionsNotMetException expectedException = new AttackingConditionsNotMetException("Not enough " + UnitType.Shield.name() + " to attack this village");
        Exception thrownException = assertThrows(expectedException.getClass(), () -> this.combatServiceUnderTesting.checkHasEnoughUnitsToAttack(providedVillageUnits, providedAttackingVillage));
        assertThat(thrownException.getMessage(), is(expectedException.getMessage()));
    }

    @Test
    void should_ThrowAttackingConditionsNotMetExceptionWithNotEnoughUnits_when_checkHasEnoughUnitsToAttackIsCalledWithZeroUnits() {
        List<VillageUnit> providedVillageUnits = new ArrayList<>();
        providedVillageUnits.add(new VillageUnit(UnitFactory.getUnitStatic(UnitType.Shield.name()), 0));
        MockVillage providedAttackingVillage = new MockVillage();
        providedAttackingVillage.addUnit(UnitFactory.getUnitStatic(UnitType.Shield.name()), 0);
        AttackingConditionsNotMetException expectedException = new AttackingConditionsNotMetException("Not enough " + UnitType.Shield.name() + " to attack this village");
        Exception thrownException = assertThrows(expectedException.getClass(), () -> this.combatServiceUnderTesting.checkHasEnoughUnitsToAttack(providedVillageUnits, providedAttackingVillage));
        assertThat(thrownException.getMessage(), is(expectedException.getMessage()));
    }

    @Test
    void should_ThrowAttackingConditionsNotMetExceptionWithNotEnoughUnits_when_checkHasEnoughUnitsToAttackIsCalledWithTooFewUnitsInVillage() {
        List<VillageUnit> providedVillageUnits = new ArrayList<>();
        providedVillageUnits.add(new VillageUnit(UnitFactory.getUnitStatic(UnitType.Shield.name()), 100));
        MockVillage providedAttackingVillage = new MockVillage();
        providedAttackingVillage.addUnit(UnitFactory.getUnitStatic(UnitType.Shield.name()), 1);
        AttackingConditionsNotMetException expectedException = new AttackingConditionsNotMetException("Not enough " + UnitType.Shield.name() + " to attack this village");
        Exception thrownException = assertThrows(expectedException.getClass(), () -> this.combatServiceUnderTesting.checkHasEnoughUnitsToAttack(providedVillageUnits, providedAttackingVillage));
        assertThat(thrownException.getMessage(), is(expectedException.getMessage()));
    }

    @Test
    void should_ReturnWithNoErrors_when_checkHasEnoughUnitsToAttackIsValid() {
        CombatService combatServiceUnderTesting = mock(CombatService.class);
        List<VillageUnit> providedVillageUnits = new ArrayList<>();
        providedVillageUnits.add(new VillageUnit(UnitFactory.getUnitStatic(UnitType.Shield.name()), 1));
        MockVillage providedAttackingVillage = new MockVillage();
        providedAttackingVillage.addUnit(UnitFactory.getUnitStatic(UnitType.Shield.name()), 100);
        doCallRealMethod().when(combatServiceUnderTesting).checkHasEnoughUnitsToAttack(providedVillageUnits, providedAttackingVillage);
        combatServiceUnderTesting.checkHasEnoughUnitsToAttack(providedVillageUnits, providedAttackingVillage);
        verify(combatServiceUnderTesting, times(1)).checkHasEnoughUnitsToAttack(providedVillageUnits, providedAttackingVillage);
    }

    @Test
    void should_ThrowAttackingConditionsNotMetExceptionWithNotEnoughCapacityMessage_when_checkHasEnoughShipsToSendUnitsIsCalledWithLessShips () {
        List<VillageUnit> providedVillageUnitList = new ArrayList<>();
        Unit shipUnit1 = new BattleShip(); // 50
        Unit shipUnit2 = new TransportShip(); // 50
        Unit shipUnit3 = new DefenceShip(); // 50
        providedVillageUnitList.add(new VillageUnit(shipUnit1, 1));
        providedVillageUnitList.add(new VillageUnit(shipUnit2, 1));
        providedVillageUnitList.add(new VillageUnit(shipUnit3, 1));

        Scout unit1 = new Scout();
        Shield unit2 = new Shield();
        unit1.setPopulationRequiredPerUnit(1);
        unit2.setPopulationRequiredPerUnit(100);
        providedVillageUnitList.add(new VillageUnit(unit1, 200));
        providedVillageUnitList.add(new VillageUnit(unit2, 1000));

        AttackingConditionsNotMetException expectedException = new AttackingConditionsNotMetException("Not enough ship capacity for this attack");
        Exception thrownException = assertThrows(expectedException.getClass(), () -> this.combatServiceUnderTesting.checkHasEnoughShipsToSendUnits(providedVillageUnitList));
        assertThat(thrownException.getMessage(), is(expectedException.getMessage()));
    }

    @Test
    void should_ReturnWithNoErrors_when_checkHasEnoughShipsToSendUnitsIsValid () {
        CombatService combatServiceUnderTesting = mock(CombatService.class);
        List<VillageUnit> providedVillageUnitList = new ArrayList<>();
        Unit shipUnit1 = new BattleShip(); // 50
        Unit shipUnit2 = new TransportShip(); // 50
        Unit shipUnit3 = new DefenceShip(); // 50
        providedVillageUnitList.add(new VillageUnit(shipUnit1, 1));
        providedVillageUnitList.add(new VillageUnit(shipUnit2, 1));
        providedVillageUnitList.add(new VillageUnit(shipUnit3, 1));

        Scout unit1 = new Scout();
        Shield unit2 = new Shield();
        unit1.setPopulationRequiredPerUnit(1);
        unit2.setPopulationRequiredPerUnit(1);
        providedVillageUnitList.add(new VillageUnit(unit1, 1));
        providedVillageUnitList.add(new VillageUnit(unit2, 1));

        doCallRealMethod().when(combatServiceUnderTesting).checkHasEnoughShipsToSendUnits(providedVillageUnitList);
        combatServiceUnderTesting.checkHasEnoughShipsToSendUnits(providedVillageUnitList);
        verify(combatServiceUnderTesting, times(1)).checkHasEnoughShipsToSendUnits(providedVillageUnitList);
    }
}
