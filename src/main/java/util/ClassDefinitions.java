package util;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Base64;
import java.util.HashSet;
import java.util.Set;

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
public class ClassDefinitions {
    private static final Set<String> serviceClassDefinitions = new HashSet<>();
    private static final Set<String> proxyClassDefinitions = new HashSet<>();

    static {

    }

    private ClassDefinitions() {
    }

    public static boolean isProxyClass(String className) {
        return proxyClassDefinitions.contains(className);
    }

    public static boolean isServiceClass(String className) {
        return serviceClassDefinitions.contains(className);
    }
}
