package dev.trainerforge.validator;

import java.math.BigDecimal;

/**
 * Central source of validation limits shared by the domain validators.
 *
 * Update the named ranges here when the reference data grows. Keeping the
 * numeric types separated prevents validators from mixing integral and
 * decimal boundaries accidentally.
 */
public final class ValidationLimits {

    private static final long MIN_IDENTIFIER_ID = 1L;

    private ValidationLimits() {
    }

    public enum Identifier {
        NATIONAL_POKEDEX("National Pokedex number", 1025L),
        GENERATION("Generation ID", 10L),
        REGION("Region ID", 10L),
        TYPE("Type ID", 18L),
        ABILITY("Ability ID", 326L);

        private final String label;
        private final long min;
        private final long max;

        Identifier(String label, long max) {
            this.label = label;
            this.min = MIN_IDENTIFIER_ID;
            this.max = max;
        }

        public String label() {
            return label;
        }

        public long min() {
            return min;
        }

        public long max() {
            return max;
        }
    }

    public enum DecimalRange {
        WEIGHT("Weight", "kg", "0.1", "999.9"),
        HEIGHT("Height", "m", "0.1", "100.0");

        private final String label;
        private final String unit;
        private final BigDecimal min;
        private final BigDecimal max;

        DecimalRange(String label, String unit, String min, String max) {
            this.label = label;
            this.unit = unit;
            this.min = new BigDecimal(min);
            this.max = new BigDecimal(max);
        }

        public String label() {
            return label;
        }

        public String unit() {
            return unit;
        }

        public BigDecimal min() {
            return min;
        }

        public BigDecimal max() {
            return max;
        }
    }

    public enum IntegerRange {
        BASE_STAT(5, 255);

        private final int min;
        private final int max;

        IntegerRange(int min, int max) {
            this.min = min;
            this.max = max;
        }

        public int min() {
            return min;
        }

        public int max() {
            return max;
        }
    }

    public enum BaseStat {
        HP("HP"),
        ATTACK("Attack"),
        DEFENSE("Defense"),
        SPECIAL_ATTACK("Special Attack"),
        SPECIAL_DEFENSE("Special Defense"),
        SPEED("Speed");

        private final String label;

        BaseStat(String label) {
            this.label = label;
        }

        public String label() {
            return label;
        }
    }
}
