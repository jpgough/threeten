/*
 * Copyright (c) 2012, Stephen Colebourne & Michael Nascimento Santos
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
package javax.time.calendrical;

import static javax.time.Month.AUGUST;
import static javax.time.Month.FEBRUARY;
import static javax.time.Month.JULY;
import static javax.time.Month.JUNE;
import static javax.time.Month.MARCH;
import static javax.time.Month.OCTOBER;
import static javax.time.Month.SEPTEMBER;
import static javax.time.calendrical.LocalPeriodUnit.DAYS;
import static javax.time.calendrical.LocalPeriodUnit.MONTHS;
import static javax.time.calendrical.LocalPeriodUnit.WEEKS;
import static javax.time.calendrical.LocalPeriodUnit.YEARS;
import static org.testng.Assert.assertEquals;

import javax.time.LocalDate;
import javax.time.Month;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test.
 */
@Test
public class TestLocalPeriodUnit {

    //-----------------------------------------------------------------------
    @DataProvider(name = "yearsBetween")
    Object[][] data_yearsBetween() {
        return new Object[][] {
            {date(1939, SEPTEMBER, 2), date(1939, SEPTEMBER, 1), 0},
            {date(1939, SEPTEMBER, 2), date(1939, SEPTEMBER, 2), 0},
            {date(1939, SEPTEMBER, 2), date(1939, SEPTEMBER, 3), 0},
            
            {date(1939, SEPTEMBER, 2), date(1940, SEPTEMBER, 1), 0},
            {date(1939, SEPTEMBER, 2), date(1940, SEPTEMBER, 2), 1},
            {date(1939, SEPTEMBER, 2), date(1940, SEPTEMBER, 3), 1},
            
            {date(1939, SEPTEMBER, 2), date(1938, SEPTEMBER, 1), -1},
            {date(1939, SEPTEMBER, 2), date(1938, SEPTEMBER, 2), -1},
            {date(1939, SEPTEMBER, 2), date(1938, SEPTEMBER, 3), 0},
            
            {date(1939, SEPTEMBER, 2), date(1945, SEPTEMBER, 3), 6},
            {date(1939, SEPTEMBER, 2), date(1945, OCTOBER, 3), 6},
            {date(1939, SEPTEMBER, 2), date(1945, AUGUST, 3), 5},
        };
    }

    @Test(dataProvider = "yearsBetween")
    public void test_yearsBetween(LocalDate start, LocalDate end, long expected) {
        assertEquals(YEARS.between(start, end), expected);
    }

    @Test(dataProvider = "yearsBetween")
    public void test_yearsBetweenReversed(LocalDate start, LocalDate end, long expected) {
        assertEquals(YEARS.between(end, start), -expected);
    }

    //-----------------------------------------------------------------------
    @DataProvider(name = "monthsBetween")
    Object[][] data_monthsBetween() {
        return new Object[][] {
            {date(2012, JULY, 2), date(2012, JULY, 1), 0},
            {date(2012, JULY, 2), date(2012, JULY, 2), 0},
            {date(2012, JULY, 2), date(2012, JULY, 3), 0},
            
            {date(2012, JULY, 2), date(2012, AUGUST, 1), 0},
            {date(2012, JULY, 2), date(2012, AUGUST, 2), 1},
            {date(2012, JULY, 2), date(2012, AUGUST, 3), 1},
            
            {date(2012, JULY, 2), date(2012, SEPTEMBER, 1), 1},
            {date(2012, JULY, 2), date(2012, SEPTEMBER, 2), 2},
            {date(2012, JULY, 2), date(2012, SEPTEMBER, 3), 2},
            
            {date(2012, JULY, 2), date(2012, JUNE, 1), -1},
            {date(2012, JULY, 2), date(2012, JUNE, 2), -1},
            {date(2012, JULY, 2), date(2012, JUNE, 3), 0},
            
            {date(2012, FEBRUARY, 27), date(2012, MARCH, 26), 0},
            {date(2012, FEBRUARY, 27), date(2012, MARCH, 27), 1},
            {date(2012, FEBRUARY, 27), date(2012, MARCH, 28), 1},
            
            {date(2012, FEBRUARY, 28), date(2012, MARCH, 27), 0},
            {date(2012, FEBRUARY, 28), date(2012, MARCH, 28), 1},
            {date(2012, FEBRUARY, 28), date(2012, MARCH, 29), 1},
            
            {date(2012, FEBRUARY, 29), date(2012, MARCH, 28), 0},
            {date(2012, FEBRUARY, 29), date(2012, MARCH, 29), 1},
            {date(2012, FEBRUARY, 29), date(2012, MARCH, 30), 1},
        };
    }

    @Test(dataProvider = "monthsBetween")
    public void test_monthsBetween(LocalDate start, LocalDate end, long expected) {
        assertEquals(MONTHS.between(start, end), expected);
    }

