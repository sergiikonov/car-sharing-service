package mate.academy.carsharingservice.repository.role;

import mate.academy.carsharingservice.model.Role;
import mate.academy.carsharingservice.model.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName name);
}
