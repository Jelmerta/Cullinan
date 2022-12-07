package cullinan.test.imports;

public final class StaticField {
    private static String value;
    static {
        value = "hello";
    }

    public StaticField() {
    }

    public String getValue() {
        return value;
    }
}