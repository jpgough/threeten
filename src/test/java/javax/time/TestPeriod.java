/*
 * Copyright (c) 2008-2012, Stephen Colebourne & Michael Nascimento Santos
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 *  * Neither the name of JSR-310 nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package javax.time;

import static javax.time.calendrical.LocalPeriodUnit.DAYS;
import static javax.time.calendrical.LocalPeriodUnit.HALF_DAYS;
import static javax.time.calendrical.LocalPeriodUnit.HOURS;
import static javax.time.calendrical.LocalPeriodUnit.MICROS;
import static javax.time.calendrical.LocalPeriodUnit.MILLIS;
import static javax.time.calendrical.LocalPeriodUnit.MINUTES;
import static javax.time.calendrical.LocalPeriodUnit.MONTHS;
import static javax.time.calendrical.LocalPeriodUnit.NANOS;
import static javax.time.calendrical.LocalPeriodUnit.SECONDS;
import static javax.time.calendrical.LocalPeriodUnit.YEARS;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test.
 */
@Test
public class TestPeriod {

//    private static final BigInteger MAX_BINT = BigInteger.valueOf(Integer.MAX_VALUE);
//    private static final BigInteger BINT_24 = BigInteger.valueOf(24);
//    private static final BigInteger BINT_60 = BigInteger.valueOf(60);
//    private static final BigInteger BINT_1BN = BigInteger.valueOf(1000000000L);

    //-----------------------------------------------------------------------
    // basics
    //-----------------------------------------------------------------------
    public void test_interfaces() {
        assertTrue(Serializable.class.isAssignableFrom(Period.class));
    }

    @DataProvider(name="serialization")
    Object[][] data_serialization() {
        return new Object[][] {
            {Period.ZERO},
            {Period.of(0, DAYS)},
            {Period.of(1, DAYS)},
            {Period.of(1, 2, 3, 4, 5, 6)},
        };
    }

