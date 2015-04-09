package net.petercashel.commonlib.util;

import java.io.File;
import java.io.IOException;

public class OS_Util {

	public static OS getPlatform() {
		String osName = System.getProperty("os.name").toLowerCase();
		if (osName.contains("win")) {
			return OS.windows;
		}
		if (osName.contains("mac")) {
			return OS.macos;
		}
		if (osName.contains("solaris")) {
			return OS.solaris;
		}
		if (osName.contains("sunos")) {
			return OS.solaris;
		}
		if (osName.contains("linux")) {
			return OS.linux;
		}
		if (osName.contains("unix")) {
			return OS.linux;
		}
		return OS.unknown;
	}

	public static enum OS {
		linux, solaris, windows, macos, unknown;
	}

	public static boolean isPosix() {
		if (getPlatform().ordinal() == 0 || getPlatform().ordinal() == 1
				|| getPlatform().ordinal() == 3 || getPlatform().ordinal() == 4)
			return true;
		return false;
	}

	public static boolean isUnix() {
		if (getPlatform().ordinal() == 0 || getPlatform().ordinal() == 1
				|| getPlatform().ordinal() == 4)
			return true;
		return false;
	}

	public static boolean isDarwin() {
		if (getPlatform().ordinal() == 3)
			return true;
		return false;
	}

	public static boolean isWinNT() {
		if (getPlatform().ordinal() == 2)
			return true;
		return false;
	}
}
