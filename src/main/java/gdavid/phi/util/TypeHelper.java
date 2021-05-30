package gdavid.phi.util;

public class TypeHelper {
	
	public static Class<?> commonSuper(Class<?> a, Class<?> b) {
		if (b == null) return null;
		Class<?> s = a;
		while (s != null && !s.isAssignableFrom(b)) {
			s = s.getSuperclass();
		}
		return s;
	}
	
}
