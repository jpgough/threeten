/*
 * Copyright (c) 2007-2012, Stephen Colebourne & Michael Nascimento Santos
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

import static javax.time.calendrical.LocalDateTimeField.MONTH_OF_YEAR;
import static javax.time.calendrical.LocalDateTimeUnit.HALF_YEARS;
import static javax.time.calendrical.LocalDateTimeUnit.MONTHS;
import static javax.time.calendrical.LocalDateTimeUnit.QUARTER_YEARS;

import javax.time.calendrical.CalendricalAdjuster;
import javax.time.calendrical.CalendricalObject;
import javax.time.calendrical.DateTimeAdjuster;
import javax.time.calendrical.DateTimeBuilder;
import javax.time.calendrical.DateTimeField;
import javax.time.calendrical.DateTimeObject;
import javax.time.calendrical.LocalDateTimeField;
import javax.time.calendrical.LocalDateTimeUnit;
import javax.time.calendrical.PeriodUnit;

/**
 * A month-of-year, such as 'July'.
 * <p>
 * {@code Month} is an enum representing the 12 months of the year -
 * January, February, March, April, May, June, July, August, September, October,
 * November and December.
 * <p>
 * In addition to the textual enum name, each month-of-year has an {@code int} value.
 * The {@code int} value follows normal usage and the ISO-8601 standard,
 * from 1 (January) to 12 (December). It is recommended that applications use the enum
 * rather than the {@code int} value to ensure code clarity.
 * <p>
 * <b>Do not use {@code ordinal()} to obtain the numeric representation of {@code Month}.
 * Use {@code getValue()} instead.</b>
 * <p>
 * This enum represents a common concept that is found in many calendar systems.
 * As such, this enum may be used by any calendar system that has the month-of-year
 * concept defined exactly equivalent to the ISO calendar system.
 * 
 * <h4>Implementation notes</h4>
 * This is an immutable and thread-safe enum.
 */
public enum Month implements DateTimeObject, DateTimeAdjuster {

