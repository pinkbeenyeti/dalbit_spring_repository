package dalbit.domain.device;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DeviceType {

    MOON_LAMP("달빛 무드등");

    private final String description;
}
