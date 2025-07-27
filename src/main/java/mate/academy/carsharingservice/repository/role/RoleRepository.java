package mate.academy.carsharingservice.repository.role;

import java.util.Optional;
import mate.academy.carsharingservice.model.user.Role;
import mate.academy.carsharingservice.model.user.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName name);
}
