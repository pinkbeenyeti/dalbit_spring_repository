package dalbit.domain.device.moon;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Color {

    RED("#FF0000", 255, 0, 0),
    GREEN("#00FF00", 0, 255, 0),
    BLUE("#0000FF", 0, 0, 255),
    YELLOW("#FFFF00", 255, 255, 0),
    GOLD("#EAB308", 234, 179,8),
    HOTPINK("#EC4899", 236, 72, 153),
    INDIGO("#6366F1", 99, 102, 241),
    CERULEAN("#0EA5E9", 14, 165, 233),
    APPLE("#22C55E", 34, 197, 94),
    WHITE("#FFFFFF", 236, 72, 153),
    BLACK("#000000", 0, 0, 0); // LED Off

    private final String hexCode;
    private final int r, g, b;
}
