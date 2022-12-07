package input;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import input.IdentifiedServiceCut;

import java.io.FileNotFoundException;
import java.io.FileReader;

public class DecompositionInputReader {
    public static IdentifiedServiceCut getIdentifiedServiceCut(String decompositionInputFile) {
        IdentifiedServiceCut serviceCut;
        try {
            JsonReader reader = new JsonReader(new FileReader(decompositionInputFile));
            serviceCut = new Gson().fromJson(reader, IdentifiedServiceCut.class);
        } catch (FileNotFoundException e) {
            System.err.println("Unable to read from JSON file with error " + e.getMessage());
            throw new RuntimeException(e);
        }
        return serviceCut;
    }
}
