package cullinan.helpers.templates;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ServiceClientTemplate {
    private static ClassNotFoundException service; // This field can be null even though it is initialized (not null) in the constructor
    static {
        try {
            Registry registry = LocateRegistry.getRegistry();
            service = (ClassNotFoundException) registry.lookup("//localhost/microserviceName");
        } catch (NotBoundException | RemoteException e) {
            throw new RuntimeException(e);
        }
    }
}
