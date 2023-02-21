package generatedfiles;

import cullinan.helpers.decomposition.writers.DataWriter;

public interface Writable {
    DataWriter createWriter();
    // Object getData();? We need the CtType somehow, this does not work then...?
}
