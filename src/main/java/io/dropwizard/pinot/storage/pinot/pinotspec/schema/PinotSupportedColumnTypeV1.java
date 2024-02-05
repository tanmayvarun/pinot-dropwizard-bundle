package io.dropwizard.pinot.storage.pinot.pinotspec.schema;

/**
 * todo: we will have to keep versioning of data types to ensure our logics don't break when
 * pinot makes breaking changes to data types in future.
 */
public enum PinotSupportedColumnTypeV1 {
    INT {
        @Override
        public <U, V> V accept(PinotSupportedColumnTypeV1Visitor<U, V> visitor) {
            return visitor.visitInt();
        }
    },
    LONG {
        @Override
        public <U, V> V accept(PinotSupportedColumnTypeV1Visitor<U, V> visitor) {
            return visitor.visitLong();
        }
    },
    FLOAT {
        @Override
        public <U, V> V accept(PinotSupportedColumnTypeV1Visitor<U, V> visitor) {
            return visitor.visitFloat();
        }
    },
    DOUBLE {
        @Override
        public <U, V> V accept(PinotSupportedColumnTypeV1Visitor<U, V> visitor) {
            return visitor.visitDouble();
        }
    },
    BOOLEAN {
        @Override
        public <U, V> V accept(PinotSupportedColumnTypeV1Visitor<U, V> visitor) {
            return visitor.visitBoolean();
        }
    },
    TIMESTAMP {
        @Override
        public <U, V> V accept(PinotSupportedColumnTypeV1Visitor<U, V> visitor) {
            return visitor.visitTimestamp();
        }
    },
    STRING {
        @Override
        public <U, V> V accept(PinotSupportedColumnTypeV1Visitor<U, V> visitor) {
            return visitor.visitString();
        }
    },
    BYTES {
        @Override
        public <U, V> V accept(PinotSupportedColumnTypeV1Visitor<U, V> visitor) {
            return visitor.visitBytes();
        }
    },
    JSON {
        @Override
        public <U, V> V accept(PinotSupportedColumnTypeV1Visitor<U, V> visitor) {
            return visitor.visitJson();
        }
    };

    public abstract <U, V> V accept(PinotSupportedColumnTypeV1Visitor<U, V> visitor);

    public interface PinotSupportedColumnTypeV1Visitor<U,V> {

        V visitInt();

        V visitLong();

        V visitFloat();
        V visitDouble();
        V visitBoolean();
        V visitTimestamp();

        V visitString();

        V visitBytes();

        V visitJson();

    }
}
