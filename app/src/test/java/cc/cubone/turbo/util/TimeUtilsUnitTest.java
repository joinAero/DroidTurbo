package cc.cubone.turbo.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TimeUtilsUnitTest {
    @Test
    public void readableSeconds_isExpected() throws Exception {
        assertEquals("0s", TimeUtils.readableSeconds(0));

        assertEquals("1s", TimeUtils.readableSeconds(1));
        assertEquals("1m", TimeUtils.readableSeconds(60));
        assertEquals("1h", TimeUtils.readableSeconds(3600));
        assertEquals("1d", TimeUtils.readableSeconds(86400));

        assertEquals("1m1s", TimeUtils.readableSeconds(61));
        assertEquals("1h1s", TimeUtils.readableSeconds(3601));
        assertEquals("1h1m", TimeUtils.readableSeconds(3660));
        assertEquals("1h1m1s", TimeUtils.readableSeconds(3661));
        assertEquals("2h2m2s", TimeUtils.readableSeconds(7322));
        assertEquals("1d1m1s", TimeUtils.readableSeconds(86461));
    }
}
