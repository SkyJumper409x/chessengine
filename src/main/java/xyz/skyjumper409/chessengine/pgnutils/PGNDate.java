package xyz.skyjumper409.chessengine.pgnutils;

import java.time.temporal.TemporalAccessor;
import java.time.temporal.ChronoField;

public class PGNDate {
    private TemporalAccessor value;
    private int singularValue;
    private ChronoField singularField;

    public TemporalAccessor getValue() {
        return value;
    }
    public ChronoField getSingularField() {
        return singularField;
    }
    public int getSingularValue() {
        return singularValue;
    }

    public boolean hasRegularValue() {
        return value != null;
    }
    public boolean hasSingularValue() {
        return singularField != null;
    }
    public boolean isUnknown() {
        return !(hasRegularValue() || hasSingularValue());
    }
    static PGNDate parse(String s) { // so this isnt in the PGNValueType enum
        String[] tokens = s.split("\\.");
        if(tokens.length != 3) {
            System.out.printf("Encountered Invalid Date String in %s.parse(String s) (s=\"%s\")%n", PGNDate.class.getCanonicalName(), s);
            return null;
        }
        // pretend the following code doesnt exist (it is terrible) 
        boolean hasYear = true, hasMonth = true, hasDay = true;
        byte valid = 0;
        int[] ints = new int[3];
        if(tokens[0].contains("?")) {
            hasYear = false;
        } else {
            valid |= 0b01;
            ints[0] = Integer.parseInt(tokens[0]);
        }
        if(tokens[1].contains("?")) {
            hasMonth = false;
        } else {
            valid |= 0b010;
            ints[1] = Integer.parseInt(tokens[1]);
        }
        if(tokens[2].contains("?")) {
            hasDay = false;
        } else {
            valid |= 0b0100;
            ints[2] = Integer.parseInt(tokens[2]);
        }

        if(!(hasYear || hasMonth || hasDay)) {
            return new PGNDate();
        }
        PGNDate result = new PGNDate();
        if(hasDay && hasMonth && hasYear) {
            result.value = java.time.LocalDate.of(ints[0],ints[1],ints[2]);
        } else if(hasYear ^ hasMonth ^ hasDay) {
            int idx = (int)(Math.log(valid)/Math.log(2)); // log10(x)/log10(2) = log2(x)
            assert(((double) idx) == (Math.log(valid)/Math.log(2))); // to ensure only one of the bits is set
            result.singularValue = ints[idx];
            result.singularField = new ChronoField[]{ChronoField.YEAR, ChronoField.MONTH_OF_YEAR, ChronoField.DAY_OF_MONTH}[idx];
        } else if(hasYear) {
            if(hasMonth) {
                result.value = java.time.YearMonth.of(ints[0],ints[1]);
            } else if(hasDay) {
                result.value = xyz.skyjumper409.time.YearDay.of(ints[0],ints[2]);
            } else {
                System.out.printf("%s.parse(String s): This code should be unreachable, something's really wrong lmfao (s=\"%s\")%n", PGNDate.class.getCanonicalName(), s);
                System.exit(1);
            }
        } else if(hasMonth) {
            if(hasDay) { 
                result.value = java.time.MonthDay.of(ints[1],ints[2]);
            } else {
                System.out.printf("%s.parse(String s): this is literally impossible (s=\"%s\")%n", PGNDate.class.getCanonicalName(), s);
                System.out.println("Stacktrace:");
                new Throwable().printStackTrace();
                System.exit(1);
            }
        }
        return result;
    }
}
