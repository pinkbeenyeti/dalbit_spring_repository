package dalbit.domain.user;

import java.util.UUID;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class User {

    private final Long id;
    private final String externalId;
    private final String providerId;
    private UserName name;
    private final UserEmail email;
    private final Role role;

    @Builder
    private User(Long id, String externalId, String providerId, UserName name, UserEmail email, Role role) {
        this.id = id;
        this.externalId = externalId;
        this.providerId = providerId;
        this.name = name;
        this.email = email;
        this.role = role;
    }

    public static User register(String providerId, UserName name, UserEmail email, Role role) {
        return new User(null, UUID.randomUUID().toString() ,providerId, name, email, role);
    }

    public void updateName(UserName newName) {
        this.name = newName;
    }
}
