package spoonhelpers.model;

import java.util.List;

public class SpoonBody {
    private List<SpoonCodeLine> codeLines;

    private SpoonBody(Builder builder) {
        this.codeLines = builder.codeLines;
    }

    public static Builder newBuilder() {
        return new SpoonBody.Builder();
    }

//    public void addLine(SpoonCodeLine codeLine) {
//
//    }

    public static class Builder {

        private List<SpoonCodeLine> codeLines = null;

        public SpoonBody build() {
            return new SpoonBody(this);
        }
    }
}
