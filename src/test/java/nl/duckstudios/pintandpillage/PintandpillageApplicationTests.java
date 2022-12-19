package nl.duckstudios.pintandpillage;

import org.junit.jupiter.api.Test;
import org.junit.platform.suite.api.IncludeTags;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;
import org.springframework.boot.test.context.SpringBootTest;

@Suite
@SelectPackages({ "nl.duckstudios.pintandpillage.controller", "nl.duckstudios.pintandpillage.entity", "nl.duckstudios.pintandpillage.service"})
@SpringBootTest
class PintandpillageApplicationTests {

	@Test
	void contextLoads() {
	}

}