    @Test(dataProvider = "monthsBetween")
    public void test_monthsBetweenReversed(LocalDate start, LocalDate end, long expected) {
        assertEquals(MONTHS.between(end, start), -expected);
    }

    //-----------------------------------------------------------------------
    @DataProvider(name = "weeksBetween")
    Object[][] data_weeksBetween() {
        return new Object[][] {
            {date(2012, JULY, 2), date(2012, JUNE, 25), -1},
            {date(2012, JULY, 2), date(2012, JUNE, 26), 0},
            {date(2012, JULY, 2), date(2012, JULY, 2), 0},
            {date(2012, JULY, 2), date(2012, JULY, 8), 0},
            {date(2012, JULY, 2), date(2012, JULY, 9), 1},
            
            {date(2012, FEBRUARY, 28), date(2012, FEBRUARY, 21), -1},
            {date(2012, FEBRUARY, 28), date(2012, FEBRUARY, 22), 0},
            {date(2012, FEBRUARY, 28), date(2012, FEBRUARY, 28), 0},
            {date(2012, FEBRUARY, 28), date(2012, FEBRUARY, 29), 0},
            {date(2012, FEBRUARY, 28), date(2012, MARCH, 1), 0},
            {date(2012, FEBRUARY, 28), date(2012, MARCH, 5), 0},
            {date(2012, FEBRUARY, 28), date(2012, MARCH, 6), 1},
            
            {date(2012, FEBRUARY, 29), date(2012, FEBRUARY, 22), -1},
            {date(2012, FEBRUARY, 29), date(2012, FEBRUARY, 23), 0},
            {date(2012, FEBRUARY, 29), date(2012, FEBRUARY, 28), 0},
            {date(2012, FEBRUARY, 29), date(2012, FEBRUARY, 29), 0},
            {date(2012, FEBRUARY, 29), date(2012, MARCH, 1), 0},
            {date(2012, FEBRUARY, 29), date(2012, MARCH, 6), 0},
            {date(2012, FEBRUARY, 29), date(2012, MARCH, 7), 1},
        };
    }

    @Test(dataProvider = "weeksBetween")
    public void test_weeksBetween(LocalDate start, LocalDate end, long expected) {
        assertEquals(WEEKS.between(start, end), expected);
    }

    @Test(dataProvider = "weeksBetween")
    public void test_weeksBetweenReversed(LocalDate start, LocalDate end, long expected) {
        assertEquals(WEEKS.between(end, start), -expected);
    }

    //-----------------------------------------------------------------------
    @DataProvider(name = "daysBetween")
    Object[][] data_daysBetween() {
        return new Object[][] {
            {date(2012, JULY, 2), date(2012, JULY, 1), -1},
            {date(2012, JULY, 2), date(2012, JULY, 2), 0},
            {date(2012, JULY, 2), date(2012, JULY, 3), 1},
            
            {date(2012, FEBRUARY, 28), date(2012, FEBRUARY, 27), -1},
            {date(2012, FEBRUARY, 28), date(2012, FEBRUARY, 28), 0},
            {date(2012, FEBRUARY, 28), date(2012, FEBRUARY, 29), 1},
            {date(2012, FEBRUARY, 28), date(2012, MARCH, 1), 2},
            
            {date(2012, FEBRUARY, 29), date(2012, FEBRUARY, 27), -2},
            {date(2012, FEBRUARY, 29), date(2012, FEBRUARY, 28), -1},
            {date(2012, FEBRUARY, 29), date(2012, FEBRUARY, 29), 0},
            {date(2012, FEBRUARY, 29), date(2012, MARCH, 1), 1},
            
            {date(2012, MARCH, 1), date(2012, FEBRUARY, 27), -3},
            {date(2012, MARCH, 1), date(2012, FEBRUARY, 28), -2},
            {date(2012, MARCH, 1), date(2012, FEBRUARY, 29), -1},
            {date(2012, MARCH, 1), date(2012, MARCH, 1), 0},
            {date(2012, MARCH, 1), date(2012, MARCH, 2), 1},
            
            {date(2012, MARCH, 1), date(2013, FEBRUARY, 28), 364},
            {date(2012, MARCH, 1), date(2013, MARCH, 1), 365},
            
            {date(2011, MARCH, 1), date(2012, FEBRUARY, 28), 364},
            {date(2011, MARCH, 1), date(2012, FEBRUARY, 29), 365},
            {date(2011, MARCH, 1), date(2012, MARCH, 1), 366},
        };
    }

    @Test(dataProvider = "daysBetween")
    public void test_daysBetween(LocalDate start, LocalDate end, long expected) {
        assertEquals(DAYS.between(start, end), expected);
    }

    @Test(dataProvider = "daysBetween")
    public void test_daysBetweenReversed(LocalDate start, LocalDate end, long expected) {
        assertEquals(DAYS.between(end, start), -expected);
    }

    //-----------------------------------------------------------------------
    private static LocalDate date(int year, Month month, int dom) {
        return LocalDate.of(year, month, dom);
    }

}
