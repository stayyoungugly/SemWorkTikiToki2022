package coder;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

public class ShannonCoder {
	private static Map<String, String> codes;

	public static void main(String[] args) {
		try {
			Scanner sc = new Scanner(System.in);
			String line = sc.nextLine();

			File file = new File("src/main/java/file/shannon.txt");

			FileReader fr = new FileReader(file);

			BufferedReader reader = new BufferedReader(fr);

			String alphabet = reader.readLine();
			String probability = reader.readLine();

			codes = runShannonCode(alphabet, probability);
			printCodes();

			System.out.println(toShannonCode(line));

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static String toShannonCode(String message) {
		StringBuilder code = new StringBuilder("");
		for (int i = 0; i < message.length(); i++) {
			String charCode = codes.get(String.valueOf(message.charAt(i)));
			code.append(charCode);
		}
		return code.toString();
	}

	private static void printCodes() {
		for (Map.Entry<String, String> entry : codes.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			System.out.println(key + " - " + value);
		}
	}

	private static Map<String, String> runShannonCode(String alphabet, String probability) {
		String[] alphabetArray = alphabet.split(" ");
		String[] probabilityArray = probability.split(" ");

		int length = alphabetArray.length;
		TreeMap<String, String> codes = new TreeMap<>();

		HashMap<String, Double> map = new HashMap<>();
		for (int i = 0; i < length; i++) {
			map.put(alphabetArray[i], Double.parseDouble(probabilityArray[i]));
		}

		Map<String, Double> sortedMap = map.entrySet().stream()
				.sorted(Map.Entry.<String, Double>comparingByValue().reversed())
				.collect(Collectors.toMap(
						Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, TreeMap::new));

		BigDecimal[] q = new BigDecimal[length];

		Object[] p = sortedMap.values().toArray();

		double pPrev;
		double pCurrent;

		int[] l = new int[length];

		for (int i = 0; i < length; i++) {
			pCurrent = (double) p[i];
			if (i == 0) {
				q[i] = BigDecimal.valueOf(0.0);
			} else {
				pPrev = (double) p[i - 1];
				q[i] = BigDecimal.valueOf(pPrev).add(q[i - 1]);
			}
			l[i] = -log2(pCurrent);
		}

		StringBuilder code = new StringBuilder("");
		for (int i = 0; i < length; i++) {
			String key = (String) sortedMap.keySet().toArray()[i];
			if (i == 0) {
				code.append("0".repeat(l[0]));
				codes.put(key, code.toString());
			} else {
				double qDouble = Double.parseDouble(q[i].toString());
				String qCode = fractionalToBinary(qDouble, l[i]);
				codes.put(key, qCode);
			}
		}
		return codes;
	}

	public static int log2(double x) {
		return (int) Math.floor(Math.log(x) / Math.log(2));
	}

	private static String fractionalToBinary(double num, int precision) {
		StringBuilder binary = new StringBuilder();
		while (binary.length() < precision) {
			double r = num * 2;
			if (r >= 1) {
				binary.append(1);
				num = r - 1;
			} else {
				binary.append(0);
				num = r;
			}
		}
		return binary.toString();
	}
}
