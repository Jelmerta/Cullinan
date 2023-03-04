package cullinan.helpers.decomposition.generators.model;

import generatedfiles.ParentPom;

public class GeneratedParentModule {
    private ParentPom parentPom;

    public GeneratedParentModule() {
    }

    public void addPom(ParentPom parentPom) {
        this.parentPom =  parentPom;
    }

    public ParentPom getParentPom() {
        return parentPom;
    }
}
