package net.petercashel.commonlib.random;

import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class HexCodeGenerator {

	public static boolean debug = false;

	/*
	 * Generates a Hex based code using name, time, data, rng, and BigInteger.
	 * Supply a name or string and the length required (tested with 96 and 256)
	 */
	public static String Generate(String name, int size) {

		String UNICODEName = toUNICODE(name);
		BigInteger BigIntUNICODEName = new BigInteger(UNICODEName.trim());

		if (HexCodeGenerator.debug) {
			System.out.println(UNICODEName);
		}

		DateFormat dateFormat = new SimpleDateFormat("HHmmssa");
		Date date = new Date();

		if (HexCodeGenerator.debug) {
			System.out.println(dateFormat.format(date));
		}

		String UNICODETime = toUNICODE(dateFormat.format(date));
		BigInteger BigIntUNICODETime = new BigInteger(UNICODETime);

		if (HexCodeGenerator.debug) {
			System.out.println(UNICODETime);
		}

		dateFormat = new SimpleDateFormat("EEEEddMMMyyyy");
		date = new Date();

		if (HexCodeGenerator.debug) {
			System.out.println(dateFormat.format(date));
		}

		String UNICODEDate = toUNICODE(dateFormat.format(date));
		UNICODEDate = toUNICODE(UNICODEDate.trim());
		BigInteger BigIntUNICODEDate = new BigInteger(UNICODEDate);

		if (HexCodeGenerator.debug) {
			System.out.println(UNICODEDate);
		}

		BigInteger BigIntCode_1 = ((BigIntUNICODEName.multiply(BigIntUNICODETime)).multiply(BigIntUNICODEDate));

		if (HexCodeGenerator.debug) {
			System.out.println("BigIntCode_1");
			System.out.println(BigIntCode_1);
		}
		BigInteger BigIntDivider = BigIntCode_1.divide(new BigInteger(String.valueOf(64)));
		if (HexCodeGenerator.debug) {
			System.out.println("BigIntDivider");
			System.out.println(BigIntDivider);			
		}
		String BigIntDividedString = BigIntDivider.toString();
		Random random = new Random(Long.parseLong(BigIntDividedString.substring(4, 20)));
		BigInteger BigIntRandom_1 = new BigInteger(150, random);
		if (HexCodeGenerator.debug) {
			System.out.println("BigIntRandom_1");
			System.out.println(BigIntRandom_1);
		}

		BigInteger BigIntPreFinal = (BigIntCode_1.multiply(BigIntRandom_1)).divide(BigIntDivider);
		BigInteger BigIntFinal = (BigIntPreFinal.multiply(BigIntRandom_1)).multiply(BigIntCode_1);

		if (HexCodeGenerator.debug) {
			System.out.println("BigIntFinal");
			System.out.println(BigIntFinal);
		}

		int BigIntFinalLength = BigIntFinal.toString().length();

		int whole = 0;
		int remains = 0;

		whole = BigIntFinalLength / 4;
		String Hex = "0";		

		if (HexCodeGenerator.debug) {
			System.out.println("HexStart");
			System.out.println(whole);
			System.out.println(remains);
		}


		for(int i = 0; i < whole; i++)
		{
			try {
				if (Hex.length() < ((size+1)*2)) {
					String str = Integer.toHexString(Integer.parseInt(BigIntFinal.toString().substring(i * 4, (i * 4) + 4)));
					Hex = Hex + str;
				}
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (Hex.length() > size) {
			Hex = Hex.substring(32, size + 32);				
		}
		return Hex;
	}

	private static String toUNICODE(String input) {
		String output = "";
		for(int i = 0; i < input.length(); i++)
		{
			try {
				char ch = input.charAt(i);
				int cp = String.valueOf(ch).codePointAt(0);
				output = output + cp;
			} catch (NullPointerException e) {
				//just incase it throws when the character is blank
			}
		}
		return output;
	}

}
