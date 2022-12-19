package nl.duckstudios.pintandpillage.service;

import nl.duckstudios.pintandpillage.Exceptions.UnauthorizedException;
import nl.duckstudios.pintandpillage.entity.User;
import nl.duckstudios.pintandpillage.entity.Village;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Random;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("AccountService")
public class TestAccountService {

    @Mock
    private Village villageMock;
    @Mock
    private User userMock;
    private long correctUserId = new Random().nextLong();
    private long incorrectUserId;

    private AccountService accountServiceUnderTesting = new AccountService();

    @BeforeEach
    void generateIncorrectUserId () {
        this.incorrectUserId = new Random().nextLong();
        if (this.incorrectUserId == correctUserId) {
            this.generateIncorrectUserId();
        }
    }

    void setupVillageStubWithIncorrectUserId() {
        when(this.villageMock.getUser()).thenReturn(this.userMock);
        when(this.userMock.getId()).thenReturn(this.correctUserId);
    }

    @Test
    void should_throwUnauthorizedException_when_checkIsCorrectUserIsCalledWithIncorrectUserId() {
        this.setupVillageStubWithIncorrectUserId();
        UnauthorizedException expectedException = new UnauthorizedException("No the owner of this village");
        Exception thrownException = assertThrows(expectedException.getClass(), () -> this.accountServiceUnderTesting.checkIsCorrectUser(this.incorrectUserId, villageMock));
        assertThat(thrownException.getMessage(), is(expectedException.getMessage()));
    }

}