    /**
     * The singleton instance for the month of January with 31 days.
     * This has the numeric value of {@code 1}.
     */
    JANUARY,
    /**
     * The singleton instance for the month of February with 28 days, or 29 in a leap year.
     * This has the numeric value of {@code 2}.
     */
    FEBRUARY,
    /**
     * The singleton instance for the month of March with 31 days.
     * This has the numeric value of {@code 3}.
     */
    MARCH,
    /**
     * The singleton instance for the month of April with 30 days.
     * This has the numeric value of {@code 4}.
     */
    APRIL,
    /**
     * The singleton instance for the month of May with 31 days.
     * This has the numeric value of {@code 5}.
     */
    MAY,
    /**
     * The singleton instance for the month of June with 30 days.
     * This has the numeric value of {@code 6}.
     */
    JUNE,
    /**
     * The singleton instance for the month of July with 31 days.
     * This has the numeric value of {@code 7}.
     */
    JULY,
    /**
     * The singleton instance for the month of August with 31 days.
     * This has the numeric value of {@code 8}.
     */
    AUGUST,
    /**
     * The singleton instance for the month of September with 30 days.
     * This has the numeric value of {@code 9}.
     */
    SEPTEMBER,
    /**
     * The singleton instance for the month of October with 31 days.
     * This has the numeric value of {@code 10}.
     */
    OCTOBER,
    /**
     * The singleton instance for the month of November with 30 days.
     * This has the numeric value of {@code 11}.
     */
    NOVEMBER,
    /**
     * The singleton instance for the month of December with 31 days.
     * This has the numeric value of {@code 12}.
     */
    DECEMBER;
    /**
     * Private cache of all the constants.
     */
    private static final Month[] ENUMS = Month.values();

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code Month} from an {@code int} value.
     * <p>
     * {@code Month} is an enum representing the 12 months of the year.
     * This factory allows the enum to be obtained from the {@code int} value.
     * The {@code int} value follows the ISO-8601 standard, from 1 (January) to 12 (December).
     *
     * @param month  the month-of-year to represent, from 1 (January) to 12 (December)
     * @return the month-of-year, not null
     * @throws CalendricalException if the month-of-year is invalid
     */
    public static Month of(int month) {
        if (month < 1 || month > 12) {
            throw new CalendricalException("Invalid value for MonthOfYear: " + month);
        }
        return ENUMS[month - 1];
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code Month} from a calendrical.
     * <p>
     * A calendrical represents some form of date and time information.
     * This factory converts the arbitrary calendrical to an instance of {@code Month}.
     * 
     * @param calendrical  the calendrical to convert, not null
     * @return the month-of-year, not null
     * @throws CalendricalException if unable to convert to a {@code Month}
     */
    public static Month from(CalendricalObject calendrical) {
        if (calendrical instanceof Month) {
            return (Month) calendrical;
        }
        return of((int) MONTH_OF_YEAR.get(calendrical));
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the month-of-year {@code int} value.
     * <p>
     * The values are numbered following the ISO-8601 standard,
     * from 1 (January) to 12 (December).
     *
     * @return the month-of-year, from 1 (January) to 12 (December)
     */
    public int getValue() {
        return ordinal() + 1;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the textual representation, such as 'Jan' or 'December'.
     * <p>
     * This method is notionally specific to {@link ISOChronology} as it uses
     * the month-of-year rule to obtain the text. However, it is expected that
     * the text will be equivalent for all month-of-year rules, thus this aspect
     * of the implementation should be irrelevant to applications.
     * <p>
     * If no textual mapping is found then the {@link #getValue() numeric value} is returned.
     *
     * @param locale  the locale to use, not null
     * @return the short text value of the month-of-year, not null
     */
//    public String getText(TextStyle style, Locale locale) {
//        return MONTH_OF_YEAR.getText(getValue(), style, locale);
//    }

    //-----------------------------------------------------------------------
    /**
     * Gets the next month-of-year.
     * <p>
     * This calculates based on the time-line, thus it rolls around the end of
     * the year. The next month after December is January.
     *
     * @return the next month-of-year, not null
     */
    public Month next() {
        return roll(1);
    }

    /**
     * Gets the previous month-of-year.
     * <p>
     * This calculates based on the time-line, thus it rolls around the end of
     * the year. The previous month before January is December.
     *
     * @return the previous month-of-year, not null
     */
    public Month previous() {
        return roll(-1);
    }

    /**
     * Rolls the month-of-year, adding the specified number of months.
     * <p>
     * This calculates based on the time-line, thus it rolls around the end of
     * the year from December to January. The months to roll by may be negative.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param months  the months to roll by, positive or negative
     * @return the resulting month-of-year, not null
     */
    public Month roll(long months) {
        int amount = (int) (months % 12);
        return values()[(ordinal() + (amount + 12)) % 12];
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the length of this month in days.
     * <p>
     * This takes a flag to determine whether to return the length for a leap year or not.
     * <p>
     * February has 28 days in a standard year and 29 days in a leap year.
     * April, June, September and November have 30 days.
     * All other months have 31 days.
     *
     * @param leapYear  true if the length is required for a leap year
     * @return the length of this month in days, from 28 to 31
     */
    public int length(boolean leapYear) {
        switch (this) {
            case FEBRUARY:
                return (leapYear ? 29 : 28);
            case APRIL:
            case JUNE:
            case SEPTEMBER:
            case NOVEMBER:
                return 30;
            default:
                return 31;
        }
    }

    /**
     * Gets the minimum length of this month in days.
     * <p>
     * February has a minimum length of 28 days.
     * April, June, September and November have 30 days.
     * All other months have 31 days.
     *
     * @return the minimum length of this month in days, from 28 to 31
     */
    public int minLength() {
        switch (this) {
            case FEBRUARY:
                return 28;
            case APRIL:
            case JUNE:
            case SEPTEMBER:
            case NOVEMBER:
                return 30;
            default:
                return 31;
        }
    }

    /**
     * Gets the maximum length of this month in days.
     * <p>
     * February has a maximum length of 29 days.
     * April, June, September and November have 30 days.
     * All other months have 31 days.
     *
     * @return the maximum length of this month in days, from 29 to 31
     */
    public int maxLength() {
        switch (this) {
            case FEBRUARY:
                return 29;
            case APRIL:
            case JUNE:
            case SEPTEMBER:
            case NOVEMBER:
                return 30;
            default:
                return 31;
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the day-of-year for the first day of this month.
     * <p>
     * This returns the day-of-year that this month begins on, using the leap
     * year flag to determine the length of February.
     *
     * @param leapYear  true if the length is required for a leap year
     * @return the last day of this month, from 1 to 335
     */
    public int getMonthStartDayOfYear(boolean leapYear) {
        int leap = leapYear ? 1 : 0;
        switch (this) {
            case JANUARY:
                return 1;
            case FEBRUARY:
                return 32;
            case MARCH:
                return 60 + leap;
            case APRIL:
                return 91 + leap;
            case MAY:
                return 121 + leap;
            case JUNE:
                return 152 + leap;
            case JULY:
                return 182 + leap;
            case AUGUST:
                return 213 + leap;
            case SEPTEMBER:
                return 244 + leap;
            case OCTOBER:
                return 274 + leap;
            case NOVEMBER:
                return 305 + leap;
            case DECEMBER:
            default:
                return 335 + leap;
        }
    }

    /**
     * Gets the day-of-year for the last day of this month.
     * <p>
     * This returns the day-of-year that this month ends on, using the leap
     * year flag to determine the length of February.
     *
     * @param leapYear  true if the length is required for a leap year
     * @return the last day of this month, from 31 to 366
     */
    public int getMonthEndDayOfYear(boolean leapYear) {
        return getMonthStartDayOfYear(leapYear) + length(leapYear) - 1;
    }

    //-----------------------------------------------------------------------
    /**
     * Extracts date-time information in a generic way.
     * <p>
     * This method exists to fulfill the {@link CalendricalObject} interface.
     * This implementation returns the following types:
     * <ul>
     * <li>Month
     * <li>DateTimeBuilder, using {@link LocalDateTimeField#MONTH_OF_YEAR}
     * <li>Class, returning {@code Month}
     * </ul>
     * 
     * @param <R> the type to extract
     * @param type  the type to extract, null returns null
     * @return the extracted object, null if unable to extract
     */
    @SuppressWarnings("unchecked")
    @Override
    public <R> R extract(Class<R> type) {
        if (type == DateTimeBuilder.class) {
            return (R) new DateTimeBuilder(MONTH_OF_YEAR, getValue());
        } else if (type == Class.class) {
            return (R) Month.class;
        } else if (type == Month.class) {
            return (R) this;
        }
        return null;
    }

    @Override
    public Month with(CalendricalAdjuster adjuster) {
        if (adjuster instanceof Month) {
            return ((Month) adjuster);
        } else if (adjuster instanceof DateTimeAdjuster) {
            return (Month) ((DateTimeAdjuster) adjuster).makeAdjustmentTo(this);
        }
        DateTimes.checkNotNull(adjuster, "Adjuster must not be null");
        throw new CalendricalException("Unable to adjust Month with " + adjuster.getClass().getSimpleName());
    }

    /**
     * Implementation of the strategy to make an adjustment to the specified date-time object.
     * <p>
     * This method is not intended to be called by application code directly.
     * Applications should use the {@code with(DateTimeAdjuster)} method on the
     * date-time object to make the adjustment passing this as the argument.
     * 
     * <h4>Implementation notes</h4>
     * Adjusts the specified date-time to have the value of this month.
     * Other fields in the target object may be adjusted of necessary to ensure the date is valid.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param calendrical  the target object to be adjusted, not null
     * @return the adjusted object, not null
     */
    @Override
    public DateTimeObject makeAdjustmentTo(DateTimeObject calendrical) {
        return calendrical.with(MONTH_OF_YEAR, getValue());
    }

    //-----------------------------------------------------------------------
    @Override
    public long get(DateTimeField field) {
        if (field == MONTH_OF_YEAR) {
            return getValue();
        } else if (field instanceof LocalDateTimeField) {
            throw new CalendricalException(field.getName() + " not valid for Month");
        }
        return field.get(this);
    }

    @Override
    public Month with(DateTimeField field, long newValue) {
        if (field == MONTH_OF_YEAR) {
            ((LocalDateTimeField) field).checkValidValue(newValue);
            return Month.of((int) newValue);
        } else if (field instanceof LocalDateTimeField) {
            throw new CalendricalException(field.getName() + " not valid for Month");
        }
        return field.set(this, newValue);
    }

    @Override
    public Month plus(long period, PeriodUnit unit) {
        if (unit == MONTHS) {
            return roll(period % 12);
        } else if (unit == QUARTER_YEARS) {
            return roll((period % 4) * 3);
        } else if (unit == HALF_YEARS) {
            return roll((period % 2) * 6);
        } else if (unit instanceof LocalDateTimeUnit) {
            throw new CalendricalException(unit.getName() + " not valid for Month");
        }
        return unit.add(this, period);
    }

    @Override
    public Month minus(long period, PeriodUnit unit) {
        return plus(DateTimes.safeNegate(period), unit);
    }

}