package jdbcext.types;

import java.math.BigDecimal;

public final class TypeConverters {
    private TypeConverters(){}

    private abstract static class AbstractConverter<S,D> implements TypeConverter<S,D> {
        private Class<S> sourceType;
        private Class<D> destinationType;

        AbstractConverter(Class<S> sourceType, Class<D> destinationType) {
            this.sourceType = sourceType;
            this.destinationType = destinationType;
        }

        public final Class<S> getSourceType() {
            return sourceType;
        }

        public final Class<D> getDestinationType() {
            return destinationType;
        }
    }

    public static class LongToBigDecimalConverter extends AbstractConverter<Long,BigDecimal> {

        public LongToBigDecimalConverter() {
            super(Long.class, BigDecimal.class);
        }

        @Override
        public BigDecimal convert(Long object) {
            if(object == null) {
                return null;
            }

            return BigDecimal.valueOf(object);
        }
    }

    public static class IntegerToBigDecimalConverter extends AbstractConverter<Integer,BigDecimal> {

        public IntegerToBigDecimalConverter() {
            super(Integer.class, BigDecimal.class);
        }

        @Override
        public BigDecimal convert(Integer object) {
            if(object == null) {
                return null;
            }

            return BigDecimal.valueOf(object);
        }
    }

    public static class BigDecimalToLongConverter extends AbstractConverter<BigDecimal,Long> {

        public BigDecimalToLongConverter() {
            super(BigDecimal.class, Long.class);
        }

        @Override
        public Long convert(BigDecimal object) {
            if(object == null) {
                return null;
            }

            return object.longValueExact();
        }
    }

    public static class BigDecimalToIntegerConverter extends AbstractConverter<BigDecimal,Integer> {

        public BigDecimalToIntegerConverter() {
            super(BigDecimal.class, Integer.class);
        }

        @Override
        public Integer convert(BigDecimal object) {
            if(object == null) {
                return null;
            }

            return object.intValueExact();
        }
    }
}
