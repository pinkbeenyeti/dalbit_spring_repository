package dalbit.application.persistence.jpa.user.port;

import dalbit.domain.user.User;

public interface SaveUserPort {
    User saveUser(User user);
}
