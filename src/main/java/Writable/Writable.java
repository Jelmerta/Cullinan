package Writable;

import cullinan.helpers.decomposition.writers.DataWriter;

import java.util.List;

public interface Writable {
    List<DataWriter> createWriters();
    DataWriter createWriter();
}
