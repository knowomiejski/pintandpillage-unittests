package nl.duckstudios.pintandpillage.tdd;

import nl.duckstudios.pintandpillage.entity.buildings.Building;
import nl.duckstudios.pintandpillage.entity.buildings.ResourceVault;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

@Tag("TestResourceVault")
public class TestResourceVault {

    ResourceVault resourceVaultUnderTesting;

    @BeforeEach
    public void initResourceVault () {
        this.resourceVaultUnderTesting = new ResourceVault();
        resourceVaultUnderTesting.setLevel(0);
    }

    @Test
    public void should_BeOfClassTypeResourceVault_when_Initialized () {
        Class<ResourceVault> expectedClass = ResourceVault.class;

        assertThat(resourceVaultUnderTesting, instanceOf(expectedClass));
    }

    @Test
    public void should_BeAssignableToTypeBuilding_when_Initialized () {
        boolean expectedValue = true;

        assertThat(Building.class.isAssignableFrom(resourceVaultUnderTesting.getClass()), is(expectedValue));
    }

    @Test
    public void should_ReturnStringVault_when_GetNameIsCalled () {
        String expectedValue = "Vault";
        String actualValue = resourceVaultUnderTesting.getName();
        assertThat(actualValue, is(expectedValue));
    }

    @Test
    public void should_ReturnA10secondConstructionTime_when_getConstructionTimeIsCalledAfterInitialization() {
        long expectedValue = 10;
        long actualValue = resourceVaultUnderTesting.getConstructionTimeSeconds();
        assertThat(actualValue, is(expectedValue));
    }

    @Test
    public void should_ReturnA30secondConstructionTime_when_getConstructionTimeIsCalledWithLevelOne() {
        long expectedValue = 30;
        resourceVaultUnderTesting.setLevel(1);
        long actualValue = resourceVaultUnderTesting.getConstructionTimeSeconds();
        assertThat(actualValue, is(expectedValue));
    }

    // 1. Vault is een Building
    // 2. Vault moet een naam "Vault" hebben
    // 4. Een vault moet tijd kosten om te bouwen 10 sec
    // 5. Een vault moet grondstoffen kosten om te bouwen 10 wood 25 stone
    // 6. Er moet een tijd geset worden voor de volgende upgrade
    // 7. Er moeten grondstoffen geset worden voor de volgende upgrade
    // 8. Het plunderen van resources zorgt dat 60% van de resources verdwijnen

}