    @Test(dataProvider="serialization")
    public void test_serialization(Period period) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(period);
        oos.close();
        
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(
                baos.toByteArray()));
        if (period.isZero()) {
            assertSame(ois.readObject(), period);
        } else {
            assertEquals(ois.readObject(), period);
        }
    }

    public void test_immutable() {
        Class<Period> cls = Period.class;
        assertTrue(Modifier.isPublic(cls.getModifiers()));
        assertTrue(Modifier.isFinal(cls.getModifiers()));
        Field[] fields = cls.getDeclaredFields();
        for (Field field : fields) {
            if (Modifier.isStatic(field.getModifiers()) == false && !Modifier.isTransient(field.getModifiers())) {
                assertTrue(Modifier.isPrivate(field.getModifiers()));
                assertTrue(Modifier.isFinal(field.getModifiers()));
            }
        }
        Constructor<?>[] cons = cls.getDeclaredConstructors();
        for (Constructor<?> con : cons) {
            assertTrue(Modifier.isPrivate(con.getModifiers()));
        }
    }

    //-----------------------------------------------------------------------
    // factories
    //-----------------------------------------------------------------------
    public void factory_zeroSingleton() {
        assertSame(Period.ZERO, Period.ZERO);
        assertSame(Period.of(0, 0, 0, 0, 0, 0), Period.ZERO);
        assertSame(Period.of(0, 0, 0, 0, 0, 0, 0), Period.ZERO);
        assertSame(Period.ofDate(0, 0, 0), Period.ZERO);
        assertSame(Period.ofTime(0, 0, 0), Period.ZERO);
        assertSame(Period.ofTime(0, 0, 0, 0), Period.ZERO);
        assertSame(Period.of(0, YEARS), Period.ZERO);
        assertSame(Period.of(0, MONTHS), Period.ZERO);
        assertSame(Period.of(0, DAYS), Period.ZERO);
        assertSame(Period.of(0, HOURS), Period.ZERO);
        assertSame(Period.of(0, MINUTES), Period.ZERO);
        assertSame(Period.of(0, SECONDS), Period.ZERO);
        assertSame(Period.of(0, NANOS), Period.ZERO);
    }

    //-----------------------------------------------------------------------
    // of(PeriodProvider)
    //-----------------------------------------------------------------------
    public void factory_of_ints() {
        assertPeriod(Period.of(1, 2, 3, 4, 5, 6), 1, 2, 3, 4, 5, 6, 0);
        assertPeriod(Period.of(0, 2, 3, 4, 5, 6), 0, 2, 3, 4, 5, 6, 0);
        assertPeriod(Period.of(1, 0, 0, 0, 0, 0), 1, 0, 0, 0, 0, 0, 0);
        assertPeriod(Period.of(0, 0, 0, 0, 0, 0), 0, 0, 0, 0, 0, 0, 0);
        assertPeriod(Period.of(-1, -2, -3, -4, -5, -6), -1, -2, -3, -4, -5, -6, 0);
    }

    //-----------------------------------------------------------------------
    // ofDate
    //-----------------------------------------------------------------------
    public void factory_ofDate_ints() {
        assertPeriod(Period.ofDate(1, 2, 3), 1, 2, 3, 0, 0, 0, 0);
        assertPeriod(Period.ofDate(0, 2, 3), 0, 2, 3, 0, 0, 0, 0);
        assertPeriod(Period.ofDate(1, 0, 0), 1, 0, 0, 0, 0, 0, 0);
        assertPeriod(Period.ofDate(0, 0, 0), 0, 0, 0, 0, 0, 0, 0);
        assertPeriod(Period.ofDate(-1, -2, -3), -1, -2, -3, 0, 0, 0, 0);
    }

    //-----------------------------------------------------------------------
    // ofTime
    //-----------------------------------------------------------------------
    public void factory_ofTime_3ints() {
        assertPeriod(Period.ofTime(1, 2, 3), 0, 0, 0, 1, 2, 3, 0);
        assertPeriod(Period.ofTime(0, 2, 3), 0, 0, 0, 0, 2, 3, 0);
        assertPeriod(Period.ofTime(1, 0, 0), 0, 0, 0, 1, 0, 0, 0);
        assertPeriod(Period.ofTime(0, 0, 0), 0, 0, 0, 0, 0, 0, 0);
        assertPeriod(Period.ofTime(-1, -2, -3), 0, 0, 0, -1, -2, -3, 0);
    }

    public void factory_ofTime_4ints() {
        assertPeriod(Period.ofTime(1, 2, 3, 4), 0, 0, 0, 1, 2, 3, 4);
        assertPeriod(Period.ofTime(0, 2, 3, 4), 0, 0, 0, 0, 2, 3, 4);
        assertPeriod(Period.ofTime(1, 0, 0, 0), 0, 0, 0, 1, 0, 0, 0);
        assertPeriod(Period.ofTime(0, 0, 0, 0), 0, 0, 0, 0, 0, 0, 0);
        assertPeriod(Period.ofTime(-1, -2, -3, -4), 0, 0, 0, -1, -2, -3, -4);
    }

    //-----------------------------------------------------------------------
    // of one field
    //-----------------------------------------------------------------------
    public void test_factory_of_intPeriodUnit() {
        assertEquals(Period.of(1, YEARS), Period.of(1, YEARS));
        assertEquals(Period.of(2, MONTHS), Period.of(2, MONTHS));
        assertEquals(Period.of(3, DAYS), Period.of(3, DAYS));
        
        assertEquals(Period.of(1, HALF_DAYS), Period.of(12, HOURS));
        assertEquals(Period.of(Integer.MAX_VALUE / (3600 * 8), HOURS), Period.of(Integer.MAX_VALUE / (3600 * 8), HOURS));
        assertEquals(Period.of(-1, MINUTES), Period.of(-1, MINUTES));
        assertEquals(Period.of(-2, SECONDS), Period.of(-2, SECONDS));
        assertEquals(Period.of(Integer.MIN_VALUE, NANOS), Period.of(Integer.MIN_VALUE, NANOS));
        assertEquals(Period.of(2, MILLIS), Period.of(2000000, NANOS));
        assertEquals(Period.of(2, MICROS), Period.of(2000, NANOS));
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_factory_of_intPeriodUnit_null() {
        Period.of(1, null);
    }

    //-----------------------------------------------------------------------
    public void factory_years() {
        assertPeriod(Period.of(1, YEARS), 1, 0, 0, 0, 0, 0, 0);
        assertPeriod(Period.of(0, YEARS), 0, 0, 0, 0, 0, 0, 0);
        assertPeriod(Period.of(-1, YEARS), -1, 0, 0, 0, 0, 0, 0);
        assertPeriod(Period.of(Integer.MAX_VALUE, YEARS), Integer.MAX_VALUE, 0, 0, 0, 0, 0, 0);
        assertPeriod(Period.of(Integer.MIN_VALUE, YEARS), Integer.MIN_VALUE, 0, 0, 0, 0, 0, 0);
    }

    public void factory_months() {
        assertPeriod(Period.of(1, MONTHS), 0, 1, 0, 0, 0, 0, 0);
        assertPeriod(Period.of(0, MONTHS), 0, 0, 0, 0, 0, 0, 0);
        assertPeriod(Period.of(-1, MONTHS), 0, -1, 0, 0, 0, 0, 0);
        assertPeriod(Period.of(Integer.MAX_VALUE, MONTHS), 0, Integer.MAX_VALUE, 0, 0, 0, 0, 0);
        assertPeriod(Period.of(Integer.MIN_VALUE, MONTHS), 0, Integer.MIN_VALUE, 0, 0, 0, 0, 0);
    }

    public void factory_days() {
        assertPeriod(Period.of(1, DAYS), 0, 0, 1, 0, 0, 0, 0);
        assertPeriod(Period.of(0, DAYS), 0, 0, 0, 0, 0, 0, 0);
        assertPeriod(Period.of(-1, DAYS), 0, 0, -1, 0, 0, 0, 0);
        assertPeriod(Period.of(Integer.MAX_VALUE, DAYS), 0, 0, Integer.MAX_VALUE, 0, 0, 0, 0);
        assertPeriod(Period.of(Integer.MIN_VALUE, DAYS), 0, 0, Integer.MIN_VALUE, 0, 0, 0, 0);
    }

    public void factory_hours() {
        assertPeriod(Period.of(1, HOURS), 0, 0, 0, 1, 0, 0, 0);
        assertPeriod(Period.of(0, HOURS), 0, 0, 0, 0, 0, 0, 0);
        assertPeriod(Period.of(-1, HOURS), 0, 0, 0, -1, 0, 0, 0);
        assertPeriod(Period.of(Integer.MAX_VALUE / (3600 * 8), HOURS), 0, 0, 0, Integer.MAX_VALUE / (3600 * 8), 0, 0, 0);
        assertPeriod(Period.of(Integer.MIN_VALUE / (3600 * 8), HOURS), 0, 0, 0, Integer.MIN_VALUE / (3600 * 8), 0, 0, 0);
    }

    public void factory_minutes() {
        assertPeriod(Period.of(1, MINUTES), 0, 0, 0, 0, 1, 0, 0);
        assertPeriod(Period.of(0, MINUTES), 0, 0, 0, 0, 0, 0, 0);
        assertPeriod(Period.of(-1, MINUTES), 0, 0, 0, 0, -1, 0, 0);
        int val = Integer.MAX_VALUE / (60 * 8);
        assertPeriod(Period.of(val, MINUTES), 0, 0, 0,
                        (int) (val / 60L),
                        (int) (val % 60),
                        0, 0);
        val = Integer.MIN_VALUE / (60 * 8);
        assertPeriod(Period.of(val, MINUTES), 0, 0, 0,
                        (int) (val / 60L),
                        (int) (val % 60),
                        0, 0);
    }

    public void factory_seconds() {
        assertPeriod(Period.of(1, SECONDS), 0, 0, 0, 0, 0, 1, 0);
        assertPeriod(Period.of(0, SECONDS), 0, 0, 0, 0, 0, 0, 0);
        assertPeriod(Period.of(-1, SECONDS), 0, 0, 0, 0, 0, -1, 0);
        assertPeriod(Period.of(Integer.MAX_VALUE, SECONDS), 0, 0, 0,
                        (int) (Integer.MAX_VALUE / 3600L),
                        (int) ((Integer.MAX_VALUE / 60L) % 60),
                        (int) (Integer.MAX_VALUE % 60),
                        0);
        assertPeriod(Period.of(Integer.MIN_VALUE, SECONDS), 0, 0, 0,
                        (int) (Integer.MIN_VALUE / 3600L),
                        (int) ((Integer.MIN_VALUE / 60L) % 60),
                        (int) (Integer.MIN_VALUE % 60),
                        0);
    }

    public void factory_nanos() {
        assertPeriod(Period.of(1, NANOS), 0, 0, 0, 0, 0, 0, 1);
        assertPeriod(Period.of(0, NANOS), 0, 0, 0, 0, 0, 0, 0);
        assertPeriod(Period.of(-1, NANOS), 0, 0, 0, 0, 0, 0, -1);
        assertPeriod(Period.of(Long.MAX_VALUE, NANOS), 0, 0, 0,
                        (int) (Long.MAX_VALUE / 3600_000_000_000L),
                        (int) ((Long.MAX_VALUE / 60_000_000_000L) % 60),
                        (int) ((Long.MAX_VALUE / 1_000_000_000L) % 60),
                        Long.MAX_VALUE % 1_000_000_000L);
        assertPeriod(Period.of(Long.MIN_VALUE, NANOS), 0, 0, 0,
                        (int) (Long.MIN_VALUE / 3600_000_000_000L),
                        (int) ((Long.MIN_VALUE / 60_000_000_000L) % 60),
                        (int) ((Long.MIN_VALUE / 1_000_000_000L) % 60),
                        Long.MIN_VALUE % 1_000_000_000L);
    }

    //-----------------------------------------------------------------------
    // of(Duration)
    //-----------------------------------------------------------------------
    public void factory_duration() {
        assertPeriod(Period.of(Duration.ofSeconds(2, 3)), 0, 0, 0, 0, 0, 2, 3);
        assertPeriod(Period.of(Duration.ofSeconds(59, 3)), 0, 0, 0, 0, 0, 59, 3);
        assertPeriod(Period.of(Duration.ofSeconds(60, 3)), 0, 0, 0, 0, 1, 0, 3);
        assertPeriod(Period.of(Duration.ofSeconds(61, 3)), 0, 0, 0, 0, 1, 1, 3);
        assertPeriod(Period.of(Duration.ofSeconds(3599, 3)), 0, 0, 0, 0, 59, 59, 3);
        assertPeriod(Period.of(Duration.ofSeconds(3600, 3)), 0, 0, 0, 1, 0, 0, 3);
    }

    public void factory_duration_negative() {
        assertPeriod(Period.of(Duration.ofSeconds(-2, 3)), 0, 0, 0, 0, 0, -1, -999999997);
        assertPeriod(Period.of(Duration.ofSeconds(-59, 3)), 0, 0, 0, 0, 0, -58, -999999997);
        assertPeriod(Period.of(Duration.ofSeconds(-60, 3)), 0, 0, 0, 0, 0, -59, -999999997);
        assertPeriod(Period.of(Duration.ofSeconds(-60, -3)), 0, 0, 0, 0, -1, 0, -3);
        
        assertPeriod(Period.of(Duration.ofSeconds(2, -3)), 0, 0, 0, 0, 0, 1, 999999997);
    }

    public void factory_duration_big() {
        Duration dur = Duration.ofSeconds(Integer.MAX_VALUE, 3);
        long secs = Integer.MAX_VALUE;
        assertPeriod(Period.of(dur), 0, 0, 0, (int) (secs / 3600), (int) ((secs % 3600) / 60), (int) (secs % 60), 3);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_duration_null() {
        Period.of((Duration) null);
    }

    //-----------------------------------------------------------------------
    // between
    //-----------------------------------------------------------------------
    @DataProvider(name="betweenDates")
    Object[][] data_betweenDates() {
        return new Object[][] {
            {2010, 1, 1, 2010, 1, 1,  0, 0, 0},
            {2010, 1, 1, 2010, 1, 2,  0, 0, 1},
            {2010, 1, 1, 2010, 2, 1,  0, 1, 0},
            {2010, 1, 1, 2010, 2, 2,  0, 1, 1},
            {2010, 1, 1, 2011, 1, 1,  1, 0, 0},
            
            {2010, 6, 12, 2010, 1, 1,  0, -5, -11},
            {2010, 6, 12, 2010, 1, 2,  0, -5, -10},
            {2010, 6, 12, 2010, 2, 1,  0, -4, -11},
            {2010, 6, 12, 2010, 9, 24,  0, 3, 12},
            
            {2010, 6, 12, 2009, 1, 1,  -1, -5, -11},
            {2010, 6, 12, 2009, 1, 2,  -1, -5, -10},
            {2010, 6, 12, 2009, 2, 1,  -1, -4, -11},
            {2010, 6, 12, 2009, 9, 24,  0, -9, 12},
            
            {2010, 6, 12, 2008, 1, 1,  -2, -5, -11},
            {2010, 6, 12, 2008, 1, 2,  -2, -5, -10},
            {2010, 6, 12, 2008, 2, 1,  -2, -4, -11},
            {2010, 6, 12, 2008, 9, 24,  -1, -9, 12},
        };
    }

    @Test(dataProvider="betweenDates")
    public void factory_between_LocalDate(int y1, int m1, int d1, int y2, int m2, int d2, int ye, int me, int de) {
        LocalDate start = LocalDate.of(y1, m1, d1);
        LocalDate end = LocalDate.of(y2, m2, d2);
        Period test = Period.between(start, end);
        assertPeriod(test, ye, me, de, 0, 0, 0, 0);
        //assertEquals(start.plus(test), end);
    }

    @DataProvider(name="betweenTimes")
    Object[][] data_betweenTimes() {
        return new Object[][] {
            {12, 30, 40, 12, 30, 45,  0, 0, 5},
            {12, 30, 40, 12, 35, 40,  0, 5, 0},
            {12, 30, 40, 13, 30, 40,  1, 0, 0},
            
            {12, 30, 40, 12, 30, 35,  0, 0, -5},
            {12, 30, 40, 12, 25, 40,  0, -5, 0},
            {12, 30, 40, 11, 30, 40,  -1, 0, 0},
        };
    }

    @Test(dataProvider="betweenTimes")
    public void factory_between_LocalTime(int h1, int m1, int s1, int h2, int m2, int s2, int he, int me, int se) {
        LocalTime start = LocalTime.of(h1, m1, s1);
        LocalTime end = LocalTime.of(h2, m2, s2);
        Period test = Period.between(start, end);
        assertPeriod(test, 0, 0, 0, he, me, se, 0);
        //assertEquals(start.plus(test), end);
    }

    public void factory_between_YearMonth() {
        assertPeriod(Period.between(YearMonth.of(2012, 6), YearMonth.of(2013, 7)), 1, 1, 0, 0, 0, 0, 0);
        assertPeriod(Period.between(YearMonth.of(2012, 6), YearMonth.of(2013, 3)), 0, 9, 0, 0, 0, 0, 0);
        assertPeriod(Period.between(YearMonth.of(2012, 6), YearMonth.of(2011, 7)), 0, -11, 0, 0, 0, 0, 0);
    }

    public void factory_between_Month() {
        assertPeriod(Period.between(Month.FEBRUARY, Month.MAY), 0, 3, 0, 0, 0, 0, 0);
        assertPeriod(Period.between(Month.NOVEMBER, Month.MAY), 0, -6, 0, 0, 0, 0, 0);
    }

    //-----------------------------------------------------------------------
    // betweenISO
    //-----------------------------------------------------------------------
    @DataProvider(name="betweenISO")
    Object[][] data_betweenISO() {
        return new Object[][] {
            {2010, 1, 1, 2010, 1, 1, 0, 0, 0},
            {2010, 1, 1, 2010, 1, 2, 0, 0, 1},
            {2010, 1, 1, 2010, 1, 31, 0, 0, 30},
            {2010, 1, 1, 2010, 2, 1, 0, 1, 0},
            {2010, 1, 1, 2010, 2, 28, 0, 1, 27},
            {2010, 1, 1, 2010, 3, 1, 0, 2, 0},
            {2010, 1, 1, 2010, 12, 31, 0, 11, 30},
            {2010, 1, 1, 2011, 1, 1, 1, 0, 0},
            {2010, 1, 1, 2011, 12, 31, 1, 11, 30},
            {2010, 1, 1, 2012, 1, 1, 2, 0, 0},
            
            {2010, 1, 10, 2010, 1, 1, 0, 0, -9},
            {2010, 1, 10, 2010, 1, 2, 0, 0, -8},
            {2010, 1, 10, 2010, 1, 9, 0, 0, -1},
            {2010, 1, 10, 2010, 1, 10, 0, 0, 0},
            {2010, 1, 10, 2010, 1, 11, 0, 0, 1},
            {2010, 1, 10, 2010, 1, 31, 0, 0, 21},
            {2010, 1, 10, 2010, 2, 1, 0, 0, 22},
            {2010, 1, 10, 2010, 2, 9, 0, 0, 30},
            {2010, 1, 10, 2010, 2, 10, 0, 1, 0},
            {2010, 1, 10, 2010, 2, 28, 0, 1, 18},
            {2010, 1, 10, 2010, 3, 1, 0, 1, 19},
            {2010, 1, 10, 2010, 3, 9, 0, 1, 27},
            {2010, 1, 10, 2010, 3, 10, 0, 2, 0},
            {2010, 1, 10, 2010, 12, 31, 0, 11, 21},
            {2010, 1, 10, 2011, 1, 1, 0, 11, 22},
            {2010, 1, 10, 2011, 1, 9, 0, 11, 30},
            {2010, 1, 10, 2011, 1, 10, 1, 0, 0},
            
            {2010, 3, 30, 2011, 5, 1, 1, 1, 1},
            {2010, 4, 30, 2011, 5, 1, 1, 0, 1},
            
            {2010, 2, 28, 2012, 2, 27, 1, 11, 30},
            {2010, 2, 28, 2012, 2, 28, 2, 0, 0},
            {2010, 2, 28, 2012, 2, 29, 2, 0, 1},
            
            {2012, 2, 28, 2014, 2, 27, 1, 11, 30},
            {2012, 2, 28, 2014, 2, 28, 2, 0, 0},
            {2012, 2, 28, 2014, 3, 1, 2, 0, 1},
            
            {2012, 2, 29, 2014, 2, 28, 1, 11, 30},
            {2012, 2, 29, 2014, 3, 1, 2, 0, 1},
            {2012, 2, 29, 2014, 3, 2, 2, 0, 2},
            
            {2012, 2, 29, 2016, 2, 28, 3, 11, 30},
            {2012, 2, 29, 2016, 2, 29, 4, 0, 0},
            {2012, 2, 29, 2016, 3, 1, 4, 0, 1},
            
            {2010, 1, 1, 2009, 12, 31, 0, 0, -1},
            {2010, 1, 1, 2009, 12, 30, 0, 0, -2},
            {2010, 1, 1, 2009, 12, 2, 0, 0, -30},
            {2010, 1, 1, 2009, 12, 1, 0, -1, 0},
            {2010, 1, 1, 2009, 11, 30, 0, -1, -1},
            {2010, 1, 1, 2009, 11, 2, 0, -1, -29},
            {2010, 1, 1, 2009, 11, 1, 0, -2, 0},
            {2010, 1, 1, 2009, 1, 2, 0, -11, -30},
            {2010, 1, 1, 2009, 1, 1, -1, 0, 0},
            
            {2010, 1, 15, 2010, 1, 15, 0, 0, 0},
            {2010, 1, 15, 2010, 1, 14, 0, 0, -1},
            {2010, 1, 15, 2010, 1, 1, 0, 0, -14},
            {2010, 1, 15, 2009, 12, 31, 0, 0, -15},
            {2010, 1, 15, 2009, 12, 16, 0, 0, -30},
            {2010, 1, 15, 2009, 12, 15, 0, -1, 0},
            {2010, 1, 15, 2009, 12, 14, 0, -1, -1},
            
            {2010, 2, 28, 2009, 3, 1, 0, -11, -27},
            {2010, 2, 28, 2009, 2, 28, -1, 0, 0},
            {2010, 2, 28, 2009, 2, 27, -1, 0, -1},
            
            {2010, 2, 28, 2008, 2, 29, -1, -11, -28},
            {2010, 2, 28, 2008, 2, 28, -2, 0, 0},
            {2010, 2, 28, 2008, 2, 27, -2, 0, -1},
            
            {2012, 2, 29, 2009, 3, 1, -2, -11, -28},
            {2012, 2, 29, 2009, 2, 28, -3, 0, -1},
            {2012, 2, 29, 2009, 2, 27, -3, 0, -2},
            
            {2012, 2, 29, 2008, 3, 1, -3, -11, -28},
            {2012, 2, 29, 2008, 2, 29, -4, 0, 0},
            {2012, 2, 29, 2008, 2, 28, -4, 0, -1},
        };
    }

    @Test(dataProvider="betweenISO")
    public void factory_betweenISO_LocalDate(int y1, int m1, int d1, int y2, int m2, int d2, int ye, int me, int de) {
        LocalDate start = LocalDate.of(y1, m1, d1);
        LocalDate end = LocalDate.of(y2, m2, d2);
        Period test = Period.betweenISO(start, end);
        assertPeriod(test, ye, me, de, 0, 0, 0, 0);
        //assertEquals(start.plus(test), end);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_betweenISO_LocalDate_nullFirst() {
        Period.betweenISO((LocalDate) null, LocalDate.of(2010, 1, 1));
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_betweenISO_LocalDate_nullSecond() {
        Period.betweenISO(LocalDate.of(2010, 1, 1), (LocalDate) null);
    }

    //-------------------------------------------------------------------------
    @Test(expectedExceptions=NullPointerException.class)
    public void factory_betweenISO_LocalTime_nullFirst() {
        Period.betweenISO((LocalTime) null, LocalTime.of(12, 30));
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_betweenISO_LocalTime_nullSecond() {
        Period.betweenISO(LocalTime.of(12, 30), (LocalTime) null);
    }

    //-----------------------------------------------------------------------
    // parse()
    //-----------------------------------------------------------------------
//    @Test(dataProvider="toStringAndParse")
//    public void test_parse(Period test, String expected) {
//        if (Math.signum(test.getSeconds()) == Math.signum(test.getNanos())) {
//            assertEquals(test, Period.parse(expected));
//        }
//    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_parse_nullText() {
        Period.parse((String) null);
    }

    //-----------------------------------------------------------------------
    // isZero()
    //-----------------------------------------------------------------------
    public void test_isZero() {
        assertEquals(Period.of(1, 2, 3, 4, 5, 6, 7).isZero(), false);
        assertEquals(Period.of(1, 2, 3, 0, 0, 0, 0).isZero(), false);
        assertEquals(Period.of(0, 0, 0, 4, 5, 6, 7).isZero(), false);
        assertEquals(Period.of(1, 0, 0, 0, 0, 0, 0).isZero(), false);
        assertEquals(Period.of(0, 2, 0, 0, 0, 0, 0).isZero(), false);
        assertEquals(Period.of(0, 0, 3, 0, 0, 0, 0).isZero(), false);
        assertEquals(Period.of(0, 0, 0, 4, 0, 0, 0).isZero(), false);
        assertEquals(Period.of(0, 0, 0, 0, 5, 0, 0).isZero(), false);
        assertEquals(Period.of(0, 0, 0, 0, 0, 6, 0).isZero(), false);
        assertEquals(Period.of(0, 0, 0, 0, 0, 0, 7).isZero(), false);
        assertEquals(Period.of(0, 0, 0, 0, 0, 0).isZero(), true);
    }

    //-----------------------------------------------------------------------
    // isPositive()
    //-----------------------------------------------------------------------
    public void test_isPositive() {
        assertEquals(Period.of(1, 2, 3, 4, 5, 6, 7).isPositive(), true);
        assertEquals(Period.of(1, 2, 3, 0, 0, 0, 0).isPositive(), true);
        assertEquals(Period.of(0, 0, 0, 4, 5, 6, 7).isPositive(), true);
        assertEquals(Period.of(1, 0, 0, 0, 0, 0, 0).isPositive(), true);
        assertEquals(Period.of(0, 2, 0, 0, 0, 0, 0).isPositive(), true);
        assertEquals(Period.of(0, 0, 3, 0, 0, 0, 0).isPositive(), true);
        assertEquals(Period.of(0, 0, 0, 4, 0, 0, 0).isPositive(), true);
        assertEquals(Period.of(0, 0, 0, 0, 5, 0, 0).isPositive(), true);
        assertEquals(Period.of(0, 0, 0, 0, 0, 6, 0).isPositive(), true);
        assertEquals(Period.of(0, 0, 0, 0, 0, 0, 7).isPositive(), true);
        assertEquals(Period.of(-1, -2, -3, -4, -5, -6, -7).isPositive(), false);
        assertEquals(Period.of(-1, -2, 3, 4, -5, -6, -7).isPositive(), false);
        assertEquals(Period.of(-1, 0, 0, 0, 0, 0, 0).isPositive(), false);
        assertEquals(Period.of(0, -2, 0, 0, 0, 0, 0).isPositive(), false);
        assertEquals(Period.of(0, 0, -3, 0, 0, 0, 0).isPositive(), false);
        assertEquals(Period.of(0, 0, 0, -4, 0, 0, 0).isPositive(), false);
        assertEquals(Period.of(0, 0, 0, 0, -5, 0, 0).isPositive(), false);
        assertEquals(Period.of(0, 0, 0, 0, 0, -6, 0).isPositive(), false);
        assertEquals(Period.of(0, 0, 0, 0, 0, 0, -7).isPositive(), false);
        assertEquals(Period.of(0, 0, 0, 0, 0, 0).isPositive(), false);
    }

//    //-----------------------------------------------------------------------
//    // getNanosInt()
//    //-----------------------------------------------------------------------
//    public void test_getNanosInt() {
//        Period test = Period.of(Integer.MAX_VALUE, NANOS);
//        assertEquals(test.getNanosInt(), Integer.MAX_VALUE);
//    }
//
//    @Test(expectedExceptions=ArithmeticException.class)
//    public void test_getNanosInt_tooBig() {
//        Period test = Period.of(Integer.MAX_VALUE + 1L, NANOS);
//        test.getNanosInt();
//    }

    //-----------------------------------------------------------------------
    // withYears()
    //-----------------------------------------------------------------------
    public void test_withYears() {
        Period test = Period.of(1, 2, 3, 4, 5, 6, 7);
        assertPeriod(test.withYears(10), 10, 2, 3, 4, 5, 6, 7);
    }

    public void test_withYears_noChange() {
        Period test = Period.of(1, 2, 3, 4, 5, 6, 7);
        assertSame(test.withYears(1), test);
    }

    public void test_withYears_toZero() {
        Period test = Period.of(1, YEARS);
        assertSame(test.withYears(0), Period.ZERO);
    }

    //-----------------------------------------------------------------------
    // withMonths()
    //-----------------------------------------------------------------------
    public void test_withMonths() {
        Period test = Period.of(1, 2, 3, 4, 5, 6, 7);
        assertPeriod(test.withMonths(10), 1, 10, 3, 4, 5, 6, 7);
    }

    public void test_withMonths_noChange() {
        Period test = Period.of(1, 2, 3, 4, 5, 6, 7);
        assertSame(test.withMonths(2), test);
    }

    public void test_withMonths_toZero() {
        Period test = Period.of(1, MONTHS);
        assertSame(test.withMonths(0), Period.ZERO);
    }

    //-----------------------------------------------------------------------
    // withDays()
    //-----------------------------------------------------------------------
    public void test_withDays() {
        Period test = Period.of(1, 2, 3, 4, 5, 6, 7);
        assertPeriod(test.withDays(10), 1, 2, 10, 4, 5, 6, 7);
    }

    public void test_withDays_noChange() {
        Period test = Period.of(1, 2, 3, 4, 5, 6, 7);
        assertSame(test.withDays(3), test);
    }

    public void test_withDays_toZero() {
        Period test = Period.of(1, DAYS);
        assertSame(test.withDays(0), Period.ZERO);
    }

//    //-----------------------------------------------------------------------
//    // withHours()
//    //-----------------------------------------------------------------------
//    public void test_withHours() {
//        Period test = Period.of(1, 2, 3, 4, 5, 6, 7);
//        assertPeriod(test.withHours(10), 1, 2, 3, 10, 5, 6, 7);
//    }
//
//    public void test_withHours_noChange() {
//        Period test = Period.of(1, 2, 3, 4, 5, 6, 7);
//        assertSame(test.withHours(4), test);
//    }
//
//    public void test_withHours_toZero() {
//        Period test = Period.of(1, HOURS);
//        assertSame(test.withHours(0), Period.ZERO);
//    }
//
//    //-----------------------------------------------------------------------
//    // withMinutes()
//    //-----------------------------------------------------------------------
//    public void test_withMinutes() {
//        Period test = Period.of(1, 2, 3, 4, 5, 6, 7);
//        assertPeriod(test.withMinutes(10), 1, 2, 3, 4, 10, 6, 7);
//    }
//
//    public void test_withMinutes_noChange() {
//        Period test = Period.of(1, 2, 3, 4, 5, 6, 7);
//        assertSame(test.withMinutes(5), test);
//    }
//
//    public void test_withMinutes_toZero() {
//        Period test = Period.of(1, MINUTES);
//        assertSame(test.withMinutes(0), Period.ZERO);
//    }
//
//    //-----------------------------------------------------------------------
//    // withSeconds()
//    //-----------------------------------------------------------------------
//    public void test_withSeconds() {
//        Period test = Period.of(1, 2, 3, 4, 5, 6, 7);
//        assertPeriod(test.withSeconds(10), 1, 2, 3, 4, 5, 10, 7);
//    }
//
//    public void test_withSeconds_noChange() {
//        Period test = Period.of(1, 2, 3, 4, 5, 6, 7);
//        assertSame(test.withSeconds(6), test);
//    }
//
//    public void test_withSeconds_toZero() {
//        Period test = Period.of(1, SECONDS);
//        assertSame(test.withSeconds(0), Period.ZERO);
//    }

    //-----------------------------------------------------------------------
    // withTimeNanos()
    //-----------------------------------------------------------------------
    public void test_withNanos() {
        Period test = Period.of(1, 2, 3, 4, 5, 6, 7);
        assertPeriod(test.withTimeNanos(10), 1, 2, 3, 0, 0, 0, 10);
    }

    public void test_withNanos_noChange() {
        Period test = Period.of(1, 2, 3, 4, 5, 6, 7);
        assertSame(test.withTimeNanos(((4 * 60 + 5) * 60 + 6) * 1_000_000_000L + 7), test);
    }

    public void test_withNanos_toZero() {
        Period test = Period.of(1, NANOS);
        assertSame(test.withTimeNanos(0), Period.ZERO);
    }



    //-----------------------------------------------------------------------
    // plusYears()
    //-----------------------------------------------------------------------
    public void test_plusYears() {
        Period test = Period.of(1, 2, 3, 4, 5, 6, 7);
        assertPeriod(test.plus(10, YEARS), 11, 2, 3, 4, 5, 6, 7);
        assertPeriod(test.plus(Period.of(10, YEARS)), 11, 2, 3, 4, 5, 6, 7);
    }

    public void test_plusYears_noChange() {
        Period test = Period.of(1, 2, 3, 4, 5, 6, 7);
        assertSame(test.plus(0, YEARS), test);
        assertPeriod(test.plus(Period.of(0, YEARS)), 1, 2, 3, 4, 5, 6, 7);
    }

    public void test_plusYears_toZero() {
        Period test = Period.of(-1, YEARS);
        assertSame(test.plus(1, YEARS), Period.ZERO);
        assertSame(test.plus(Period.of(1, YEARS)), Period.ZERO);
    }

    @Test(expectedExceptions=ArithmeticException.class)
    public void test_plusYears_overflowTooBig() {
        Period test = Period.of(Integer.MAX_VALUE, YEARS);
        test.plus(1, YEARS);
    }

    @Test(expectedExceptions=ArithmeticException.class)
    public void test_plusYears_overflowTooSmall() {
        Period test = Period.of(Integer.MIN_VALUE, YEARS);
        test.plus(-1, YEARS);
    }

    //-----------------------------------------------------------------------
    // plusMonths()
    //-----------------------------------------------------------------------
    public void test_plusMonths() {
        Period test = Period.of(1, 2, 3, 4, 5, 6, 7);
        assertPeriod(test.plus(10, MONTHS), 1, 12, 3, 4, 5, 6, 7);
        assertPeriod(test.plus(Period.of(10, MONTHS)), 1, 12, 3, 4, 5, 6, 7);
    }

    public void test_plusMonths_noChange() {
        Period test = Period.of(1, 2, 3, 4, 5, 6, 7);
        assertSame(test.plus(0, MONTHS), test);
        assertEquals(test.plus(Period.of(0, MONTHS)), test);
    }

    public void test_plusMonths_toZero() {
        Period test = Period.of(-1, MONTHS);
        assertSame(test.plus(1, MONTHS), Period.ZERO);
        assertSame(test.plus(Period.of(1, MONTHS)), Period.ZERO);
    }

    @Test(expectedExceptions=ArithmeticException.class)
    public void test_plusMonths_overflowTooBig() {
        Period test = Period.of(Integer.MAX_VALUE, MONTHS);
        test.plus(1, MONTHS);
    }

    @Test(expectedExceptions=ArithmeticException.class)
    public void test_plusMonths_overflowTooSmall() {
        Period test = Period.of(Integer.MIN_VALUE, MONTHS);
        test.plus(-1, MONTHS);
    }

    //-----------------------------------------------------------------------
    // plusDays()
    //-----------------------------------------------------------------------
    public void test_plusDays() {
        Period test = Period.of(1, 2, 3, 4, 5, 6, 7);
        assertPeriod(test.plus(10, DAYS), 1, 2, 13, 4, 5, 6, 7);
    }

    public void test_plusDays_noChange() {
        Period test = Period.of(1, 2, 3, 4, 5, 6, 7);
        assertSame(test.plus(0, DAYS), test);
    }

    public void test_plusDays_toZero() {
        Period test = Period.of(-1, DAYS);
        assertSame(test.plus(1, DAYS), Period.ZERO);
    }

    @Test(expectedExceptions=ArithmeticException.class)
    public void test_plusDays_overflowTooBig() {
        Period test = Period.of(Integer.MAX_VALUE, DAYS);
        test.plus(1, DAYS);
    }

    @Test(expectedExceptions=ArithmeticException.class)
    public void test_plusDays_overflowTooSmall() {
        Period test = Period.of(Integer.MIN_VALUE, DAYS);
        test.plus(-1, DAYS);
    }

    //-----------------------------------------------------------------------
    // plusHours()
    //-----------------------------------------------------------------------
    public void test_plusHours() {
        Period test = Period.of(1, 2, 3, 4, 5, 6, 7);
        assertPeriod(test.plus(10, HOURS), 1, 2, 3, 14, 5, 6, 7);
    }

    public void test_plusHours_noChange() {
        Period test = Period.of(1, 2, 3, 4, 5, 6, 7);
        assertSame(test.plus(0, HOURS), test);
    }

    public void test_plusHours_toZero() {
        Period test = Period.of(-1, HOURS);
        assertSame(test.plus(1, HOURS), Period.ZERO);
    }

    @Test(expectedExceptions=ArithmeticException.class)
    public void test_plusHours_overflowTooBig() {
        Period test = Period.of(Integer.MAX_VALUE, HOURS);
        test.plus(1, HOURS);
    }

    @Test(expectedExceptions=ArithmeticException.class)
    public void test_plusHours_overflowTooSmall() {
        Period test = Period.of(Integer.MIN_VALUE, HOURS);
        test.plus(-1, HOURS);
    }

    //-----------------------------------------------------------------------
    // plusMinutes()
    //-----------------------------------------------------------------------
    public void test_plusMinutes() {
        Period test = Period.of(1, 2, 3, 4, 5, 6, 7);
        assertPeriod(test.plus(10, MINUTES), 1, 2, 3, 4, 15, 6, 7);
    }

    public void test_plusMinutes_noChange() {
        Period test = Period.of(1, 2, 3, 4, 5, 6, 7);
        assertSame(test.plus(0, MINUTES), test);
    }

    public void test_plusMinutes_toZero() {
        Period test = Period.of(-1, MINUTES);
        assertSame(test.plus(1, MINUTES), Period.ZERO);
    }

    //-----------------------------------------------------------------------
    // plusSeconds()
    //-----------------------------------------------------------------------
    public void test_plusSeconds() {
        Period test = Period.of(1, 2, 3, 4, 5, 6, 7);
        assertPeriod(test.plus(10, SECONDS), 1, 2, 3, 4, 5, 16, 7);
    }

    public void test_plusSeconds_noChange() {
        Period test = Period.of(1, 2, 3, 4, 5, 6, 7);
        assertSame(test.plus(0, SECONDS), test);
    }

    public void test_plusSeconds_toZero() {
        Period test = Period.of(-1, SECONDS);
        assertSame(test.plus(1, SECONDS), Period.ZERO);
    }

    //-----------------------------------------------------------------------
    // plusNanos()
    //-----------------------------------------------------------------------
    public void test_plusNanos() {
        Period test = Period.of(1, 2, 3, 4, 5, 6, 7);
        assertPeriod(test.plus(10, NANOS), 1, 2, 3, 4, 5, 6, 17);
    }

    public void test_plusNanos_noChange() {
        Period test = Period.of(1, 2, 3, 4, 5, 6, 7);
        assertSame(test.plus(0, NANOS), test);
    }

    public void test_plusNanos_toZero() {
        Period test = Period.of(-1, NANOS);
        assertSame(test.plus(1, NANOS), Period.ZERO);
    }

    @Test(expectedExceptions=ArithmeticException.class)
    public void test_plusNanos_overflowTooBig() {
        Period test = Period.of(Long.MAX_VALUE, NANOS);
        test.plus(1, NANOS);
    }

    @Test(expectedExceptions=ArithmeticException.class)
    public void test_plusNanos_overflowTooSmall() {
        Period test = Period.of(Long.MIN_VALUE, NANOS);
        test.plus(-1, NANOS);
    }

    //-----------------------------------------------------------------------
    // minusYears()
    //-----------------------------------------------------------------------
    public void test_minusYears() {
        Period test = Period.of(1, 2, 3, 4, 5, 6, 7);
        assertPeriod(test.minus(10, YEARS), -9, 2, 3, 4, 5, 6, 7);
    }

    public void test_minusYears_noChange() {
        Period test = Period.of(1, 2, 3, 4, 5, 6, 7);
        assertSame(test.minus(0, YEARS), test);
    }

    public void test_minusYears_toZero() {
        Period test = Period.of(1, YEARS);
        assertSame(test.minus(1, YEARS), Period.ZERO);
    }

    @Test(expectedExceptions=ArithmeticException.class)
    public void test_minusYears_overflowTooBig() {
        Period test = Period.of(Integer.MAX_VALUE, YEARS);
        test.minus(-1, YEARS);
    }

    @Test(expectedExceptions=ArithmeticException.class)
    public void test_minusYears_overflowTooSmall() {
        Period test = Period.of(Integer.MIN_VALUE, YEARS);
        test.minus(1, YEARS);
    }

    //-----------------------------------------------------------------------
    // minusMonths()
    //-----------------------------------------------------------------------
    public void test_minusMonths() {
        Period test = Period.of(1, 2, 3, 4, 5, 6, 7);
        assertPeriod(test.minus(10, MONTHS), 1, -8, 3, 4, 5, 6, 7);
    }

    public void test_minusMonths_noChange() {
        Period test = Period.of(1, 2, 3, 4, 5, 6, 7);
        assertSame(test.minus(0, MONTHS), test);
    }

    public void test_minusMonths_toZero() {
        Period test = Period.of(1, MONTHS);
        assertSame(test.minus(1, MONTHS), Period.ZERO);
    }

    @Test(expectedExceptions=ArithmeticException.class)
    public void test_minusMonths_overflowTooBig() {
        Period test = Period.of(Integer.MAX_VALUE, MONTHS);
        test.minus(-1, MONTHS);
    }

    @Test(expectedExceptions=ArithmeticException.class)
    public void test_minusMonths_overflowTooSmall() {
        Period test = Period.of(Integer.MIN_VALUE, MONTHS);
        test.minus(1, MONTHS);
    }

    //-----------------------------------------------------------------------
    // minusDays()
    //-----------------------------------------------------------------------
    public void test_minusDays() {
        Period test = Period.of(1, 2, 3, 4, 5, 6, 7);
        assertPeriod(test.minus(10, DAYS), 1, 2, -7, 4, 5, 6, 7);
    }

    public void test_minusDays_noChange() {
        Period test = Period.of(1, 2, 3, 4, 5, 6, 7);
        assertSame(test.minus(0, DAYS), test);
    }

    public void test_minusDays_toZero() {
        Period test = Period.of(1, DAYS);
        assertSame(test.minus(1, DAYS), Period.ZERO);
    }

    @Test(expectedExceptions=ArithmeticException.class)
    public void test_minusDays_overflowTooBig() {
        Period test = Period.of(Integer.MAX_VALUE, DAYS);
        test.minus(-1, DAYS);
    }

    @Test(expectedExceptions=ArithmeticException.class)
    public void test_minusDays_overflowTooSmall() {
        Period test = Period.of(Integer.MIN_VALUE, DAYS);
        test.minus(1, DAYS);
    }

    //-----------------------------------------------------------------------
    // minusHours()
    //-----------------------------------------------------------------------
    public void test_minusHours() {
        Period test = Period.of(1, 2, 3, 4, 5, 6, 7);
        assertPeriod(test.minus(10, HOURS), 1, 2, 3, -5, -54, -53, -999999993);
        assertEquals(test.minus(10, HOURS).plus(10, HOURS), test);
    }

    public void test_minusHours_noChange() {
        Period test = Period.of(1, 2, 3, 4, 5, 6, 7);
        assertSame(test.minus(0, HOURS), test);
    }

    public void test_minusHours_toZero() {
        Period test = Period.of(1, HOURS);
        assertSame(test.minus(1, HOURS), Period.ZERO);
    }

    @Test(expectedExceptions=ArithmeticException.class)
    public void test_minusHours_overflowTooBig() {
        Period test = Period.of(Integer.MAX_VALUE, HOURS);
        test.minus(-1, HOURS);
    }

    @Test(expectedExceptions=ArithmeticException.class)
    public void test_minusHours_overflowTooSmall() {
        Period test = Period.of(Integer.MIN_VALUE, HOURS);
        test.minus(1, HOURS);
    }

    //-----------------------------------------------------------------------
    // minusMinutes()
    //-----------------------------------------------------------------------
    public void test_minusMinutes() {
        Period test = Period.of(1, 2, 3, 4, 5, 6, 7);
        assertPeriod(test.minus(10, MINUTES), 1, 2, 3, 3, 55, 6, 7);
        assertEquals(test.minus(10, MINUTES).plus(10, MINUTES), test);
    }

    public void test_minusMinutes_noChange() {
        Period test = Period.of(1, 2, 3, 4, 5, 6, 7);
        assertSame(test.minus(0, MINUTES), test);
    }

    public void test_minusMinutes_toZero() {
        Period test = Period.of(1, MINUTES);
        assertSame(test.minus(1, MINUTES), Period.ZERO);
    }

    //-----------------------------------------------------------------------
    // minusSeconds()
    //-----------------------------------------------------------------------
    public void test_minusSeconds() {
        Period test = Period.of(1, 2, 3, 4, 5, 6, 7);
        assertPeriod(test.minus(10, SECONDS), 1, 2, 3, 4, 4, 56, 7);
        assertEquals(test.minus(10, SECONDS).plus(10, SECONDS), test);
    }

    public void test_minusSeconds_noChange() {
        Period test = Period.of(1, 2, 3, 4, 5, 6, 7);
        assertSame(test.minus(0, SECONDS), test);
    }

    public void test_minusSeconds_toZero() {
        Period test = Period.of(1, SECONDS);
        assertSame(test.minus(1, SECONDS), Period.ZERO);
    }

    //-----------------------------------------------------------------------
    // minusNanos()
    //-----------------------------------------------------------------------
    public void test_minusNanos() {
        Period test = Period.of(1, 2, 3, 4, 5, 6, 7);
        assertPeriod(test.minus(10, NANOS), 1, 2, 3, 4, 5, 5, 999999997);
    }

    public void test_minusNanos_noChange() {
        Period test = Period.of(1, 2, 3, 4, 5, 6, 7);
        assertSame(test.minus(0, NANOS), test);
    }

    public void test_minusNanos_toZero() {
        Period test = Period.of(1, NANOS);
        assertSame(test.minus(1, NANOS), Period.ZERO);
    }

    @Test(expectedExceptions=ArithmeticException.class)
    public void test_minusNanos_overflowTooBig() {
        Period test = Period.of(Long.MAX_VALUE, NANOS);
        test.minus(-1, NANOS);
    }

    @Test(expectedExceptions=ArithmeticException.class)
    public void test_minusNanos_overflowTooSmall() {
        Period test = Period.of(Long.MIN_VALUE, NANOS);
        test.minus(1, NANOS);
    }

    //-----------------------------------------------------------------------
    // multipliedBy()
    //-----------------------------------------------------------------------
    public void test_multipliedBy() {
        Period test = Period.of(1, 2, 3, 4, 5, 6, 7);
        assertPeriod(test.multipliedBy(2), 2, 4, 6, 8, 10, 12, 14);
        assertPeriod(test.multipliedBy(-3), -3, -6, -9, -12, -15, -18, -21);
    }

    public void test_multipliedBy_zeroBase() {
        assertSame(Period.ZERO.multipliedBy(2), Period.ZERO);
    }

    public void test_multipliedBy_zero() {
        Period test = Period.of(1, 2, 3, 4, 5, 6, 7);
        assertSame(test.multipliedBy(0), Period.ZERO);
    }

    public void test_multipliedBy_one() {
        Period test = Period.of(1, 2, 3, 4, 5, 6, 7);
        assertSame(test.multipliedBy(1), test);
    }

    @Test(expectedExceptions=ArithmeticException.class)
    public void test_multipliedBy_overflowTooBig() {
        Period test = Period.of(Integer.MAX_VALUE / 2 + 1, YEARS);
        test.multipliedBy(2);
    }

    @Test(expectedExceptions=ArithmeticException.class)
    public void test_multipliedBy_overflowTooSmall() {
        Period test = Period.of(Integer.MIN_VALUE / 2 - 1, YEARS);
        test.multipliedBy(2);
    }

    //-----------------------------------------------------------------------
    // negated()
    //-----------------------------------------------------------------------
    public void test_negated() {
        Period test = Period.of(1, 2, 3, 4, 5, 6, 7);
        assertPeriod(test.negated(), -1, -2, -3, -4, -5, -6, -7);
    }

    public void test_negated_zero() {
        assertSame(Period.ZERO.negated(), Period.ZERO);
    }

    public void test_negated_max() {
        assertPeriod(Period.of(Integer.MAX_VALUE, YEARS).negated(), -Integer.MAX_VALUE, 0, 0, 0, 0, 0, 0);
    }

    @Test(expectedExceptions=ArithmeticException.class)
    public void test_negated_overflow() {
        Period.of(Integer.MIN_VALUE, YEARS).negated();
    }

    //-----------------------------------------------------------------------
    // normalized()
    //-----------------------------------------------------------------------
//    public void test_normalized() {
//        assertPeriod(Period.of(1, 2, 3, 4, 5, 6, 7).normalized(), 1, 2, 3, 4, 5, 6, 7);
//    }
//
//    public void test_normalized_months() {
//        assertPeriod(Period.of(1, 11, 3, 4, 5, 6).normalized(), 1, 11, 3, 4, 5, 6, 0);
//        assertPeriod(Period.of(1, 12, 3, 4, 5, 6).normalized(), 2, 0, 3, 4, 5, 6, 0);
//        assertPeriod(Period.of(1, 23, 3, 4, 5, 6).normalized(), 2, 11, 3, 4, 5, 6, 0);
//        assertPeriod(Period.of(1, 24, 3, 4, 5, 6).normalized(), 3, 0, 3, 4, 5, 6, 0);
//        
//        assertPeriod(Period.of(3, -2, 3, 4, 5, 6).normalized(), 2, 10, 3, 4, 5, 6, 0);
//    }
//
//    public void test_normalized_nanos() {
//        assertPeriod(Period.of(1, 2, 3, 4, 5, 6, 999999999).normalized(), 1, 2, 3, 4, 5, 6, 999999999);
//        assertPeriod(Period.of(1, 2, 3, 4, 5, 6, 1000000000).normalized(), 1, 2, 3, 4, 5, 7, 0);
//        
//        assertPeriod(Period.of(1, 2, 3, 4, 5, 59, 999999999).normalized(), 1, 2, 3, 4, 5, 59, 999999999);
//        assertPeriod(Period.of(1, 2, 3, 4, 5, 59, 1000000000).normalized(), 1, 2, 3, 4, 6, 0, 0);
//        
//        assertPeriod(Period.of(1, 2, 3, 4, 59, 59, 999999999).normalized(), 1, 2, 3, 4, 59, 59, 999999999);
//        assertPeriod(Period.of(1, 2, 3, 4, 59, 59, 1000000000).normalized(), 1, 2, 3, 5, 0, 0, 0);
//        
//        assertPeriod(Period.of(1, 2, 3, 23, 59, 59, 999999999).normalized(), 1, 2, 3, 23, 59, 59, 999999999);
//        assertPeriod(Period.of(1, 2, 3, 23, 59, 59, 1000000000).normalized(), 1, 2, 3, 24, 0, 0, 0);
//        
//        assertPeriod(Period.of(1, 2, 3, 0, 1, 0, -1).normalized(), 1, 2, 3, 0, 0, 59, 999999999);
//    }
//
//    public void test_normalized_seconds() {
//        assertPeriod(Period.of(1, 2, 3, 4, 5, 59).normalized(), 1, 2, 3, 4, 5, 59, 0);
//        assertPeriod(Period.of(1, 2, 3, 4, 5, 60).normalized(), 1, 2, 3, 4, 6, 0, 0);
//        assertPeriod(Period.of(1, 2, 3, 4, 5, 119).normalized(), 1, 2, 3, 4, 6, 59, 0);
//        assertPeriod(Period.of(1, 2, 3, 4, 5, 120).normalized(), 1, 2, 3, 4, 7, 0, 0);
//        
//        assertPeriod(Period.of(1, 2, 3, 4, 59, 59).normalized(), 1, 2, 3, 4, 59, 59, 0);
//        assertPeriod(Period.of(1, 2, 3, 4, 59, 60).normalized(), 1, 2, 3, 5, 0, 0, 0);
//        
//        assertPeriod(Period.of(1, 2, 3, 23, 59, 59).normalized(), 1, 2, 3, 23, 59, 59, 0);
//        assertPeriod(Period.of(1, 2, 3, 23, 59, 60).normalized(), 1, 2, 3, 24, 0, 0, 0);
//        
//        assertPeriod(Period.of(1, 2, 3, 0, 0, -1).normalized(), 1, 2, 3, -1, 59, 59, 0);
//    }
//
//    public void test_normalized_minutes() {
//        assertPeriod(Period.of(1, 2, 3, 4, 59, 6).normalized(), 1, 2, 3, 4, 59, 6, 0);
//        assertPeriod(Period.of(1, 2, 3, 4, 60, 6).normalized(), 1, 2, 3, 5, 0, 6, 0);
//        assertPeriod(Period.of(1, 2, 3, 4, 119, 6).normalized(), 1, 2, 3, 5, 59, 6, 0);
//        assertPeriod(Period.of(1, 2, 3, 4, 120, 6).normalized(), 1, 2, 3, 6, 0, 6, 0);
//        
//        assertPeriod(Period.of(1, 2, 3, 23, 59, 6).normalized(), 1, 2, 3, 23, 59, 6, 0);
//        assertPeriod(Period.of(1, 2, 3, 23, 60, 6).normalized(), 1, 2, 3, 24, 0, 6, 0);
//        
//        assertPeriod(Period.of(1, 2, 3, 23, -1, 0).normalized(), 1, 2, 3, 22, 59, 0, 0);
//        assertPeriod(Period.of(1, 2, 3, 23, -1, 60).normalized(), 1, 2, 3, 23, 0, 0, 0);
//    }
//
//    public void test_normalized_hours() {
//        assertPeriod(Period.of(1, 2, 3, 23, 5, 6).normalized(), 1, 2, 3, 23, 5, 6, 0);
//        assertPeriod(Period.of(1, 2, 3, 24, 5, 6).normalized(), 1, 2, 3, 24, 5, 6, 0);
//    }
//
//    public void test_normalized_zero() {
//        assertSame(Period.ZERO.normalized(), Period.ZERO);
//    }
//
//    public void test_normalized_bigNanos() {
//        Period big = Period.of(1, 2, 3, 4, 5, 6, Long.MAX_VALUE);
//        long total = Long.MAX_VALUE;
//        long nanos = total % 1000000000L;
//        total = total / 1000000000L + 6;
//        int secs = (int) (total % 60);
//        total = total / 60 + 5;
//        int mins = (int) (total % 60);
//        total = total / 60 + 4;
//        if (total > Integer.MAX_VALUE) {
//            throw new AssertionError("Bad test");
//        }
//        int hours = (int) total;
//        assertPeriod(big.normalized(), 1, 2, 3, hours, mins, secs, nanos);
//    }
//
//    @Test(expectedExceptions=ArithmeticException.class)
//    public void test_normalized_max() {
//        Period base = Period.of(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE,
//                Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Long.MAX_VALUE);
//        base.normalized();
//    }
//
//    @Test(expectedExceptions=ArithmeticException.class)
//    public void test_normalized_maxTime() {
//        Period base = Period.of(0, 0, Integer.MAX_VALUE,
//                Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Long.MAX_VALUE);
//        base.normalized();
//    }
//
//    //-----------------------------------------------------------------------
//    // normalizedWith24HourDays()
//    //-----------------------------------------------------------------------
//    public void test_normalizedWith24HourDays() {
//        assertPeriod(Period.of(1, 2, 3, 4, 5, 6).normalizedWith24HourDays(), 1, 2, 3, 4, 5, 6, 0);
//        
//        assertPeriod(Period.of(3, -2, 3, 4, 5, 6).normalizedWith24HourDays(), 2, 10, 3, 4, 5, 6, 0);
//    }
//
//    public void test_normalizedWith24HourDays_months() {
//        assertPeriod(Period.of(1, 11, 3, 4, 5, 6).normalizedWith24HourDays(), 1, 11, 3, 4, 5, 6, 0);
//        assertPeriod(Period.of(1, 12, 3, 4, 5, 6).normalizedWith24HourDays(), 2, 0, 3, 4, 5, 6, 0);
//        assertPeriod(Period.of(1, 23, 3, 4, 5, 6).normalizedWith24HourDays(), 2, 11, 3, 4, 5, 6, 0);
//        assertPeriod(Period.of(1, 24, 3, 4, 5, 6).normalizedWith24HourDays(), 3, 0, 3, 4, 5, 6, 0);
//    }
//
//    public void test_normalizedWith24HourDays_nanos() {
//        assertPeriod(Period.of(1, 2, 3, 4, 5, 6, 999999999).normalizedWith24HourDays(), 1, 2, 3, 4, 5, 6, 999999999);
//        assertPeriod(Period.of(1, 2, 3, 4, 5, 6, 1000000000).normalizedWith24HourDays(), 1, 2, 3, 4, 5, 7, 0);
//        
//        assertPeriod(Period.of(1, 2, 3, 4, 5, 59, 999999999).normalizedWith24HourDays(), 1, 2, 3, 4, 5, 59, 999999999);
//        assertPeriod(Period.of(1, 2, 3, 4, 5, 59, 1000000000).normalizedWith24HourDays(), 1, 2, 3, 4, 6, 0, 0);
//        
//        assertPeriod(Period.of(1, 2, 3, 4, 59, 59, 999999999).normalizedWith24HourDays(), 1, 2, 3, 4, 59, 59, 999999999);
//        assertPeriod(Period.of(1, 2, 3, 4, 59, 59, 1000000000).normalizedWith24HourDays(), 1, 2, 3, 5, 0, 0, 0);
//        
//        assertPeriod(Period.of(1, 2, 3, 23, 59, 59, 999999999).normalizedWith24HourDays(), 1, 2, 3, 23, 59, 59, 999999999);
//        assertPeriod(Period.of(1, 2, 3, 23, 59, 59, 1000000000).normalizedWith24HourDays(), 1, 2, 4, 0, 0, 0, 0);
//    }
//
//    public void test_normalizedWith24HourDays_seconds() {
//        assertPeriod(Period.of(1, 2, 3, 4, 5, 59).normalizedWith24HourDays(), 1, 2, 3, 4, 5, 59, 0);
//        assertPeriod(Period.of(1, 2, 3, 4, 5, 60).normalizedWith24HourDays(), 1, 2, 3, 4, 6, 0, 0);
//        assertPeriod(Period.of(1, 2, 3, 4, 5, 119).normalizedWith24HourDays(), 1, 2, 3, 4, 6, 59, 0);
//        assertPeriod(Period.of(1, 2, 3, 4, 5, 120).normalizedWith24HourDays(), 1, 2, 3, 4, 7, 0, 0);
//        
//        assertPeriod(Period.of(1, 2, 3, 4, 59, 59).normalizedWith24HourDays(), 1, 2, 3, 4, 59, 59, 0);
//        assertPeriod(Period.of(1, 2, 3, 4, 59, 60).normalizedWith24HourDays(), 1, 2, 3, 5, 0, 0, 0);
//        
//        assertPeriod(Period.of(1, 2, 3, 23, 59, 59).normalizedWith24HourDays(), 1, 2, 3, 23, 59, 59, 0);
//        assertPeriod(Period.of(1, 2, 3, 23, 59, 60).normalizedWith24HourDays(), 1, 2, 4, 0, 0, 0, 0);
//        
//        assertPeriod(Period.of(1, 2, 3, 0, 0, -1).normalizedWith24HourDays(), 1, 2, 2, 23, 59, 59, 0);
//    }
//
//    public void test_normalizedWith24HourDays_minutes() {
//        assertPeriod(Period.of(1, 2, 3, 4, 59, 6).normalizedWith24HourDays(), 1, 2, 3, 4, 59, 6, 0);
//        assertPeriod(Period.of(1, 2, 3, 4, 60, 6).normalizedWith24HourDays(), 1, 2, 3, 5, 0, 6, 0);
//        assertPeriod(Period.of(1, 2, 3, 4, 119, 6).normalizedWith24HourDays(), 1, 2, 3, 5, 59, 6, 0);
//        assertPeriod(Period.of(1, 2, 3, 4, 120, 6).normalizedWith24HourDays(), 1, 2, 3, 6, 0, 6, 0);
//        
//        assertPeriod(Period.of(1, 2, 3, 23, 59, 6).normalizedWith24HourDays(), 1, 2, 3, 23, 59, 6, 0);
//        assertPeriod(Period.of(1, 2, 3, 23, 60, 6).normalizedWith24HourDays(), 1, 2, 4, 0, 0, 6, 0);
//        
//        assertPeriod(Period.of(1, 2, 3, 23, -1, 0).normalizedWith24HourDays(), 1, 2, 3, 22, 59, 0, 0);
//        assertPeriod(Period.of(1, 2, 3, 23, -1, 60).normalizedWith24HourDays(), 1, 2, 3, 23, 0, 0, 0);
//    }
//
//    public void test_normalizedWith24HourDays_hours() {
//        assertPeriod(Period.of(1, 2, 3, 23, 5, 6).normalizedWith24HourDays(), 1, 2, 3, 23, 5, 6, 0);
//        assertPeriod(Period.of(1, 2, 3, 24, 5, 6).normalizedWith24HourDays(), 1, 2, 4, 0, 5, 6, 0);
//        assertPeriod(Period.of(1, 2, 3, 47, 5, 6).normalizedWith24HourDays(), 1, 2, 4, 23, 5, 6, 0);
//        assertPeriod(Period.of(1, 2, 3, 48, 5, 6).normalizedWith24HourDays(), 1, 2, 5, 0, 5, 6, 0);
//    }
//
//    public void test_normalizedWith24HourDays_zero() {
//        assertSame(Period.ZERO.normalizedWith24HourDays(), Period.ZERO);
//    }
//
//    public void test_normalizedWith24HourDays_bigNanos() {
//        Period big = Period.of(1, 2, 3, 4, 5, 6, Long.MAX_VALUE);
//        long total = Long.MAX_VALUE;
//        long nanos = total % 1000000000L;
//        total = total / 1000000000L + 6;
//        int secs = (int) (total % 60);
//        total = total / 60 + 5;
//        int mins = (int) (total % 60);
//        total = total / 60 + 4;
//        int hours = (int) (total % 24);
//        total = total / 24 + 3;
//        if (total > Integer.MAX_VALUE) {
//            throw new AssertionError("Bad test");
//        }
//        int days = (int) total;
//        assertPeriod(big.normalizedWith24HourDays(), 1, 2, days, hours, mins, secs, nanos);
//    }
//
//    @Test(expectedExceptions=ArithmeticException.class)
//    public void test_normalizedWith24HourDays_max() {
//        Period base = Period.of(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE,
//                Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Long.MAX_VALUE);
//        base.normalizedWith24HourDays();
//    }
//
//    @Test(expectedExceptions=ArithmeticException.class)
//    public void test_normalizedWith24HourDays_maxTime() {
//        Period base = Period.of(0, 0, Integer.MAX_VALUE,
//                Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Long.MAX_VALUE);
//        base.normalizedWith24HourDays();
//    }
//
    //-----------------------------------------------------------------------
    // toDuration()
    //-----------------------------------------------------------------------
    public void test_toDuration() {
        assertEquals(Period.ZERO.toDuration(), Duration.of(0, SECONDS));
        assertEquals(Period.of(0, 0, 0, 4, 5, 6, 7).toDuration(), Duration.ofSeconds((4 * 60 + 5) * 60L + 6, 7));
    }

    public void test_toDuration_calculation() {
        assertEquals(Period.of(0, 0, 0, 2, 0, 0, 0).toDuration(), Duration.ofSeconds(2 * 3600));
        assertEquals(Period.of(0, 0, 0, 0, 2, 0, 0).toDuration(), Duration.of(120, SECONDS));
        assertEquals(Period.of(0, 0, 0, 0, 0, 2, 0).toDuration(), Duration.of(2, SECONDS));
        
        assertEquals(Period.of(0, 0, 0, 0, 0, 3, 1000000000L - 1).toDuration(), Duration.ofSeconds(3, 999999999));
        assertEquals(Period.of(0, 0, 0, 0, 0, 3, 1000000000L).toDuration(), Duration.ofSeconds(4, 0));
    }

    public void test_toDuration_negatives() {
        assertEquals(Period.of(0, 0, 0, 0, 0, 2, 1).toDuration(), Duration.ofSeconds(2, 1));
        assertEquals(Period.of(0, 0, 0, 0, 0, 2, -1).toDuration(), Duration.ofSeconds(1, 999999999));
        assertEquals(Period.of(0, 0, 0, 0, 0, -2, 1).toDuration(), Duration.ofSeconds(-2, 1));
        assertEquals(Period.of(0, 0, 0, 0, 0, -2, -1).toDuration(), Duration.ofSeconds(-3, 999999999));
    }

//    public void test_toDuration_big() {
//        BigInteger calc = BigInteger.valueOf(Long.MAX_VALUE).divide(BINT_1BN)
//                            .add(MAX_BINT)
//                            .add(MAX_BINT.multiply(BINT_60).add(MAX_BINT).multiply(BINT_60));
//        long s = new BigDecimal(calc).longValueExact();
//        calc = BigInteger.valueOf(Long.MAX_VALUE).remainder(BINT_1BN);
//        int n = new BigDecimal(calc).intValueExact();
//        Period test = Period.of(0, 0, 0, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Long.MAX_VALUE);
//        assertEquals(test.toDuration(), Duration.ofSeconds(s, n));
//
//        calc = BigInteger.valueOf(Long.MAX_VALUE).divide(BINT_1BN)
//                            .add(MAX_BINT)
//                            .add(MAX_BINT.multiply(BINT_24).add(MAX_BINT).multiply(BINT_60).add(MAX_BINT).multiply(BINT_60));
//        s = new BigDecimal(calc).longValueExact();
//        calc = BigInteger.valueOf(Long.MAX_VALUE).remainder(BINT_1BN);
//        n = new BigDecimal(calc).intValueExact();
//        test = Period.of(0, 0, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Long.MAX_VALUE);
//        assertEquals(test.toDuration(), Duration.ofSeconds(s, n));
//    }

    @Test(expectedExceptions=DateTimeException.class)
    public void test_toDuration_years() {
        Period.of(1, 0, 0, 4, 5, 6, 7).toDuration();
    }

    @Test(expectedExceptions=DateTimeException.class)
    public void test_toDuration_months() {
        Period.of(0, 1, 0, 4, 5, 6, 7).toDuration();
    }

    @Test(expectedExceptions=DateTimeException.class)
    public void test_toDuration_days() {
        Duration test = Period.of(0, 0, 1, 4, 5, 6, 7).toDuration();
        assertEquals(test, Duration.ofSeconds(101106, 7L));
    }

    //-----------------------------------------------------------------------
    // equals() / hashCode()
    //-----------------------------------------------------------------------
    public void test_equals() {
        assertEquals(Period.of(1, 0, 0, 0, 0, 0).equals(Period.of(1, YEARS)), true);
        assertEquals(Period.of(0, 1, 0, 0, 0, 0).equals(Period.of(1, MONTHS)), true);
        assertEquals(Period.of(0, 0, 1, 0, 0, 0).equals(Period.of(1, DAYS)), true);
        assertEquals(Period.of(0, 0, 0, 1, 0, 0).equals(Period.of(1, HOURS)), true);
        assertEquals(Period.of(0, 0, 0, 0, 1, 0).equals(Period.of(1, MINUTES)), true);
        assertEquals(Period.of(0, 0, 0, 0, 0, 1).equals(Period.of(1, SECONDS)), true);
        assertEquals(Period.of(1, 2, 3, 0, 0, 0).equals(Period.ofDate(1, 2, 3)), true);
        assertEquals(Period.of(0, 0, 0, 1, 2, 3).equals(Period.ofTime(1, 2, 3)), true);
        assertEquals(Period.of(1, 2, 3, 4, 5, 6).equals(Period.of(1, 2, 3, 4, 5, 6)), true);
        
        assertEquals(Period.of(1, YEARS).equals(Period.of(1, YEARS)), true);
        assertEquals(Period.of(1, YEARS).equals(Period.of(2, YEARS)), false);
        
        assertEquals(Period.of(1, MONTHS).equals(Period.of(1, MONTHS)), true);
        assertEquals(Period.of(1, MONTHS).equals(Period.of(2, MONTHS)), false);
        
        assertEquals(Period.of(1, DAYS).equals(Period.of(1, DAYS)), true);
        assertEquals(Period.of(1, DAYS).equals(Period.of(2, DAYS)), false);
        
        assertEquals(Period.of(1, HOURS).equals(Period.of(1, HOURS)), true);
        assertEquals(Period.of(1, HOURS).equals(Period.of(2, HOURS)), false);
        
        assertEquals(Period.of(1, MINUTES).equals(Period.of(1, MINUTES)), true);
        assertEquals(Period.of(1, MINUTES).equals(Period.of(2, MINUTES)), false);
        
        assertEquals(Period.of(1, SECONDS).equals(Period.of(1, SECONDS)), true);
        assertEquals(Period.of(1, SECONDS).equals(Period.of(2, SECONDS)), false);
        
        assertEquals(Period.ofDate(1, 2, 3).equals(Period.ofDate(1, 2, 3)), true);
        assertEquals(Period.ofDate(1, 2, 3).equals(Period.ofDate(0, 2, 3)), false);
        assertEquals(Period.ofDate(1, 2, 3).equals(Period.ofDate(1, 0, 3)), false);
        assertEquals(Period.ofDate(1, 2, 3).equals(Period.ofDate(1, 2, 0)), false);
        
        assertEquals(Period.ofTime(1, 2, 3).equals(Period.ofTime(1, 2, 3)), true);
        assertEquals(Period.ofTime(1, 2, 3).equals(Period.ofTime(0, 2, 3)), false);
        assertEquals(Period.ofTime(1, 2, 3).equals(Period.ofTime(1, 0, 3)), false);
        assertEquals(Period.ofTime(1, 2, 3).equals(Period.ofTime(1, 2, 0)), false);
    }

    public void test_equals_self() {
        Period test = Period.of(1, 2, 3, 4, 5, 6);
        assertEquals(test.equals(test), true);
    }

    public void test_equals_null() {
        Period test = Period.of(1, 2, 3, 4, 5, 6);
        assertEquals(test.equals(null), false);
    }

    public void test_equals_otherClass() {
        Period test = Period.of(1, 2, 3, 4, 5, 6);
        assertEquals(test.equals(""), false);
    }

    //-----------------------------------------------------------------------
    public void test_hashCode() {
        Period test5 = Period.of(5, DAYS);
        Period test6 = Period.of(6, DAYS);
        Period test5M = Period.of(5, MONTHS);
        Period test5Y = Period.of(5, YEARS);
        assertEquals(test5.hashCode() == test5.hashCode(), true);
        assertEquals(test5.hashCode() == test6.hashCode(), false);
        assertEquals(test5.hashCode() == test5M.hashCode(), false);
        assertEquals(test5.hashCode() == test5Y.hashCode(), false);
    }

    //-----------------------------------------------------------------------
    // toString()
    //-----------------------------------------------------------------------
    @DataProvider(name="toStringAndParse")
    Object[][] data_toString() {
        return new Object[][] {
            {Period.ZERO, "PT0S"},
            {Period.of(0, DAYS), "PT0S"},
            {Period.of(1, YEARS), "P1Y"},
            {Period.of(1, MONTHS), "P1M"},
            {Period.of(1, DAYS), "P1D"},
            {Period.of(1, HOURS), "PT1H"},
            {Period.of(1, MINUTES), "PT1M"},
            {Period.of(1, SECONDS), "PT1S"},
            {Period.of(1, 2, 3, 4, 5, 6), "P1Y2M3DT4H5M6S"},
            {Period.of(1, 2, 3, 4, 5, 6, 700000000), "P1Y2M3DT4H5M6.7S"},
            {Period.of(0, 0, 0, 0, 0, 0, 100000000), "PT0.1S"},
            {Period.of(0, 0, 0, 0, 0, 0, -100000000), "PT-0.1S"},
            {Period.of(0, 0, 0, 0, 0, 1, -900000000), "PT0.1S"},
            {Period.of(0, 0, 0, 0, 0, -1, 900000000), "PT-0.1S"},
            {Period.of(0, 0, 0, 0, 0, 1, 100000000), "PT1.1S"},
            {Period.of(0, 0, 0, 0, 0, 1, -100000000), "PT0.9S"},
            {Period.of(0, 0, 0, 0, 0, -1, 100000000), "PT-0.9S"},
            {Period.of(0, 0, 0, 0, 0, -1, -100000000), "PT-1.1S"},
            {Period.of(0, 0, 0, 0, 0, 0, 10000000), "PT0.01S"},
            {Period.of(0, 0, 0, 0, 0, 0, -10000000), "PT-0.01S"},
            {Period.of(0, 0, 0, 0, 0, 0, 1000000), "PT0.001S"},
            {Period.of(0, 0, 0, 0, 0, 0, -1000000), "PT-0.001S"},
            {Period.of(0, 0, 0, 0, 0, 0, 1000), "PT0.000001S"},
            {Period.of(0, 0, 0, 0, 0, 0, -1000), "PT-0.000001S"},
            {Period.of(0, 0, 0, 0, 0, 0, 1), "PT0.000000001S"},
            {Period.of(0, 0, 0, 0, 0, 0, -1), "PT-0.000000001S"},
        };
    }

    //-----------------------------------------------------------------------
    private void assertPeriod(Period test, int y, int mo, int d, int h, int mn, int s, long n) {
        assertEquals(test.getYears(), y, "years");
        assertEquals(test.getMonths(), mo, "months");
        assertEquals(test.getDays(), d, "days");
        assertEquals(test.getHours(), h, "hours");
        assertEquals(test.getMinutes(), mn, "mins");
        assertEquals(test.getSeconds(), s, "secs");
        assertEquals(test.getNanos(), n, "nanos");
        assertEquals(test.getTimeNanos(), (((h * 60L + mn) * 60 + s) * 1_000_000_000L + n), "total nanos");
    }

}
