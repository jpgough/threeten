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

import static javax.time.calendrical.LocalDateTimeField.DAY_OF_MONTH;
import static javax.time.calendrical.LocalDateTimeField.EPOCH_MONTH;
import static javax.time.calendrical.LocalDateTimeField.MONTH_OF_YEAR;
import static javax.time.calendrical.LocalDateTimeField.NANO_OF_DAY;
import static javax.time.calendrical.LocalDateTimeField.YEAR;
import static javax.time.calendrical.LocalPeriodUnit.DAYS;
import static javax.time.calendrical.LocalPeriodUnit.MONTHS;
import static javax.time.calendrical.LocalPeriodUnit.NANOS;
import static javax.time.calendrical.LocalPeriodUnit.YEARS;

import java.io.Serializable;

import javax.time.calendrical.AdjustableDateTime;
import javax.time.calendrical.DateTime;
import javax.time.calendrical.DateTimePlusMinusAdjuster;
import javax.time.calendrical.DateTimeValueRange;
import javax.time.calendrical.LocalDateTimeField;
import javax.time.calendrical.LocalPeriodUnit;
import javax.time.calendrical.PeriodUnit;
import javax.time.chrono.Chronology;
import javax.time.format.DateTimeParseException;

/**
 * An immutable period consisting of the most common units, such as '3 Months, 4 Days and 7 Hours'.
 * <p>
 * A period is a human-scale description of an amount of time.
 * The model is of a directed amount of time, meaning that the period may be negative.
 * <p>
 * This period supports the following units:
 * <ul>
 * <li>{@link LocalPeriodUnit#YEARS YEARS}</li>
 * <li>{@link LocalPeriodUnit#MONTHS MONTHS}</li>
 * <li>{@link LocalPeriodUnit#DAYS DAYS}</li>
 * <li>time units with an {@link PeriodUnit#isDurationEstimated() exact duration}</li>
 * </ul>
 * <p>
 * The period may be used with any calendar system with the exception is methods with an "ISO" suffix.
 * The meaning of a "year" or a "month" is only applied when the object is added to a date.
 * 
 * <h4>Implementation notes</h4>
 * This class is immutable and thread-safe.
 * The maximum number of hours that can be stored is about 2.5 million, limited by storing
 * a single {@code long} nanoseconds for all time units internally.
 */
