package com.damari.mvrnd.util;

import org.apache.commons.lang3.StringUtils;

public class StringHelper {

	/**
	 * If string too large crop first string.
	 * @param s to crop.
	 * @param maxLen allowed.
	 * @return Cropped string.
	 */
	public static String crop(String s, int maxLen) {
		if (s == null) return s;
		if (s.length() == 0) return s;

		// One string
		String skipFirstChar = s.substring(1, s.length());
		int i = StringUtils.indexOfAny(skipFirstChar, "ABCDEFGHIJKLMNOPQRSTUVWXYZ ");
		if (i == -1) {
			return StringUtils.capitalize(s.substring(0, Math.min(maxLen, s.length())));
		}

		// Two strings
		i++; // adjust for skip first char
		String s1 = StringUtils.capitalize(s.substring(0, i).trim());
		String s2 = StringUtils.capitalize(s.substring(i, s.length()).trim());
		if ((s1.length() + s2.length()) < maxLen) { // Within boundary
			return s1 + " " + s2;
		}

		// Beyond boundary, shorten first word
		String c1 = s1.substring(0, s1.length() + s2.length() - maxLen + 1);
		if ((c1.length() + s2.length()) < maxLen) {
			return c1 + (c1.length() == s1.length() ? " " : ".") + s2;
		}

		// Crop both string symmetrically'ish
		boolean isEven = (maxLen % 2 == 0);
		return isEven
				? s1.substring(0, maxLen / 2 - 1) + "." + s2.substring(0, maxLen / 2)
				: s1.substring(0, maxLen / 2) + "." + s2.substring(0, maxLen / 2) ;
	}

}
