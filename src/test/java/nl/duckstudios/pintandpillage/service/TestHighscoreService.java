package nl.duckstudios.pintandpillage.service;

import nl.duckstudios.pintandpillage.dao.UserDAO;
import nl.duckstudios.pintandpillage.mocks.MockHighscoreService;
import nl.duckstudios.pintandpillage.mocks.MockVillage;
import nl.duckstudios.pintandpillage.mocks.MockWorldVillage;
import nl.duckstudios.pintandpillage.model.UserHighscore;
import nl.duckstudios.pintandpillage.testHelpers.VillagesHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Tag("HighScoreService")
public class TestHighscoreService {

        @Mock
        MockVillage mockVillage = new MockVillage();

        @Mock
        UserDAO userDAOMock;

        @Mock
        VillageService villageServiceMock;

        MockHighscoreService highscoreServiceUnderTesting;

        VillagesHelper villagesHelper = new VillagesHelper();

        List<MockWorldVillage> mockedWorldVillageList = villagesHelper.getMockWorldVillages();

        @BeforeEach
        void initialize () {
                when(this.villageServiceMock.getWorldVillages()).thenReturn(this.villagesHelper.convertMockWorldVillagesToWorldVillages(this.mockedWorldVillageList));
                this.highscoreServiceUnderTesting = new MockHighscoreService(this.userDAOMock, this.villageServiceMock);
        }

        @Test
        public void should_ReturnAnEmptyArrayList_when_getHighscoreIsCalledAndThereAreNoWorldVillages() {
                when(this.villageServiceMock.getWorldVillages()).thenReturn(new ArrayList<>());
                int expectedSize = 0;
                int actualHighscoresSize = this.highscoreServiceUnderTesting.getHighscore().size();
                assertThat(actualHighscoresSize, is(expectedSize));
        }

        @Test
        public void should_ReturnAnEmptyArrayList_when_getHighscoreIsCalledAndThereAreNoWorldVillagesWithUsers() {
                int expectedSize = 0;
                int actualHighscoresSize = this.highscoreServiceUnderTesting.getHighscore().size();
                assertThat(actualHighscoresSize, is(expectedSize));
        }

        @Test
        public void should_ReturnAnArrayListWithTenHighscores_when_getHighscoreIsCalledWithTenVillages() {
                when(this.userDAOMock.findUsernameById(anyLong())).thenReturn(
                        Optional.ofNullable(this.mockedWorldVillageList.get(0).getVillageUser()),
                        Optional.ofNullable(this.mockedWorldVillageList.get(1).getVillageUser()),
                        Optional.ofNullable(this.mockedWorldVillageList.get(2).getVillageUser()),
                        Optional.ofNullable(this.mockedWorldVillageList.get(3).getVillageUser()),
                        Optional.ofNullable(this.mockedWorldVillageList.get(4).getVillageUser()),
                        Optional.ofNullable(this.mockedWorldVillageList.get(5).getVillageUser()),
                        Optional.ofNullable(this.mockedWorldVillageList.get(6).getVillageUser()),
                        Optional.ofNullable(this.mockedWorldVillageList.get(7).getVillageUser()),
                        Optional.ofNullable(this.mockedWorldVillageList.get(8).getVillageUser()),
                        Optional.ofNullable(this.mockedWorldVillageList.get(9).getVillageUser())
                );
                int expectedSize = 10;
                int actualHighscoresSize = this.highscoreServiceUnderTesting.getHighscore().size();
                assertThat(actualHighscoresSize, is(expectedSize));
        }

        @Test
        public void should_ReturnAnArrayListWithTheCorrectPoints_when_getHighscoreIsCalledWithAValidVillageList() {
                when(this.userDAOMock.findUsernameById(anyLong())).thenReturn(
                        Optional.ofNullable(this.mockedWorldVillageList.get(0).getVillageUser()),
                        Optional.ofNullable(this.mockedWorldVillageList.get(1).getVillageUser()),
                        Optional.ofNullable(this.mockedWorldVillageList.get(2).getVillageUser()),
                        Optional.ofNullable(this.mockedWorldVillageList.get(3).getVillageUser()),
                        Optional.ofNullable(this.mockedWorldVillageList.get(4).getVillageUser()),
                        Optional.ofNullable(this.mockedWorldVillageList.get(5).getVillageUser()),
                        Optional.ofNullable(this.mockedWorldVillageList.get(6).getVillageUser()),
                        Optional.ofNullable(this.mockedWorldVillageList.get(7).getVillageUser()),
                        Optional.ofNullable(this.mockedWorldVillageList.get(8).getVillageUser()),
                        Optional.ofNullable(this.mockedWorldVillageList.get(9).getVillageUser())
                );
                List<UserHighscore> actualHighscores = this.highscoreServiceUnderTesting.getHighscore();

                for (int i = 0; i < 10; i++) {
                        int expectedPointsAmount = i;
                        int actualPoints = actualHighscores.get(i).totalPoints;
                        assertThat(actualPoints, is(expectedPointsAmount));
                }
        }
}
