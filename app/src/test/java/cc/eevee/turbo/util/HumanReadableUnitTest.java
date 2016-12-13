package cc.eevee.turbo.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class HumanReadableUnitTest {
    @Test
    public void readableSeconds_isExpected() throws Exception {
        assertEquals("0s", HumanReadable.seconds(0));

        assertEquals("1s", HumanReadable.seconds(1));
        assertEquals("1m", HumanReadable.seconds(60));
        assertEquals("1h", HumanReadable.seconds(3600));
        assertEquals("1d", HumanReadable.seconds(86400));

        assertEquals("1m1s", HumanReadable.seconds(61));
        assertEquals("1h1s", HumanReadable.seconds(3601));
        assertEquals("1h1m", HumanReadable.seconds(3660));
        assertEquals("1h1m1s", HumanReadable.seconds(3661));
        assertEquals("2h2m2s", HumanReadable.seconds(7322));
        assertEquals("1d1m1s", HumanReadable.seconds(86461));
    }
}