public final class Period
        implements DateTimePlusMinusAdjuster, Serializable {
    // maximum hours is 2,562,047

    /**
     * A constant for a period of zero.
     */
    public static final Period ZERO = new Period(0, 0, 0, 0);
    /**
     * Serialization version.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The number of years.
     */
    private final int years;
    /**
     * The number of months.
     */
    private final int months;
    /**
     * The number of days.
     */
    private final int days;
    /**
     * The number of nanoseconds.
     */
    private final long nanos;

    //-----------------------------------------------------------------------
    /**
     * Obtains a {@code Period} from date-based and time-based fields.
     * <p>
     * This creates an instance based on years, months, days, hours, minutes and seconds.
     * Within a period, the time fields are always normalized.
     *
     * @param years  the amount of years, may be negative
     * @param months  the amount of months, may be negative
     * @param days  the amount of days, may be negative
     * @param hours  the amount of hours, may be negative
     * @param minutes  the amount of minutes, may be negative
     * @param seconds  the amount of seconds, may be negative
     * @return the period, not null
     */
    public static Period of(int years, int months, int days, int hours, int minutes, int seconds) {
        return of(years, months, days, hours, minutes, seconds, 0);
    }

    /**
     * Obtains a {@code Period} from date-based and time-based fields.
     * <p>
     * This creates an instance based on years, months, days, hours, minutes, seconds and nanoseconds.
     * Within a period, the time fields are always normalized.
     *
     * @param years  the amount of years, may be negative
     * @param months  the amount of months, may be negative
     * @param days  the amount of days, may be negative
     * @param hours  the amount of hours, may be negative
     * @param minutes  the amount of minutes, may be negative
     * @param seconds  the amount of seconds, may be negative
     * @param nanos  the amount of nanos, may be negative
     * @return the period, not null
     */
    public static Period of(int years, int months, int days, int hours, int minutes, int seconds, long nanos) {
        if ((years | months | days | hours | minutes | seconds | nanos) == 0) {
            return ZERO;
        }
        long totSecs = DateTimes.safeAdd(hours * 3600L, minutes * 60L) + seconds;
        long totNanos = DateTimes.safeAdd(DateTimes.safeMultiply(totSecs, 1_000_000_000L), nanos);
        return create(years, months, days, totNanos);
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains a {@code Period} from date-based fields.
     * <p>
     * This creates an instance based on years, months and days.
     *
     * @param years  the amount of years, may be negative
     * @param months  the amount of months, may be negative
     * @param days  the amount of days, may be negative
     * @return the period, not null
     */
    public static Period ofDate(int years, int months, int days) {
        return of(years, months, days, 0, 0, 0, 0);
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains a {@code Period} from time-based fields.
     * <p>
     * This creates an instance based on hours, minutes and seconds.
     * Within a period, the time fields are always normalized.
     *
     * @param hours  the amount of hours, may be negative
     * @param minutes  the amount of minutes, may be negative
     * @param seconds  the amount of seconds, may be negative
     * @return the period, not null
     */
    public static Period ofTime(int hours, int minutes, int seconds) {
        return of(0, 0, 0, hours, minutes, seconds, 0);
    }

    /**
     * Obtains a {@code Period} from time-based fields.
     * <p>
     * This creates an instance based on hours, minutes, seconds and nanoseconds.
     * Within a period, the time fields are always normalized.
     *
     * @param hours  the amount of hours, may be negative
     * @param minutes  the amount of minutes, may be negative
     * @param seconds  the amount of seconds, may be negative
     * @param nanos  the amount of nanos, may be negative
     * @return the period, not null
     */
    public static Period ofTime(int hours, int minutes, int seconds, long nanos) {
        return of(0, 0, 0, hours, minutes, seconds, nanos);
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code Period} from a period in the specified unit.
     * <p>
     * The parameters represent the two parts of a phrase like '6 Days'. For example:
     * <pre>
     *  Period.of(3, SECONDS);
     *  Period.of(5, YEARS);
     * </pre>
     * The specified unit must be one of the supported units from {@link LocalPeriodUnit},
     * {@code YEARS}, {@code MONTHS} or {@code DAYS} or be a time unit with an
     * {@link PeriodUnit#isDurationEstimated() exact duration}.
     * Other units throw an exception.
     *
     * @param amount  the amount of the period, measured in terms of the unit, positive or negative
     * @param unit  the unit that the period is measured in, must have an exact duration, not null
     * @return the period, not null
     * @throws DateTimeException if the period unit is invalid
     * @throws ArithmeticException if a numeric overflow occurs
     */
    public static Period of(long amount, PeriodUnit unit) {
        return ZERO.plus(amount, unit);
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains a {@code Period} from a {@code Duration}.
     * <p>
     * This converts the duration to a period.
     * Within a period, the time fields are always normalized.
     * The years, months and days fields will be zero.
     * <p>
     * To populate the days field, call {@link #normalizeHoursToDays()} on the created period.
     *
     * @param duration  the duration to convert, not null
     * @return the period, not null
     * @throws ArithmeticException if numeric overflow occurs
     */
    public static Period of(Duration duration) {
        DateTimes.checkNotNull(duration, "Duration must not be null");
        if (duration.isZero()) {
            return ZERO;
        }
        return new Period(0, 0, 0, duration.toNanos());
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a {@code Period} consisting of the number of years, months, days,
     * hours, minutes, seconds, and nanoseconds between two {@code DateTime} instances.
     * <p>
     * The start date is included, but the end date is not. Only whole years count.
     * For example, from {@code 2010-01-15} to {@code 2011-03-18} is one year, two months and three days.
     * <p>
     * This method examines the {@link LocalDateTimeField fields} {@code YEAR}, {@code MONTH_OF_YEAR},
     * {@code DAY_OF_MONTH} and {@code NANO_OF_DAY}
     * The difference between each of the fields is calculated independently from the others.
     * At least one of the four fields must be present.
     * <p>
     * The four units are typically retained without normalization.
     * However, years and months are normalized if the range of months is fixed, as it is with ISO.
     * <p>
     * The result of this method can be a negative period if the end is before the start.
     * The negative sign can be different in each of the four major units.
     *
     * @param start  the start date, inclusive, not null
     * @param end  the end date, exclusive, not null
     * @return the period between the date-times, not null
     * @throws DateTimeException if the two date-times do have similar available fields
     * @throws ArithmeticException if numeric overflow occurs
     */
    public static Period between(DateTime start, DateTime end) {
        if (Chronology.from(start).equals(Chronology.from(end)) == false) {
            throw new DateTimeException("Unable to calculate period as date-times have different chronologies");
        }
        int years = 0;
        int months = 0;
        int days = 0;
        long nanos = 0;
        boolean valid = false;
        if (DateTimes.isSupported(start, YEAR)) {
            years = DateTimes.safeToInt(DateTimes.safeSubtract(end.get(YEAR), start.get(YEAR)));
            valid = true;
        }
        if (DateTimes.isSupported(start, MONTH_OF_YEAR)) {
            months = DateTimes.safeToInt(DateTimes.safeSubtract(end.get(MONTH_OF_YEAR), start.get(MONTH_OF_YEAR)));
            DateTimeValueRange startRange = Chronology.from(start).range(MONTH_OF_YEAR);
            DateTimeValueRange endRange = Chronology.from(end).range(MONTH_OF_YEAR);
            if (startRange.isFixed() && startRange.isIntValue() && startRange.equals(endRange)) {
                int monthCount = (int) (startRange.getMaximum() - startRange.getMinimum() + 1);
                long totMonths = ((long) months) + years * monthCount;
                months = (int) (totMonths % monthCount);
                years = DateTimes.safeToInt(totMonths / monthCount);
            }
            valid = true;
        }
        if (DateTimes.isSupported(start, DAY_OF_MONTH)) {
            days = DateTimes.safeToInt(DateTimes.safeSubtract(end.get(DAY_OF_MONTH), start.get(DAY_OF_MONTH)));
            valid = true;
        }
        if (DateTimes.isSupported(start, NANO_OF_DAY)) {
            nanos = DateTimes.safeSubtract(end.get(NANO_OF_DAY), start.get(NANO_OF_DAY));
            valid = true;
        }
        if (valid == false) {
            throw new DateTimeException("Unable to calculate period as date-times do not have any valid fields");
        }
        return create(years, months, days, nanos);
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains a {@code Period} consisting of the number of years, months,
     * and days between two dates.
     * <p>
     * The start date is included, but the end date is not. Only whole years count.
     * For example, from {@code 2010-01-15} to {@code 2011-03-18} is one year, two months and three days.
     * <p>
     * The result of this method can be a negative period if the end is before the start.
     * The negative sign will be the same in each of year, month and day.
     *
     * @param startDate  the start date, inclusive, not null
     * @param endDate  the end date, exclusive, not null
     * @return the period between the dates, not null
     * @throws ArithmeticException if numeric overflow occurs
     */
    public static Period betweenISO(LocalDate startDate, LocalDate endDate) {
        long startMonth = startDate.get(EPOCH_MONTH);
        long endMonth = endDate.get(EPOCH_MONTH);
        long totalMonths = endMonth - startMonth;  // safe
        int days = endDate.getDayOfMonth() - startDate.getDayOfMonth();
        if (totalMonths > 0 && days < 0) {
            totalMonths--;
            LocalDate calcDate = startDate.plusMonths(totalMonths);
            days = (int) (endDate.toEpochDay() - calcDate.toEpochDay());  // safe
        } else if (totalMonths < 0 && days > 0) {
            totalMonths++;
            days -= endDate.lengthOfMonth();
        }
        long years = totalMonths / 12;  // safe
        int months = (int) (totalMonths % 12);  // safe
        return ofDate(DateTimes.safeToInt(years), months, days);
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains a {@code Period} consisting of the number of hours, minutes,
     * seconds and nanoseconds between two times.
     * <p>
     * The start time is included, but the end time is not.
     * For example, from {@code 13:45:00} to {@code 14:50:30.123456789}
     * is {@code P1H5M30.123456789S}.
     * <p>
     * The result of this method can be a negative period if the end is before the start.
     *
     * @param startTime  the start time, inclusive, not null
     * @param endTime  the end time, exclusive, not null
     * @return the period between the times, not null
     * @throws ArithmeticException if numeric overflow occurs
     */
    public static Period betweenISO(LocalTime startTime, LocalTime endTime) {
        return create(0, 0, 0, endTime.toNanoOfDay() - startTime.toNanoOfDay());
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains a {@code Period} from a text string such as {@code PnYnMnDTnHnMn.nS}.
     * <p>
     * This will parse the string produced by {@code toString()} which is
     * a subset of the ISO-8601 period format {@code PnYnMnDTnHnMn.nS}.
     * <p>
     * The string consists of a series of numbers with a suffix identifying their meaning.
     * The values, and suffixes, must be in the sequence year, month, day, hour, minute, second.
     * Any of the number/suffix pairs may be omitted providing at least one is present.
     * If the period is zero, the value is normally represented as {@code PT0S}.
     * The numbers must consist of ASCII digits.
     * Any of the numbers may be negative. Negative zero is not accepted.
     * The number of nanoseconds is expressed as an optional fraction of the seconds.
     * There must be at least one digit before any decimal point.
     * There must be between 1 and 9 inclusive digits after any decimal point.
     * The letters will all be accepted in upper or lower case.
     * The decimal point may be either a dot or a comma.
     *
     * @param text  the text to parse, not null
     * @return the parsed period, not null
     * @throws DateTimeParseException if the text cannot be parsed to a period
     */
    public static Period parse(final CharSequence text) {
        DateTimes.checkNotNull(text, "Text to parse must not be null");
        return new PeriodParser(text).parse();
    }

    //-----------------------------------------------------------------------
    /**
     * Creates an instance.
     *
     * @param years  the amount
     * @param months  the amount
     * @param days  the amount
     * @param nanos  the amount
     */
    private static Period create(int years, int months, int days, long nanos) {
        if ((years | months | days | nanos) == 0) {
            return ZERO;
        }
        return new Period(years, months, days, nanos);
    }

    /**
     * Constructor.
     *
     * @param years  the amount
     * @param months  the amount
     * @param days  the amount
     * @param nanos  the amount
     */
    private Period(int years, int months, int days, long nanos) {
        this.years = years;
        this.months = months;
        this.days = days;
        this.nanos = nanos;
    }

    /**
     * Resolves singletons.
     *
     * @return the resolved instance
     */
    private Object readResolve() {
        if ((years | months | days | nanos) == 0) {
            return ZERO;
        }
        return this;
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if this period is zero-length.
     *
     * @return true if this period is zero-length
     */
    public boolean isZero() {
        return (this == ZERO);
    }

    /**
     * Checks if this period is fully positive, excluding zero.
     * <p>
     * This checks whether all the amounts in the period are positive,
     * defined as greater than zero.
     *
     * @return true if this period is fully positive excluding zero
     */
    public boolean isPositive() {
        return ((years | months | days | nanos) > 0);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the amount of years of this period.
     *
     * @return the amount of years of this period
     */
    public int getYears() {
        return years;
    }

    /**
     * Gets the amount of months of this period.
     *
     * @return the amount of months of this period
     */
    public int getMonths() {
        return months;
    }

    /**
     * Gets the amount of days of this period.
     *
     * @return the amount of days of this period
     */
    public int getDays() {
        return days;
    }

    /**
     * Gets the amount of hours of this period.
     * <p>
     * Within a period, the time fields are always normalized.
     *
     * @return the amount of hours of this period
     */
    public int getHours() {
        return (int) (nanos / DateTimes.NANOS_PER_HOUR);
    }

    /**
     * Gets the amount of minutes within an hour of this period.
     * <p>
     * Within a period, the time fields are always normalized.
     *
     * @return the amount of minutes within an hour of this period
     */
    public int getMinutes() {
        return (int) ((nanos / DateTimes.NANOS_PER_MINUTE) % 60);
    }

    /**
     * Gets the amount of seconds within a minute of this period.
     * <p>
     * Within a period, the time fields are always normalized.
     *
     * @return the amount of seconds within a minute of this period
     */
    public int getSeconds() {
        return (int) ((nanos / DateTimes.NANOS_PER_SECOND) % 60);
    }

    /**
     * Gets the amount of nanoseconds within a second of this period.
     * <p>
     * Within a period, the time fields are always normalized.
     *
     * @return the amount of nanoseconds within a second of this period
     */
    public int getNanos() {
        return (int) (nanos % DateTimes.NANOS_PER_SECOND);  // safe from overflow
    }

    /**
     * Gets the total amount of the time units of this period, measured in nanoseconds.
     * <p>
     * Within a period, the time fields are always normalized.
     *
     * @return the total amount of time unit nanoseconds of this period
     */
    public long getTimeNanos() {
        return nanos;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this period with the specified amount of years.
     * <p>
     * This method will only affect the years field.
     * All other units are unaffected.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param years  the years to represent
     * @return a {@code Period} based on this period with the requested years, not null
     */
    public Period withYears(int years) {
        if (years == this.years) {
            return this;
        }
        return create(years, months, days, nanos);
    }

    /**
     * Returns a copy of this period with the specified amount of months.
     * <p>
     * This method will only affect the months field.
     * All other units are unaffected.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param months  the months to represent
     * @return a {@code Period} based on this period with the requested months, not null
     */
    public Period withMonths(int months) {
        if (months == this.months) {
            return this;
        }
        return create(years, months, days, nanos);
    }

    /**
     * Returns a copy of this period with the specified amount of days.
     * <p>
     * This method will only affect the days field.
     * All other units are unaffected.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param days  the days to represent
     * @return a {@code Period} based on this period with the requested days, not null
     */
    public Period withDays(int days) {
        if (days == this.days) {
            return this;
        }
        return create(years, months, days, nanos);
    }

    /**
     * Returns a copy of this period with the specified total amount of time units
     * expressed in nanoseconds.
     * <p>
     * Within a period, the time fields are always normalized.
     * This method will affect all the time units - hours, minutes, seconds and nanos.
     * The date units are unaffected.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param nanos  the nanoseconds to represent
     * @return a {@code Period} based on this period with the requested nanoseconds, not null
     */
    public Period withTimeNanos(long nanos) {
        if (nanos == this.nanos) {
            return this;
        }
        return create(years, months, days, nanos);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this period with the specified period added.
     * <p>
     * This operates separately on the years, months, days and the normalized time.
     * There is no further normalization beyond the normalized time.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param other  the period to add, not null
     * @return a {@code Period} based on this period with the requested period added, not null
     * @throws ArithmeticException if numeric overflow occurs
     */
    public Period plus(Period other) {
        return create(
                DateTimes.safeAdd(years, other.years),
                DateTimes.safeAdd(months, other.months),
                DateTimes.safeAdd(days, other.days),
                DateTimes.safeAdd(nanos, other.nanos));
    }

    /**
     * Returns a copy of this period with the specified period added.
     * <p>
     * The specified unit must be one of the supported units from {@link LocalPeriodUnit},
     * {@code YEARS}, {@code MONTHS} or {@code DAYS} or be a time unit with an
     * {@link PeriodUnit#isDurationEstimated() exact duration}.
     * Other units throw an exception.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param amount  the amount to add, positive or negative
     * @param unit  the unit that the amount is expressed in, not null
     * @return a {@code Period} based on this period with the requested amount added, not null
     * @throws ArithmeticException if numeric overflow occurs
     */
    public Period plus(long amount, PeriodUnit unit) {
        DateTimes.checkNotNull(unit, "PeriodUnit must not be null");
        if (unit instanceof LocalPeriodUnit) {
            if (unit == YEARS || unit == MONTHS || unit == DAYS || unit.isDurationEstimated() == false) {
                if (amount == 0) {
                    return this;
                }
                switch((LocalPeriodUnit) unit) {
                    case NANOS: return plusNanos(amount);
                    case MICROS: return plusNanos(DateTimes.safeMultiply(amount, 1000L));
                    case MILLIS: return plusNanos(DateTimes.safeMultiply(amount, 1000_000L));
                    case SECONDS: return plusSeconds(amount);
                    case MINUTES: return plusMinutes(amount);
                    case HOURS: return plusHours(amount);
                    case HALF_DAYS: return plusNanos(DateTimes.safeMultiply(amount, 12 * DateTimes.NANOS_PER_HOUR));
                    case DAYS: return plusDays(amount);
                    case MONTHS: return plusMonths(amount);
                    case YEARS: return plusYears(amount);
                    default: throw new DateTimeException("Unsupported unit: " + unit.getName());
                }
            }
        }
        if (unit.isDurationEstimated()) {
            throw new DateTimeException("Unsupported unit: " + unit.getName());
        }
        return plusNanos(Duration.of(amount, unit).toNanos());
    }

    public Period plusYears(long amount) {
        return create(DateTimes.safeToInt(DateTimes.safeAdd(years, amount)), months, days, nanos);
    }

    public Period plusMonths(long amount) {
        return create(years, DateTimes.safeToInt(DateTimes.safeAdd(months, amount)), days, nanos);
    }

    public Period plusDays(long amount) {
        return create(years, months, DateTimes.safeToInt(DateTimes.safeAdd(days, amount)), nanos);
    }

    public Period plusHours(long amount) {
        return plusNanos(DateTimes.safeMultiply(amount, DateTimes.NANOS_PER_HOUR));
    }

    public Period plusMinutes(long amount) {
        return plusNanos(DateTimes.safeMultiply(amount, DateTimes.NANOS_PER_MINUTE));
    }

    public Period plusSeconds(long amount) {
        return plusNanos(DateTimes.safeMultiply(amount, DateTimes.NANOS_PER_SECOND));
    }

    public Period plusNanos(long amount) {
        return create(years, months, days, DateTimes.safeAdd(nanos,  amount));
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this period with the specified period subtracted.
     * <p>
     * This operates separately on the years, months, days and the normalized time.
     * There is no further normalization beyond the normalized time.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param other  the period to subtract, not null
     * @return a {@code Period} based on this period with the requested period subtracted, not null
     * @throws ArithmeticException if numeric overflow occurs
     */
    public Period minus(Period other) {
        return create(
                DateTimes.safeSubtract(years, other.years),
                DateTimes.safeSubtract(months, other.months),
                DateTimes.safeSubtract(days, other.days),
                DateTimes.safeSubtract(nanos, other.nanos));
    }

    /**
     * Returns a copy of this period with the specified period subtracted.
     * <p>
     * The specified unit must be one of the supported units from {@link LocalPeriodUnit},
     * {@code YEARS}, {@code MONTHS} or {@code DAYS} or be a time unit with an
     * {@link PeriodUnit#isDurationEstimated() exact duration}.
     * Other units throw an exception.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param amount  the amount to subtract, positive or negative
     * @param unit  the unit that the amount is expressed in, not null
     * @return a {@code Period} based on this period with the requested amount subtracted, not null
     * @throws ArithmeticException if numeric overflow occurs
     */
    public Period minus(long amount, PeriodUnit unit) {
        return (amount == Long.MIN_VALUE ? plus(Long.MAX_VALUE, unit).plus(1, unit) : plus(-amount, unit));
    }

    public Period minusYears(long amount) {
        return (amount == Long.MIN_VALUE ? plusYears(Long.MAX_VALUE).plusYears(1) : plusYears(-amount));
    }

    public Period minusMonths(long amount) {
        return (amount == Long.MIN_VALUE ? plusMonths(Long.MAX_VALUE).plusMonths(1) : plusMonths(-amount));
    }

    public Period minusDays(long amount) {
        return (amount == Long.MIN_VALUE ? plusDays(Long.MAX_VALUE).plusDays(1) : plusDays(-amount));
    }

    public Period minusHours(long amount) {
        return (amount == Long.MIN_VALUE ? plusHours(Long.MAX_VALUE).plusHours(1) : plusHours(-amount));
    }

    public Period minusMinutes(long amount) {
        return (amount == Long.MIN_VALUE ? plusMinutes(Long.MAX_VALUE).plusMinutes(1) : plusMinutes(-amount));
    }

    public Period minusSeconds(long amount) {
        return (amount == Long.MIN_VALUE ? plusSeconds(Long.MAX_VALUE).plusSeconds(1) : plusSeconds(-amount));
    }

    public Period minusNanos(long amount) {
        return (amount == Long.MIN_VALUE ? plusNanos(Long.MAX_VALUE).plusNanos(1) : plusNanos(-amount));
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a new instance with each element in this period multiplied
     * by the specified scalar.
     * <p>
     * This simply multiplies each field, years, months, days and normalized time,
     * by the scalar. No normalization is performed.
     *
     * @param scalar  the scalar to multiply by, not null
     * @return a {@code Period} based on this period with the amounts multiplied by the scalar, not null
     * @throws ArithmeticException if numeric overflow occurs
     */
    public Period multipliedBy(int scalar) {
        if (this == ZERO || scalar == 1) {
            return this;
        }
        return create(
                DateTimes.safeMultiply(years, scalar),
                DateTimes.safeMultiply(months, scalar),
                DateTimes.safeMultiply(days, scalar),
                DateTimes.safeMultiply(nanos, scalar));
    }

    /**
     * Returns a new instance with each amount in this period negated.
     *
     * @return a {@code Period} based on this period with the amounts negated, not null
     * @throws ArithmeticException if numeric overflow occurs
     */
    public Period negated() {
        return multipliedBy(-1);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this period with any amount in excess of 24 hours
     * normalized to a number of days.
     * <p>
     * The hours unit is decreased to have an absolute value less than 24,
     * with the days unit being increased to compensate. Other units are unaffected.
     * For example, a period of {@code P2DT28H} will be normalized to {@code P3DT4H}.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @return a {@code Period} based on this period with excess hours normalized to days, not null
     * @throws ArithmeticException if numeric overflow occurs
     */
    public Period normalizeHoursToDays() {
        int splitDays = (int) (nanos / DateTimes.NANOS_PER_DAY);  // safe from overflow
        if (splitDays == 0) {
            return this;
        }
        long remNanos = nanos % DateTimes.NANOS_PER_DAY;
        return create(years, months, DateTimes.safeAdd(days, splitDays), remNanos);
    }

    /**
     * Returns a copy of this period with any days converted to hours using a 24 hour day.
     * <p>
     * The days unit is reduced to zero, with the hours unit increased by 24 times the
     * days unit to compensate. Other units are unaffected.
     * For example, a period of {@code P2DT4H} will be normalized to {@code PT52H}.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @return a {@code Period} based on this period with days normalized to hours, not null
     * @throws ArithmeticException if numeric overflow occurs
     */
    public Period normalizeDaysToHours() {
        if (days == 0) {
            return this;
        }
        return create(years, months, 0, DateTimes.safeAdd(DateTimes.safeMultiply(days, DateTimes.NANOS_PER_DAY), nanos));
    }

    /**
     * Returns a copy of this period with the years and months normalized using a 12 month year.
     * <p>
     * The months unit is decreased to have an absolute value less than 11,
     * with the years unit being increased to compensate. Other units are unaffected.
     * As this normalization uses a 12 month year it is not valid for all calendar systems.
     * For example, a period of {@code P1Y15M} will be normalized to {@code P2Y3M}.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @return a {@code Period} based on this period with years and months normalized, not null
     * @throws ArithmeticException if numeric overflow occurs
     */
    public Period normalizeMonthsISO() {
        int splitYears = months / 12;
        if (splitYears == 0) {
            return this;
        }
        int remMonths = months % 12;
        return create(DateTimes.safeAdd(years, splitYears), remMonths, days, nanos);
    }

    //-------------------------------------------------------------------------
    /**
     * Converts this period to one that only has date units.
     * <p>
     * The resulting period will have the same years, months and days as this period
     * but the time units will all be zero. No normalization occurs in the calculation.
     * For example, a period of {@code P1Y3MT12H} will be converted to {@code P1Y3M}.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @return a {@code Period} based on this period with the time units set to zero, not null
     */
    public Period toDateOnly() {
        if (nanos == 0) {
            return this;
        }
        return create(years, months, days, 0);
    }

    //-------------------------------------------------------------------------
    /**
     * Adds this period to the specified date-time object.
     * <p>
     * This method is not intended to be called by application code directly.
     * Applications should use the {@code plus(DateTimePlusMinusAdjuster)} method
     * on the date-time object passing this period as the argument.
     * 
     * @param dateTime  the date-time object to adjust, not null
     * @return an object of the same type with the adjustment made, not null
     * @throws DateTimeException if unable to add
     * @throws ArithmeticException if numeric overflow occurs
     */
    @Override
    public AdjustableDateTime doAdd(AdjustableDateTime dateTime) {
        // TODO: better algorithm around fixed months in years
        if (years != 0) {
            dateTime = dateTime.plus(years, YEARS);
        }
        if (months != 0) {
            dateTime = dateTime.plus(months, MONTHS);
        }
        if (days != 0) {
            dateTime = dateTime.plus(days, DAYS);
        }
        if (nanos != 0) {
            dateTime = dateTime.plus(nanos, NANOS);
        }
        return dateTime;
    }

    /**
     * Subtracts this period from the specified date-time object.
     * <p>
     * This method is not intended to be called by application code directly.
     * Applications should use the {@code minus(DateTimePlusMinusAdjuster)} method
     * on the date-time object passing this period as the argument.
     * 
     * @param dateTime  the date-time object to adjust, not null
     * @return an object of the same type with the adjustment made, not null
     * @throws DateTimeException if unable to subtract
     * @throws ArithmeticException if numeric overflow occurs
     */
    @Override
    public AdjustableDateTime doSubtract(AdjustableDateTime dateTime) {
        if (years != 0) {
            dateTime = dateTime.minus(years, YEARS);
        }
        if (months != 0) {
            dateTime = dateTime.minus(months, MONTHS);
        }
        if (days != 0) {
            dateTime = dateTime.minus(days, DAYS);
        }
        if (nanos != 0) {
            dateTime = dateTime.minus(nanos, NANOS);
        }
        return dateTime;
    }

    //-----------------------------------------------------------------------
    /**
     * Converts this period to one that only has time units.
     * <p>
     * The resulting period will have the same time units as this period
     * but the date units will all be zero. No normalization occurs in the calculation.
     * For example, a period of {@code P1Y3MT12H} will be converted to {@code PT12H}.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @return a {@code Period} based on this period with the date units set to zero, not null
     */
    public Period toTimeOnly() {
        if ((years | months | days) == 0) {
            return this;
        }
        return create(0, 0, 0, nanos);
    }

    //-------------------------------------------------------------------------
    /**
     * Calculates the duration of this period.
     * <p>
     * The calculation uses the hours, minutes, seconds and nanoseconds fields.
     * If years, months or days are present an exception is thrown.
     * See {@link #toTimeOnly()} for a way to remove the date units and
     * {@link #normalizeDaysToHours()} for a way to convert days to hours.
     *
     * @return a {@code Duration} equivalent to this period, not null
     * @throws DateTimeException if the period cannot be converted as it contains years, months or days
     */
    public Duration toDuration() {
        if ((years | months | days) != 0) {
            throw new DateTimeException("Unable to convert period to duration as years/months/days are present: " + this);
        }
        return Duration.ofNanos(nanos);
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if this period is equal to another period.
     * <p>
     * The comparison is based on the amounts held in the period.
     * To be equal, the years, months, days and normalized time fields must be equal.
     *
     * @param obj  the object to check, null returns false
     * @return true if this is equal to the other period
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof Period) {
            Period other = (Period) obj;
            return years == other.years && months == other.months &&
                    days == other.days && nanos == other.nanos;
        }
        return false;
    }

    /**
     * A hash code for this period.
     *
     * @return a suitable hash code
     */
    @Override
    public int hashCode() {
        // ordered such that overflow from one field doesn't immediately affect the next field
        return ((years << 27) | (years >>> 5)) ^
                ((days << 21) | (days >>> 11)) ^
                ((months << 17) | (months >>> 15)) ^
                ((int) (nanos ^ (nanos >>> 32)));
    }

    //-----------------------------------------------------------------------
    /**
     * Outputs this period as a {@code String}, such as {@code P6Y3M1DT12H}.
     * <p>
     * The output will be in the ISO-8601 period format.
     *
     * @return a string representation of this period, not null
     */
    @Override
    public String toString() {
        // TODO: toString doesn't match state nanos/secs
        if (this == ZERO) {
            return "PT0S";
        } else {
            StringBuilder buf = new StringBuilder();
            buf.append('P');
            if (years != 0) {
                buf.append(years).append('Y');
            }
            if (months != 0) {
                buf.append(months).append('M');
            }
            if (days != 0) {
                buf.append(days).append('D');
            }
            if (nanos != 0) {
                buf.append('T');
                long hours = (nanos / DateTimes.NANOS_PER_HOUR);
                long minutes = (nanos / DateTimes.NANOS_PER_MINUTE) % 60;
                long secondNanos = (nanos % DateTimes.NANOS_PER_MINUTE);
                if (hours != 0) {
                    buf.append(hours).append('H');
                }
                if (minutes != 0) {
                    buf.append(minutes).append('M');
                }
                if (secondNanos != 0) {
                    long secondPart = (secondNanos / DateTimes.NANOS_PER_SECOND);
                    long nanoPart = (secondNanos % DateTimes.NANOS_PER_SECOND);
                    if (nanoPart == 0) {
                        buf.append(secondPart).append('S');
                    } else {
                        if (secondNanos < 0) {
                            secondPart = -secondPart;
                            nanoPart = -nanoPart;
                            buf.append('-');
                        }
                        buf.append(secondPart);
                        int dotPos = buf.length();
                        nanoPart += 1000_000_000;
                        while (nanoPart % 10 == 0) {
                            nanoPart /= 10;
                        }
                        buf.append(nanoPart);
                        buf.setCharAt(dotPos, '.');
                        buf.append('S');
                    }
                }
            }
            return buf.toString();
        }
    }

}
