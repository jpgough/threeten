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

import static javax.time.calendrical.LocalDateTimeField.OFFSET_SECONDS;
import static javax.time.calendrical.LocalPeriodUnit.HOURS;
import static javax.time.calendrical.LocalPeriodUnit.MINUTES;
import static javax.time.calendrical.LocalPeriodUnit.SECONDS;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import javax.time.calendrical.DateTime;
import javax.time.calendrical.DateTimeField;
import javax.time.calendrical.LocalDateTimeField;
import javax.time.calendrical.LocalPeriodUnit;
import javax.time.calendrical.MockFieldNoValue;
import javax.time.calendrical.PeriodUnit;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test ZoneOffset.
 */
@Test
public class TestZoneOffset {

    //-----------------------------------------------------------------------
    // Basics
    //-----------------------------------------------------------------------
	@Test(groups={"tck"})
    public void test_immutable() {
        Class<ZoneOffset> cls = ZoneOffset.class;
        assertTrue(Modifier.isPublic(cls.getModifiers()));
        assertTrue(Modifier.isFinal(cls.getModifiers()));
        Field[] fields = cls.getDeclaredFields();
        for (Field field : fields) {
            if (Modifier.isStatic(field.getModifiers()) == false) {
                assertTrue(Modifier.isPrivate(field.getModifiers()));
                assertTrue(Modifier.isFinal(field.getModifiers()));
            }
        }
    }

	@Test(groups={"tck"})
    public void test_serialization() throws Exception {
        ZoneOffset test = ZoneOffset.of("+01:30");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(baos);
        out.writeObject(test);
        baos.close();
        byte[] bytes = baos.toByteArray();
        
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        ObjectInputStream in = new ObjectInputStream(bais);
        ZoneOffset result = (ZoneOffset) in.readObject();
        
        doTestOffset(result, 1, 30, 0);
    }

    //-----------------------------------------------------------------------
    // Creation
    //-----------------------------------------------------------------------
	@Test(groups={"tck"})
    public void test_constant_UTC() {
        ZoneOffset test = ZoneOffset.UTC;
        doTestOffset(test, 0, 0, 0);
    }

    //-----------------------------------------------------------------------
	@Test(groups={"tck"})
    public void test_factory_string_UTC() {
        String[] values = new String[] {
            "Z",
            "+00","+0000","+00:00","+000000","+00:00:00",
            "-00","-0000","-00:00","-000000","-00:00:00",
        };
        for (int i = 0; i < values.length; i++) {
            ZoneOffset test = ZoneOffset.of(values[i]);
            assertSame(test, ZoneOffset.UTC);
        }
    }

