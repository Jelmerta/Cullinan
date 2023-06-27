package util;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Base64;

// The reason we need to dynamically check type of objects is because we can for example pass a big object, but we could have an equals(Object object) function
// In that situation we do not know what object is being passed on the other side and how to handle it.
// What if we are dumb and just handle it like an Object while it is a service object? Then we pass an encoded Object instead of the reference id. This object is then decoded like a proxy object? and equals will not return the correct value.
// Do we also need to handle this case if in the monolith a reference to an object is returned and we need to create a proxy?
// This will always return a String with a reference id. Why would it not be possible? We can always try to make a proxy out of it?
// I think we can try this...

// Class used to (de)serialize objects for use with service calls.
// Objects are dynamically serialized: either reference id for service class objects, or just byte64 serialized if not
// TODO Should service classes not in this microservice (but part of other service) be send as reference id or just encoded? We should be able to decode on other side right?
// TODO This could be a common util, where only the service class definitions are stored in the services. Encoding and decoding should be the same for all services.

// TODO How do we deal with arrays? List<Location>? Location[]? Object[]?
public class SerializationUtil {

    private SerializationUtil() {
    }

    public static String encode(Object object) {
        if (object == null) {
            return null;
        }
        String className = object.getClass().getName();
        String parentClassName = className.split("\\$")[0]; // Remove inner classes
        // Remove anonymous
        if (ClassDefinitions.isServiceClass(parentClassName) || ClassDefinitions.isProxyClass(parentClassName)) { // TODO I think this is correct? We should just send the reference id either way? What does mono2micro do?
            CullinanReference cullinanReference = (CullinanReference) object;
            return cullinanReference.getReferenceId(); // (className + "::") + TODO IS this required or is reference always containing this?
        } else {
            return encodeByte64(object);
        }
    }

    // TODO Don't we know from for example the name in the interface whether to decode or to retrieve from storage...?
    // TODO Should we have one storage, that would help here? Otherwise we need to decide in the client/service class instead of here whether to referenceid vs byte64...
    public static Object decode(String serialized) {
        if (serialized == null) {
//            throw new IllegalArgumentException("Did not expect serialized object to be null");
            // Maybe we just allow null objects? Like equals(null) should be allowed? Kind of depends I guess.        }
            return null;        }

        String[] split = serialized.split("::");

        if (split.length < 1 || split.length > 2) {
            throw new IllegalArgumentException("Expected :: to be contained 0 or 1 time in the serialized object, meaning there are either 1 or 2 parts splits"); // TODO Is this too strict? This probably might happen? We could also just take the last occurence of ::?
        }

        if (split.length == 1) {
            return decodeByte64(serialized);
        }

        // Split.length == 2 (:: is contained once)

        String className = split[0];
        String parentClassName = className.split("\\$")[0]; // Remove inner classes
        String referenceId = split[1];
        if (ClassDefinitions.isServiceClass(parentClassName)) {
            return StorageManager.get(serialized); // TODO Only reference id or the whole type::referenceId String?
        } else if (ClassDefinitions.isProxyClass(parentClassName)) {
            // Get class
            try {
                // TODO Constructor or static method...?
                // Use constructor with custom reference id object? Or we could make a public static instanceCreator for proxy objects.
//                CullinanId cullinanId = new CullinanId(referenceId);
//                classType.getConstructor()
                Class<?> classType = Class.forName(className);
                Constructor<?> createProxyConstructor = classType.getConstructor(CullinanId.class);
                CullinanId cullinanId = new CullinanId(serialized); // TODO We could have just the reference id without class, as the class can be inferred maybe? Not sure what is better? what does mono2micro do?
                return createProxyConstructor.newInstance(cullinanId);
            } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException |
                     InvocationTargetException | InstantiationException e) {
                throw new RuntimeException(e);
            }
            // Call constructor
            // return Object
            // In caller, Cast to the correct type
        } else {
            throw new IllegalStateException("Unknown class " + className + " with reference id " + referenceId + " passed.");
        }
    }

    private static String encodeByte64(Object object) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(object);
            objectOutputStream.close();
            return Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Object decodeByte64(String encoded) {
        try {
            byte[] data = Base64.getDecoder().decode(encoded);
            ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(data));
            Object object = objectInputStream.readObject();
            objectInputStream.close();
            return object;
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
