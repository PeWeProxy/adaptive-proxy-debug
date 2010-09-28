package sk.fiit.peweproxy.debug;

import java.util.HashMap;
import java.util.Map;

public abstract class stackTraceWatcher {
	private static Map<Object, String> stackTraces = new HashMap<Object, String>();
	
	public static void addStackTrace(Object o) {
		if (stackTraces.containsKey(o))
			System.out.println("Object "+o+" already has stored stackTrace");
		Thread curThread = Thread.currentThread();
		StringBuilder sb = new StringBuilder(curThread.toString());
		sb.append('\n');
		for (StackTraceElement element : curThread.getStackTrace()) {
			sb.append('\t');
			sb.append(element.toString());
			sb.append('\n');
		}
		stackTraces.put(o, sb.toString());
	}
	
	public static void printStackTrace(Object o) {
		String stackTrace = stackTraces.get(o);
		if (stackTrace == null)
			System.err.println("NO stackTrace stored for object "+o+"");
		else
			System.out.println(stackTrace);
	}
}
