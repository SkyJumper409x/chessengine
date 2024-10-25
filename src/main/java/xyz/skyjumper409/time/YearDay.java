package xyz.skyjumper409.time;

import java.io.Serializable;

import java.time.temporal.Temporal;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalUnit;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;


public class YearDay implements Temporal, Comparable<YearDay>, Serializable {
    private final int year;
    private final int day;
    private YearDay() {this(0,0);}
    private YearDay(int year, int day) {
        this.year=year;
        this.day=day;
    };
    public static YearDay of(int year, int day) {
        return new YearDay(year,day);
    }

    @Override
    public int compareTo(YearDay arg0) {
        if(year - arg0.year == 0) {
            return day - arg0.day;
        }
        return year-arg0.year;
    }

    @Override
    public long getLong(TemporalField arg0) {
        return get(arg0);
    }

    @Override
    public boolean isSupported(TemporalField arg0) {
        return arg0 == ChronoField.DAY_OF_MONTH || arg0 == ChronoField.YEAR;
    }

    @Override
    public boolean isSupported(TemporalUnit arg0) {
        return arg0 == ChronoUnit.DAYS || arg0 == ChronoUnit.YEARS;
    }

    @Override
    public Temporal plus(long arg0, TemporalUnit arg1) {
        int newDay = day;
        int newYear = year;
        if (arg1 == ChronoUnit.DAYS)  {
            newDay += arg0;
        } else if (arg1 == ChronoUnit.YEARS)  {
            newYear += arg0;
        }
        return new YearDay(newDay, newYear);
    }

    @Override
    public long until(Temporal arg0, TemporalUnit arg1) {
        return arg1.between(this, arg0);
    }

    @Override
    public Temporal with(TemporalField arg0, long arg1) {
        return arg0.adjustInto(this, arg1);
    }
    
}
