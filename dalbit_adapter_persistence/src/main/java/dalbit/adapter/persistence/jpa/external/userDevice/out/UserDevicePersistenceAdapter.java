package dalbit.adapter.persistence.jpa.external.userDevice.out;

import dalbit.adapter.persistence.jpa.external.userDevice.entity.UserDeviceJpaEntity;
import dalbit.adapter.persistence.jpa.external.userDevice.mapper.UserDeviceJpaMapper;
import dalbit.application.persistence.jpa.userDevice.port.DeleteUserDevicePort;
import dalbit.application.persistence.jpa.userDevice.port.LoadUserDevicePort;
import dalbit.application.persistence.jpa.userDevice.port.SaveUserDevicePort;
import dalbit.domain.userDevice.UserDevice;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserDevicePersistenceAdapter implements
    LoadUserDevicePort, SaveUserDevicePort, DeleteUserDevicePort {

    private final UserDeviceJpaRepository userDeviceJpaRepository;
    private final UserDeviceJpaMapper userDeviceJpaMapper;

    @Override
    public List<UserDevice> loadAllUserDeviceByUserId(Long userId) {
        return userDeviceJpaRepository.findAllByUserId(userId).stream()
            .map(userDeviceJpaMapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<UserDevice>  loadExistFcmTokenUserDevicesByUserId(Long userId) {
        return userDeviceJpaRepository.findValidDevicesByUserId(userId).stream()
            .map(userDeviceJpaMapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public Optional<UserDevice> loadByDeviceUniqueId(String deviceUniqueId) {
        return userDeviceJpaRepository.findByDeviceUniqueId(deviceUniqueId)
            .map(userDeviceJpaMapper::toDomain);
    }

    @Override
    public Optional<UserDevice> loadByUserIdAndDeviceUniqueId(Long userId, String deviceUniqueId) {
        return userDeviceJpaRepository.findByUserIdAndDeviceUniqueId(userId, deviceUniqueId)
            .map(userDeviceJpaMapper::toDomain);
    }

    @Override
    public UserDevice saveUserDevice(UserDevice userDevice) {
        UserDeviceJpaEntity entity = userDeviceJpaRepository.save(userDeviceJpaMapper.toEntity(userDevice));
        return userDeviceJpaMapper.toDomain(entity);
    }

    @Override
    public int deleteFcmTokens(List<String> tokens) {
        return userDeviceJpaRepository.nullifyFcmTokensIn(tokens);
    }

}
