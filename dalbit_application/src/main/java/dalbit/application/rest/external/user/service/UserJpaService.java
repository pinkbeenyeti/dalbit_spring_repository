
package dalbit.application.rest.external.user.service;

import dalbit.application.rest.external.user.useCase.GetUserInfoUseCase;
import dalbit.application.rest.external.user.useCase.UpdateUserUseCae;
import dalbit.application.persistence.jpa.user.port.LoadUserPort;
import dalbit.application.persistence.jpa.user.port.SaveUserPort;
import dalbit.domain.common.error.DalbitException;
import dalbit.domain.common.error.ErrorCode;
import dalbit.domain.user.User;
import dalbit.domain.user.UserName;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserJpaService implements GetUserInfoUseCase, UpdateUserUseCae {

    private final LoadUserPort loadUserPort;
    private final SaveUserPort saveUserPort;

    @Override
    @Transactional(readOnly = true)
    public User getUserInfo(Long userId) {
        return loadUserPort.loadUserByUserId(userId)
            .orElseThrow(() -> new DalbitException(ErrorCode.NOT_EXIST_USER));
    }

    @Override
    @Transactional
    public void updateUserName(Long userId, String newName) {
        User user = loadUserPort.loadUserByUserId(userId)
            .orElseThrow(() -> new DalbitException(ErrorCode.NOT_EXIST_USER));
        user.updateName(UserName.of(newName));
        saveUserPort.saveUser(user);
    }
}
