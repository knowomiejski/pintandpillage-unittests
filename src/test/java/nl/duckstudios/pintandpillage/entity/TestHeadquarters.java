package nl.duckstudios.pintandpillage.entity;

import nl.duckstudios.pintandpillage.helper.ResourceManager;
import nl.duckstudios.pintandpillage.mocks.MockHeadquarters;
import nl.duckstudios.pintandpillage.mocks.MockHouse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@Tag("House")
public class TestHeadquarters {
    @Mock
    private Village villageMock;
    private MockHeadquarters headquartersUnderTesting;
    @Mock
    private ResourceManager mockResourceManger = new ResourceManager();

    @BeforeEach
    public void initHeadquartersUnderTesting(){
        this.headquartersUnderTesting = new MockHeadquarters();
        this.headquartersUnderTesting.setVillage(villageMock);
        this.headquartersUnderTesting.setResourceManager(this.mockResourceManger);
    }
}
