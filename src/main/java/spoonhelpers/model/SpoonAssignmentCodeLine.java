package spoonhelpers.model;

public class SpoonAssignmentCodeLine extends SpoonCodeLine {
    private SpoonVariable spoonVariable;
    private ClassAssignmentCall classAssignmentCall; // TODO ClassAssignmentCall is probably also a subclass. For now let's use it directly until we introduce more subclasses

    private SpoonAssignmentCodeLine(Builder builder) {
        this.spoonVariable = builder.spoonVariable;
        this.classAssignmentCall = builder.classAssignmentCall;
    }

    public static Builder newBuilder(SpoonVariable spoonVariable, ClassAssignmentCall classAssignmentCall) {
        return new Builder(spoonVariable, classAssignmentCall);
    }

    public static class Builder {
        private SpoonVariable spoonVariable;
        private ClassAssignmentCall classAssignmentCall;

        private Builder(SpoonVariable spoonVariable, ClassAssignmentCall classAssignmentCall) {
            this.spoonVariable = spoonVariable;
            this.classAssignmentCall = classAssignmentCall;
        }

        public SpoonAssignmentCodeLine build() {
            return new SpoonAssignmentCodeLine(this);
        }
    }
}
