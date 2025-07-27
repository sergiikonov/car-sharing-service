package mate.academy.carsharingservice.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import mate.academy.carsharingservice.model.user.User;
import mate.academy.carsharingservice.repository.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTests {
    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Should find user by email returns requested user")
    @Sql(scripts = "classpath:database/users/add-three-users.sql")
    @Sql(scripts = "classpath:database/cleanup-test-data.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findByEmail_whenUserExist_shouldReturnCorrectUser() {
        User user = userRepository.findByEmail("user1@example.com").get();
        Long actual = user.getId();
        Long expected = 10L;
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Should check if user exists by email")
    @Sql(scripts = "classpath:database/users/add-three-users.sql")
    @Sql(scripts = "classpath:database/cleanup-test-data.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void existsByEmail_whenUserExistsByEmail_shouldReturnTrue() {
        Boolean actual = userRepository.existsByEmail("user2@example.com");
        Boolean expected = true;
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Should check if user not exists by unknown email in db")
    @Sql(scripts = "classpath:database/users/add-three-users.sql")
    @Sql(scripts = "classpath:database/cleanup-test-data.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void existsByEmail_whenUserUnExistByEmail_shouldReturnFalse() {
        Boolean actual = userRepository.existsByEmail("unknown@exapmle.com");
        Boolean expected = false;
        assertEquals(expected, actual);
    }
}
