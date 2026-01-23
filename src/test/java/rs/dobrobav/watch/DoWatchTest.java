package rs.dobrobav.watch;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import rs.dobrobav.watch.DoWatch;

class DoWatchTest {

    @Test
    void watchCanBeInstantiated() {
        DoWatch watch = new rs.dobrobav.watch.DoWatch();
        assertNotNull(watch, "DoWatch instance should not be null.");
    }
}
