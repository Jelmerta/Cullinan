package cullinanalternativeapproach;

import java.nio.file.Path;

// I know where to write and how to write and what to write!
public interface DataWriter2 {
    boolean shouldWrite(ServiceDefinition serviceDefinition);
    void write(ServiceDefinition serviceDefinition);
}