	@Test(groups={"tck"})
    public void test_factory_string_invalid() {
        String[] values = new String[] {
            "","A","B","C","D","E","F","G","H","I","J","K","L","M",
            "N","O","P","Q","R","S","T","U","V","W","X","Y","ZZ",
            "+0","+0:00","+00:0","+0:0",
            "+000","+00000",
            "+0:00:00","+00:0:00","+00:00:0","+0:0:0","+0:0:00","+00:0:0","+0:00:0",
            "+01_00","+01;00","+01@00","+01:AA",
            "+19","+19:00","+18:01","+18:00:01","+1801","+180001",
            "-0","-0:00","-00:0","-0:0",
            "-000","-00000",
            "-0:00:00","-00:0:00","-00:00:0","-0:0:0","-0:0:00","-00:0:0","-0:00:0",
            "-19","-19:00","-18:01","-18:00:01","-1801","-180001",
            "-01_00","-01;00","-01@00","-01:AA",
            "@01:00",
        };
        for (int i = 0; i < values.length; i++) {
            try {
                ZoneOffset.of(values[i]);
                fail("Should have failed:" + values[i]);
            } catch (IllegalArgumentException ex) {
                // expected
            }
        }
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void test_factory_string_null() {
        ZoneOffset.of((String) null);
    }

    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_factory_string_hours() {
        for (int i = -18; i <= 18; i++) {
            String str = (i < 0 ? "-" : "+") + Integer.toString(Math.abs(i) + 100).substring(1);
            ZoneOffset test = ZoneOffset.of(str);
            doTestOffset(test, i, 0, 0);
        }
    }

    @Test(groups={"tck"})
    public void test_factory_string_hours_minutes_noColon() {
        for (int i = -17; i <= 17; i++) {
            for (int j = -59; j <= 59; j++) {
                if ((i < 0 && j <= 0) || (i > 0 && j >= 0) || i == 0) {
                    String str = (i < 0 || j < 0 ? "-" : "+") +
                        Integer.toString(Math.abs(i) + 100).substring(1) +
                        Integer.toString(Math.abs(j) + 100).substring(1);
                    ZoneOffset test = ZoneOffset.of(str);
                    doTestOffset(test, i, j, 0);
                }
            }
        }
        ZoneOffset test1 = ZoneOffset.of("-1800");
        doTestOffset(test1, -18, 0, 0);
        ZoneOffset test2 = ZoneOffset.of("+1800");
        doTestOffset(test2, 18, 0, 0);
    }

    @Test(groups={"tck"})
    public void test_factory_string_hours_minutes_colon() {
        for (int i = -17; i <= 17; i++) {
            for (int j = -59; j <= 59; j++) {
                if ((i < 0 && j <= 0) || (i > 0 && j >= 0) || i == 0) {
                    String str = (i < 0 || j < 0 ? "-" : "+") +
                        Integer.toString(Math.abs(i) + 100).substring(1) + ":" +
                        Integer.toString(Math.abs(j) + 100).substring(1);
                    ZoneOffset test = ZoneOffset.of(str);
                    doTestOffset(test, i, j, 0);
                }
            }
        }
        ZoneOffset test1 = ZoneOffset.of("-18:00");
        doTestOffset(test1, -18, 0, 0);
        ZoneOffset test2 = ZoneOffset.of("+18:00");
        doTestOffset(test2, 18, 0, 0);
    }

    @Test(groups={"tck"})
    public void test_factory_string_hours_minutes_seconds_noColon() {
        for (int i = -17; i <= 17; i++) {
            for (int j = -59; j <= 59; j++) {
                for (int k = -59; k <= 59; k++) {
                    if ((i < 0 && j <= 0 && k <= 0) || (i > 0 && j >= 0 && k >= 0) ||
                            (i == 0 && ((j < 0 && k <= 0) || (j > 0 && k >= 0) || j == 0))) {
                        String str = (i < 0 || j < 0 || k < 0 ? "-" : "+") +
                            Integer.toString(Math.abs(i) + 100).substring(1) +
                            Integer.toString(Math.abs(j) + 100).substring(1) +
                            Integer.toString(Math.abs(k) + 100).substring(1);
                        ZoneOffset test = ZoneOffset.of(str);
                        doTestOffset(test, i, j, k);
                    }
                }
            }
        }
        ZoneOffset test1 = ZoneOffset.of("-180000");
        doTestOffset(test1, -18, 0, 0);
        ZoneOffset test2 = ZoneOffset.of("+180000");
        doTestOffset(test2, 18, 0, 0);
    }

    @Test(groups={"tck"})
    public void test_factory_string_hours_minutes_seconds_colon() {
        for (int i = -17; i <= 17; i++) {
            for (int j = -59; j <= 59; j++) {
                for (int k = -59; k <= 59; k++) {
                    if ((i < 0 && j <= 0 && k <= 0) || (i > 0 && j >= 0 && k >= 0) ||
                            (i == 0 && ((j < 0 && k <= 0) || (j > 0 && k >= 0) || j == 0))) {
                        String str = (i < 0 || j < 0 || k < 0 ? "-" : "+") +
                            Integer.toString(Math.abs(i) + 100).substring(1) + ":" +
                            Integer.toString(Math.abs(j) + 100).substring(1) + ":" +
                            Integer.toString(Math.abs(k) + 100).substring(1);
                        ZoneOffset test = ZoneOffset.of(str);
                        doTestOffset(test, i, j, k);
                    }
                }
            }
        }
        ZoneOffset test1 = ZoneOffset.of("-18:00:00");
        doTestOffset(test1, -18, 0, 0);
        ZoneOffset test2 = ZoneOffset.of("+18:00:00");
        doTestOffset(test2, 18, 0, 0);
    }

    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_factory_int_hours() {
        for (int i = -18; i <= 18; i++) {
            ZoneOffset test = ZoneOffset.ofHours(i);
            doTestOffset(test, i, 0, 0);
        }
    }

    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_factory_int_hours_minutes() {
        for (int i = -17; i <= 17; i++) {
            for (int j = -59; j <= 59; j++) {
                if ((i < 0 && j <= 0) || (i > 0 && j >= 0) || i == 0) {
                    ZoneOffset test = ZoneOffset.ofHoursMinutes(i, j);
                    doTestOffset(test, i, j, 0);
                }
            }
        }
        ZoneOffset test1 = ZoneOffset.ofHoursMinutes(-18, 0);
        doTestOffset(test1, -18, 0, 0);
        ZoneOffset test2 = ZoneOffset.ofHoursMinutes(18, 0);
        doTestOffset(test2, 18, 0, 0);
    }

    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_factory_int_hours_minutes_seconds() {
        for (int i = -17; i <= 17; i++) {
            for (int j = -59; j <= 59; j++) {
                for (int k = -59; k <= 59; k++) {
                    if ((i < 0 && j <= 0 && k <= 0) || (i > 0 && j >= 0 && k >= 0) ||
                            (i == 0 && ((j < 0 && k <= 0) || (j > 0 && k >= 0) || j == 0))) {
                        ZoneOffset test = ZoneOffset.ofHoursMinutesSeconds(i, j, k);
                        doTestOffset(test, i, j, k);
                    }
                }
            }
        }
        ZoneOffset test1 = ZoneOffset.ofHoursMinutesSeconds(-18, 0, 0);
        doTestOffset(test1, -18, 0, 0);
        ZoneOffset test2 = ZoneOffset.ofHoursMinutesSeconds(18, 0, 0);
        doTestOffset(test2, 18, 0, 0);
    }

    @Test(expectedExceptions=IllegalArgumentException.class, groups={"tck"})
    public void test_factory_int_hours_minutes_seconds_plusHoursMinusMinutes() {
        ZoneOffset.ofHoursMinutesSeconds(1, -1, 0);
    }

    @Test(expectedExceptions=IllegalArgumentException.class, groups={"tck"})
    public void test_factory_int_hours_minutes_seconds_plusHoursMinusSeconds() {
        ZoneOffset.ofHoursMinutesSeconds(1, 0, -1);
    }

    @Test(expectedExceptions=IllegalArgumentException.class, groups={"tck"})
    public void test_factory_int_hours_minutes_seconds_minusHoursPlusMinutes() {
        ZoneOffset.ofHoursMinutesSeconds(-1, 1, 0);
    }

    @Test(expectedExceptions=IllegalArgumentException.class, groups={"tck"})
    public void test_factory_int_hours_minutes_seconds_minusHoursPlusSeconds() {
        ZoneOffset.ofHoursMinutesSeconds(-1, 0, 1);
    }

    @Test(expectedExceptions=IllegalArgumentException.class, groups={"tck"})
    public void test_factory_int_hours_minutes_seconds_zeroHoursMinusMinutesPlusSeconds() {
        ZoneOffset.ofHoursMinutesSeconds(0, -1, 1);
    }

    @Test(expectedExceptions=IllegalArgumentException.class, groups={"tck"})
    public void test_factory_int_hours_minutes_seconds_zeroHoursPlusMinutesMinusSeconds() {
        ZoneOffset.ofHoursMinutesSeconds(0, 1, -1);
    }

    @Test(expectedExceptions=IllegalArgumentException.class, groups={"tck"})
    public void test_factory_int_hours_minutes_seconds_minutesTooLarge() {
        ZoneOffset.ofHoursMinutesSeconds(0, 60, 0);
    }

    @Test(expectedExceptions=IllegalArgumentException.class, groups={"tck"})
    public void test_factory_int_hours_minutes_seconds_minutesTooSmall() {
        ZoneOffset.ofHoursMinutesSeconds(0, -60, 0);
    }

    @Test(expectedExceptions=IllegalArgumentException.class, groups={"tck"})
    public void test_factory_int_hours_minutes_seconds_secondsTooLarge() {
        ZoneOffset.ofHoursMinutesSeconds(0, 0, 60);
    }

    @Test(expectedExceptions=IllegalArgumentException.class, groups={"tck"})
    public void test_factory_int_hours_minutes_seconds_secondsTooSmall() {
        ZoneOffset.ofHoursMinutesSeconds(0, 0, 60);
    }

    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_factory_ofTotalSeconds() {
        assertEquals(ZoneOffset.ofTotalSeconds(60 * 60 + 1), ZoneOffset.ofHoursMinutesSeconds(1, 0, 1));
        assertEquals(ZoneOffset.ofTotalSeconds(18 * 60 * 60), ZoneOffset.ofHours(18));
        assertEquals(ZoneOffset.ofTotalSeconds(-18 * 60 * 60), ZoneOffset.ofHours(-18));
    }
    
    @Test(groups={"implementation"})
    public void test_factory_ofTotalSecondsSame() {
    	assertSame(ZoneOffset.ofTotalSeconds(0), ZoneOffset.UTC);
    }

    @Test(expectedExceptions=IllegalArgumentException.class, groups={"tck"})
    public void test_factory_ofTotalSeconds_tooLarge() {
        ZoneOffset.ofTotalSeconds(18 * 60 * 60 + 1);
    }

    @Test(expectedExceptions=IllegalArgumentException.class, groups={"tck"})
    public void test_factory_ofTotalSeconds_tooSmall() {
        ZoneOffset.ofTotalSeconds(-18 * 60 * 60 - 1);
    }

    //-----------------------------------------------------------------------
    // from()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_factory_CalendricalObject() {
        assertEquals(ZoneOffset.from(OffsetDate.of(2012, 5, 2, ZoneOffset.ofHours(6))), ZoneOffset.ofHours(6));
        assertEquals(ZoneOffset.from(OffsetDateTime.of(2007, 7, 15, 17, 30, 0, 0, ZoneOffset.ofHours(2))), ZoneOffset.ofHours(2));
    }

