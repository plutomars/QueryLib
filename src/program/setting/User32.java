package program.setting;

import com.sun.jna.Native;

public interface User32 extends W32API {

	User32 INSTANCE = (User32) Native.loadLibrary("user32", User32.class,
			DEFAULT_OPTIONS);
	boolean ShowWindow(HWND hWnd, int nCmdShow);

	boolean SetForegroundWindow(HWND hWnd);

	HWND FindWindow(String winClass, String title);
}