package task.test.kataryna.dmytro.match.otto;

import com.squareup.otto.Bus;

/**
 * Created by dmytroKataryna on 20.01.16.
 */
public final class BusProvider {

    private static final Bus BUS = new Bus();

    public static Bus getInstance() {
        return BUS;
    }

    private BusProvider() {
    }
}
