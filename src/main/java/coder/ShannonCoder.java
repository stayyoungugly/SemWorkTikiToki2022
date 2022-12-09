package coder;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

public class ShannonCoder {
	// словарь кодовых слов, (слово - код)
	private static Map<String, String> codes;

	public static void main(String[] args) {
		try {
			// считываем файл с алфавитом и вероятностями
			File file = new File("src/main/java/file/shannon.txt");
			FileReader fr = new FileReader(file);
			BufferedReader reader = new BufferedReader(fr);

			// вводим строку, которую хотим закодировать
			Scanner sc = new Scanner(System.in);
			String line = sc.nextLine();

			// считываем алфавит из файла
			String alphabet = reader.readLine();

			// считываем распределение вероятностей из файла
			String probability = reader.readLine();

			// получаем словарь из кодовых слов
			codes = runShannonCode(alphabet, probability);

			// выводим словарь (опционально)
			printCodes();

			// кодируем сообщение и выводим
			System.out.println(toShannonCode(line));

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static String toShannonCode(String message) {
		StringBuilder code = new StringBuilder("");

		// кодируем посимвольно сообщение
		for (int i = 0; i < message.length(); i++) {

			// находим соответствующий символу код в словаре
			String charCode = codes.get(String.valueOf(message.charAt(i)));

			// добавляем найденный символ к конечному закодированному сообщению
			code.append(charCode);
		}
		return code.toString();
	}

	// -- вспомогательный метод, выводим словарь кодов --
	private static void printCodes() {
		for (Map.Entry<String, String> entry : codes.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			System.out.println(key + " - " + value);
		}
	}

	// -- метод для кодировки словаря с помощью алгоритма Шеннона --
	private static Map<String, String> runShannonCode(String alphabet, String probability) {

		// переводим строку с алфавитом в массив
		String[] alphabetArray = alphabet.split(" ");

		// переводим строку с вероятностями в массив
		String[] probabilityArray = probability.split(" ");

		int length = alphabetArray.length;

		// словарь с кодами
		TreeMap<String, String> codes = new TreeMap<>();

		// словарь символ алфавита - вероятность
		HashMap<String, Double> map = new HashMap<>();
		for (int i = 0; i < length; i++) {
			map.put(alphabetArray[i], Double.parseDouble(probabilityArray[i]));
		}


		// сортируем данный словарь по убыванию вероятностей
		Map<String, Double> sortedMap = map.entrySet().stream()
				.sorted(Map.Entry.<String, Double>comparingByValue().reversed())
				.collect(Collectors.toMap(
						Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, TreeMap::new));

		// переменная q, равная сумме предыдущих вероятностей, BigDecimal для точного сложения
		BigDecimal[] q = new BigDecimal[length];

		// вытаскиваем из словаря набор вероятностей и переводим его в массив, для удобства
		Object[] p = sortedMap.values().toArray();

		// вероятность предыдущего символа
		double pPrev;

		// вероятность текущего символа
		double pCurrent;

		// массив с логарифмами вероятностей
		int[] l = new int[length];

		for (int i = 0; i < length; i++) {
			pCurrent = (double) p[i];

			// q для нулевого элемента будет 0
			if (i == 0) {
				q[i] = BigDecimal.valueOf(0.0);
			} else {
				pPrev = (double) p[i - 1];

				// находим q для текущего символа алфавита, q предыдущего + вероятность предыдущего
				q[i] = BigDecimal.valueOf(pPrev).add(q[i - 1]);
			}

			// вычисляем логарифм для текущей вероятности
			l[i] = -log2(pCurrent);
		}

		StringBuilder code = new StringBuilder("");

		for (int i = 0; i < length; i++) {
			// перебираем по порядку символы алфавита
			String key = (String) sortedMap.keySet().toArray()[i];
			// если это первый символ, его код будет повторением 0 l[0] раз
			if (i == 0) {
				code.append("0".repeat(l[0]));
				// записываем код в словарь
				codes.put(key, code.toString());
			} else {
				// переводим q из String в double
				double qDouble = Double.parseDouble(q[i].toString());

				System.out.println(qDouble);

				// переводим дробную часть q из десятичной в двоичную систему счисления
				String qCode = fractionalToBinary(qDouble, l[i]);

				System.out.println(qCode);

				// записываем код в словарь
				codes.put(key, qCode);
			}
		}
		// возвращаем конечный словарь
		return codes;
	}


	// -- метод для нахождения log по основанию 2 --
	public static int log2(double x) {
		return (int) Math.floor(Math.log(x) / Math.log(2));
	}

	// -- метод для перевода дробной части в двоичную систему с точностью precision символов после запятой --
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