    @Test(expectedExceptions=DateTimeException.class, groups={"tck"})
    public void test_factory_CalendricalObject_invalid_noDerive() {
        ZoneOffset.from(LocalTime.of(12, 30));
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void test_factory_CalendricalObject_null() {
        ZoneOffset.from((DateTime) null);
    }

    //-----------------------------------------------------------------------
    // getTotalSeconds()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_getTotalSeconds() {
        ZoneOffset offset = ZoneOffset.ofTotalSeconds(60 * 60 + 1);
        assertEquals(offset.getTotalSeconds(), 60 * 60 + 1);
    }

    //-----------------------------------------------------------------------
    // getID()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_getID() {
        ZoneOffset offset = ZoneOffset.ofHoursMinutesSeconds(1, 0, 0);
        assertEquals(offset.getID(), "+01:00");
        offset = ZoneOffset.ofHoursMinutesSeconds(1, 2, 3);
        assertEquals(offset.getID(), "+01:02:03");
        offset = ZoneOffset.UTC;
        assertEquals(offset.getID(), "Z");
    }

    //-----------------------------------------------------------------------
    // get(DateTimeField)
    //-----------------------------------------------------------------------
    @DataProvider(name="invalidFields")
    Object[][] data_invalidFields() {
        return new Object[][] {
            {LocalDateTimeField.NANO_OF_DAY},
            {LocalDateTimeField.HOUR_OF_DAY},
            {LocalDateTimeField.INSTANT_SECONDS},
            {MockFieldNoValue.INSTANCE},
        };
    }

    @Test(groups={"tck"})
    public void test_get_DateTimeField() {
        assertEquals(ZoneOffset.UTC.get(OFFSET_SECONDS), 0);
        assertEquals(ZoneOffset.ofHours(-2).get(OFFSET_SECONDS), -7200);
        assertEquals(ZoneOffset.ofHoursMinutesSeconds(0, 1, 5).get(OFFSET_SECONDS), 65);
    }

    @Test(dataProvider="invalidFields", expectedExceptions=DateTimeException.class, groups={"tck"} )
    public void test_get_DateTimeField_invalidField(DateTimeField field) {
        ZoneOffset.UTC.get(field);
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"} )
    public void test_get_DateTimeField_null() {
        ZoneOffset.UTC.get((DateTimeField) null);
    }

    //-----------------------------------------------------------------------
    // with(DateTimeField, long)
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_with_DateTimeField() {
        assertEquals(ZoneOffset.UTC.with(OFFSET_SECONDS, 3600), ZoneOffset.ofHours(1));
        assertEquals(ZoneOffset.ofHours(-2).with(OFFSET_SECONDS, 3609), ZoneOffset.ofHoursMinutesSeconds(1, 0, 9));
        assertEquals(ZoneOffset.ofHoursMinutesSeconds(0, 1, 5).with(OFFSET_SECONDS, 0), ZoneOffset.UTC);
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"} )
    public void test_with_DateTimeField_null() {
        ZoneOffset.UTC.with((DateTimeField) null, 0);
    }

    @Test(dataProvider="invalidFields", expectedExceptions=DateTimeException.class, groups={"tck"} )
    public void test_with_DateTimeField_invalidField(DateTimeField field) {
        ZoneOffset.UTC.with(field, 0);
    }

    //-----------------------------------------------------------------------
    // plus(PeriodUnit, long)
    //-----------------------------------------------------------------------
    @DataProvider(name="invalidUnits")
    Object[][] data_invalidUnits() {
        return new Object[][] {
            {LocalPeriodUnit.NANOS},
            {LocalPeriodUnit.HALF_DAYS},
            {LocalPeriodUnit.DAYS},
        };
    }

    @Test(groups={"tck"})
    public void test_plus_PeriodUnit() {
        ZoneOffset base = ZoneOffset.ofHoursMinutesSeconds(1, 2, 3);
        assertEquals(base.plus(3, HOURS), ZoneOffset.ofHoursMinutesSeconds(4, 2, 3));
        assertEquals(base.plus(-3, HOURS), ZoneOffset.ofHoursMinutesSeconds(-1, -57, -57));
        
        assertEquals(base.plus(3, MINUTES), ZoneOffset.ofHoursMinutesSeconds(1, 5, 3));
        assertEquals(base.plus(63, MINUTES), ZoneOffset.ofHoursMinutesSeconds(2, 5, 3));
        
        assertEquals(base.plus(3, SECONDS), ZoneOffset.ofHoursMinutesSeconds(1, 2, 6));
        assertEquals(base.plus(63, SECONDS), ZoneOffset.ofHoursMinutesSeconds(1, 3, 6));
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"} )
    public void test_plus_PeriodUnit_null() {
        ZoneOffset.UTC.plus(0, (PeriodUnit) null);
    }

    @Test(dataProvider="invalidUnits", expectedExceptions=DateTimeException.class, groups={"tck"} )
    public void test_plus_PeriodUnit_invalidField(PeriodUnit unit) {
        ZoneOffset.UTC.plus(1, unit);
    }

    //-----------------------------------------------------------------------
    // minus(PeriodUnit, long)
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_minus_PeriodUnit() {
        ZoneOffset base = ZoneOffset.ofHoursMinutesSeconds(1, 2, 3);
        assertEquals(base.minus(3, HOURS), ZoneOffset.ofHoursMinutesSeconds(-1, -57, -57));
        assertEquals(base.minus(-3, HOURS), ZoneOffset.ofHoursMinutesSeconds(4, 2, 3));
        
        assertEquals(base.minus(3, MINUTES), ZoneOffset.ofHoursMinutesSeconds(0, 59, 3));
        assertEquals(base.minus(63, MINUTES), ZoneOffset.ofHoursMinutesSeconds(0, 0, -57));
        
        assertEquals(base.minus(3, SECONDS), ZoneOffset.ofHoursMinutesSeconds(1, 2, 0));
        assertEquals(base.minus(63, SECONDS), ZoneOffset.ofHoursMinutesSeconds(1, 1, 0));
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"} )
    public void test_minus_PeriodUnit_null() {
        ZoneOffset.UTC.minus(0, (PeriodUnit) null);
    }

    @Test(dataProvider="invalidUnits", expectedExceptions=DateTimeException.class, groups={"tck"} )
    public void test_minus_PeriodUnit_invalidField(PeriodUnit unit) {
        ZoneOffset.UTC.minus(1, unit);
    }

    //-----------------------------------------------------------------------
    // toZoneId()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_toZoneId() {
        ZoneOffset offset = ZoneOffset.ofHoursMinutesSeconds(1, 2, 3);
        assertEquals(offset.toZoneId(), ZoneId.of(offset));
    }

    //-----------------------------------------------------------------------
    // compareTo()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_compareTo() {
        ZoneOffset offset1 = ZoneOffset.ofHoursMinutesSeconds(1, 2, 3);
        ZoneOffset offset2 = ZoneOffset.ofHoursMinutesSeconds(2, 3, 4);
        assertTrue(offset1.compareTo(offset2) > 0);
        assertTrue(offset2.compareTo(offset1) < 0);
        assertTrue(offset1.compareTo(offset1) == 0);
        assertTrue(offset2.compareTo(offset2) == 0);
    }

    //-----------------------------------------------------------------------
    // equals() / hashCode()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_equals() {
        ZoneOffset offset1 = ZoneOffset.ofHoursMinutesSeconds(1, 2, 3);
        ZoneOffset offset2 = ZoneOffset.ofHoursMinutesSeconds(2, 3, 4);
        ZoneOffset offset2b = ZoneOffset.ofHoursMinutesSeconds(2, 3, 4);
        assertEquals(offset1.equals(offset2), false);
        assertEquals(offset2.equals(offset1), false);
        
        assertEquals(offset1.equals(offset1), true);
        assertEquals(offset2.equals(offset2), true);
        assertEquals(offset2.equals(offset2b), true);
        
        assertEquals(offset1.hashCode() == offset1.hashCode(), true);
        assertEquals(offset2.hashCode() == offset2.hashCode(), true);
        assertEquals(offset2.hashCode() == offset2b.hashCode(), true);
    }

    //-----------------------------------------------------------------------
    // toString()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_toString() {
        ZoneOffset offset = ZoneOffset.ofHoursMinutesSeconds(1, 0, 0);
        assertEquals(offset.toString(), "+01:00");
        offset = ZoneOffset.ofHoursMinutesSeconds(1, 2, 3);
        assertEquals(offset.toString(), "+01:02:03");
        offset = ZoneOffset.UTC;
        assertEquals(offset.toString(), "Z");
    }

    //-----------------------------------------------------------------------
    //-----------------------------------------------------------------------
    //-----------------------------------------------------------------------
    private void doTestOffset(ZoneOffset offset, int hours, int minutes, int seconds) {
        assertEquals(offset.getTotalSeconds(), hours * 60 * 60 + minutes * 60 + seconds);
        final String id;
        if (hours == 0 && minutes == 0 && seconds == 0) {
            id = "Z";
        } else {
            String str = (hours < 0 || minutes < 0 || seconds < 0) ? "-" : "+";
            str += Integer.toString(Math.abs(hours) + 100).substring(1);
            str += ":";
            str += Integer.toString(Math.abs(minutes) + 100).substring(1);
            if (seconds != 0) {
                str += ":";
                str += Integer.toString(Math.abs(seconds) + 100).substring(1);
            }
            id = str;
        }
        assertEquals(offset.getID(), id);
        assertEquals(offset, ZoneOffset.ofHoursMinutesSeconds(hours, minutes, seconds));
        if (seconds == 0) {
            assertEquals(offset, ZoneOffset.ofHoursMinutes(hours, minutes));
            if (minutes == 0) {
                assertEquals(offset, ZoneOffset.ofHours(hours));
            }
        }
        assertEquals(ZoneOffset.of(id), offset);
        assertEquals(offset.toZoneId(), ZoneId.of(offset));
        assertEquals(offset.toString(), id);
    }

}
