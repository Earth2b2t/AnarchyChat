package earth2b2t.anarchychat.ignore;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class Ignore {

    private final String name;
    private final IgnoreType ignoreType;
}
