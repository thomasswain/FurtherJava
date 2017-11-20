package tick2star;

import java.io.FilePermission;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.net.SocketPermission;
import java.security.PermissionCollection;
import java.security.Permissions;
import java.security.ProtectionDomain;
import java.security.SecureClassLoader;
import java.util.PropertyPermission;

public class SafeObjectInputStream extends ObjectInputStream {

	private SecureClassLoader classLoader = (SecureClassLoader) ClassLoader.getSystemClassLoader();
	private final ProtectionDomain protectionDomain;
	
	public SafeObjectInputStream(InputStream in) throws IOException {
		super(in);
		final SocketPermission socketPermission = new SocketPermission("www.cam.ac.uk:" + 80, "connect");
		final PropertyPermission propertyPermission = new PropertyPermission("user.home", "read");
		final FilePermission filePermission = new FilePermission(System.getProperty("user.home"), "read");
			
		final PermissionCollection permissionCollection = new Permissions();
		permissionCollection.add(socketPermission);
		permissionCollection.add(propertyPermission);
		permissionCollection.add(filePermission);
		
		protectionDomain = new ProtectionDomain(null, permissionCollection);
	}

	@Override
	protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException,
			ClassNotFoundException {
		try {
			return classLoader.loadClass(desc.getName());
		}
		catch (ClassNotFoundException e) {
			return super.resolveClass(desc);
		}
	}

	public void addClass(final String name, final byte[] defn) {
		classLoader = new SecureClassLoader(classLoader) {
			@Override
			protected Class<?> findClass(String className)
					throws ClassNotFoundException {
				if (className.equals(name)) {
					Class<?> result = defineClass(name, defn, 0, defn.length, protectionDomain);
					return result;
				} else {
					throw new ClassNotFoundException();
				}
			}
		};
	}

}
