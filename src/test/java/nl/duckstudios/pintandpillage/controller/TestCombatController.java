package nl.duckstudios.pintandpillage.controller;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import nl.duckstudios.pintandpillage.Exceptions.AttackingConditionsNotMetException;
import nl.duckstudios.pintandpillage.config.JwtTokenUtil;
import nl.duckstudios.pintandpillage.dao.TravelDao;
import nl.duckstudios.pintandpillage.entity.User;
import nl.duckstudios.pintandpillage.entity.VillageUnit;
import nl.duckstudios.pintandpillage.entity.travels.AttackCombatTravel;
import nl.duckstudios.pintandpillage.mocks.MockCombatController;
import nl.duckstudios.pintandpillage.mocks.MockVillage;
import nl.duckstudios.pintandpillage.model.AttackUnitData;
import nl.duckstudios.pintandpillage.model.AttackVillageData;
import nl.duckstudios.pintandpillage.service.*;
import nl.duckstudios.pintandpillage.testHelpers.AttackHelper;
import nl.duckstudios.pintandpillage.testHelpers.RequestHelper;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;


import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Tag("CombatController")
@WebMvcTest(CombatController.class)
@AutoConfigureMockMvc(addFilters = false)
public class TestCombatController {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ApplicationContext context;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtTokenUtil jwtTokenUtil;

    @MockBean
    private AuthenticationService authenticationService;

    @MockBean
    private AccountService accountService;

    @MockBean
    private VillageService villageService;

    @MockBean
    private CombatService combatService;

    @MockBean
    private DistanceService distanceService;

    @MockBean
    private TravelDao travelDao;

    private MockCombatController combatControllerUnderTesting;

    private RequestHelper requestHelper = new RequestHelper();

    private MockVillage toVillage = new MockVillage();

    private MockVillage fromVillage = new MockVillage();


    ObjectWriter ow = new ObjectMapper().setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY).writer().withDefaultPrettyPrinter();

    private AttackHelper attackHelper = new AttackHelper();

    private void setupVillagesStubs () {
        this.toVillage.setVillageId(1L);
        this.toVillage.setName("defending Village");
        this.toVillage.setPositionX(1);
        this.toVillage.setPositionY(1);

        this.fromVillage.setVillageId(2L);
        this.fromVillage.setName("attacking Village");
        this.fromVillage.setPositionX(1);
        this.fromVillage.setPositionY(1);
    }

    private void setupCombatControllerUnderTesting () {
        this.combatControllerUnderTesting = new MockCombatController(authenticationService,accountService,villageService, combatService, travelDao, distanceService);
    }

    @Test
    public void shouldThrowAttackingConditionsNotMetExceptionWhenTryingToAttackOwnVillage() throws Exception {
        setupVillagesStubs();
        AttackVillageData attackVillageData = attackHelper.generateAttackVillageData(true, toVillage.getVillageId(), toVillage.getVillageId());
        String json = ow.writeValueAsString(attackVillageData);
        this.mockMvc.perform(post("/api/combat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof AttackingConditionsNotMetException));
    }

    @Test
    public void shouldReturnAttackingVillageWhenACorrectRequestIsMade() throws Exception {
        setupVillagesStubs();
        User genericUser = new User();
        genericUser.setId(1L);
        AttackVillageData attackVillageData = attackHelper.generateAttackVillageData(true, toVillage.getVillageId(), fromVillage.getVillageId());
        when(this.authenticationService.getAuthenticatedUser()).thenReturn(genericUser);
        when(this.villageService.getVillage(eq(2L))).thenReturn(this.toVillage);
        when(this.villageService.getVillage(eq(1L))).thenReturn(this.fromVillage);
        String json = ow.writeValueAsString(attackVillageData);
        this.mockMvc.perform(post("/api/combat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("\"name\":")));
    }

    @Test
    public void shouldMatchTheSpeedOfAShieldUnitWhenCreatingCombatTravelWithMultipleUnits () {
        setupVillagesStubs();
        setupCombatControllerUnderTesting();
        AttackVillageData attackVillageData = this.attackHelper.generateAttackVillageData(false, toVillage.getVillageId(), fromVillage.getVillageId());
        when(this.distanceService.calculateDistance(any(), any())).thenReturn(1);
        when(this.combatService.convertToVillageUnits(any())).thenReturn(this.attackHelper.generateVillageUnits());
        List<VillageUnit> convertedVillageUnits = this.combatService.convertToVillageUnits(attackVillageData);
        AttackCombatTravel actualAttackCombatTravel = this.combatControllerUnderTesting.createCombatTravel(convertedVillageUnits, fromVillage, toVillage);
        LocalDateTime actualTraveltime = actualAttackCombatTravel.getTimeOfArrival();
        LocalDateTime expectedTravelTime = LocalDateTime.now().plusSeconds((1000 / 5));
        assertThat(actualTraveltime.withNano(0), is(expectedTravelTime.withNano(0)));
    }
}