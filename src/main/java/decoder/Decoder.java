package decoder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Stream;

public class Decoder {
	public static void main(String[] args) {
		try {
			Scanner sc = new Scanner(System.in);

			// считываем файл с алфавитом
			File file = new File("src/main/java/file/stopka.txt");
			FileReader fr = new FileReader(file);
			BufferedReader reader = new BufferedReader(fr);
			String alphabet = reader.readLine();

			// вводим сообщение, номер столбца в Б-У
			String message = sc.nextLine();
			int indexBWT = sc.nextInt();

			// декодируем сообщение стопкой книг
			String decodedMTF = decodeMTF(alphabet, message);

			// декодируем сообщение Б-У
			System.out.println(decodeBWT(decodedMTF, indexBWT));

		} catch (
				IOException e) {
			e.printStackTrace();
		}
	}

	// -- декодер стопки книг --
	private static String decodeMTF(String alphabet, String message) {

		List<String> alphabetConstList = new ArrayList<>();

		// превращаем строку с алфавитом в ArrayList, сортируем
		alphabetConstList = Stream.of(alphabet.split(" "))
				.sorted()
				.map(String::new).toList();


		// превращаем ArrayList из неизменяемого в изменяемый, иначе операция add не работает
		List<String> unmodifiableList = Collections.unmodifiableList(alphabetConstList);
		List<String> alphabetList = new ArrayList<>(unmodifiableList);

		// ArrayList с кодами, раздаем значения согласно индексам букв в алфавите
		List<String> code = new ArrayList<>();
		for (int i = 0; i < alphabetList.size(); i++) {
			code.add(String.valueOf(i));
		}

		StringBuilder decodedMessage = new StringBuilder();

		String letters = "";

		for (char letter : message.toCharArray()) {
			// текущий закодированный символ
			letters = letters + letter;

			// содержится ли в списке соответствующий сообщению код
			if (code.contains(letters)) {
				// индекс кода в листе кодов
				int ind = code.indexOf(letters);

				// достаем текущий символ по этому индексу
				String let = alphabetList.get(ind);

				// записываем его в конечное сообщение
				decodedMessage.append(let);

				// перемещаем элемент на верх стопки
				alphabetList.remove(ind);
				alphabetList.add(0, let);

				letters = "";
			}

		}
		return decodedMessage.toString();
	}

	// -- декодер Б-У -- (первый способ)
	private static String decodeBWT(String message, int index) {
		StringBuilder decodedMessage = new StringBuilder();

		// количество повторений букву в верхней строчке
		int repeat;

		// запоминаем текущий индекс в верхней строке, чтобы идти по элементам до него
		int position;

		// конвертируем строку с сообщением в ArrayList (это будет наша нижняя строка)
		List<String> messageList = new ArrayList<>(Arrays.asList(message.split("")));

		// собираем отдельный лист с алфавитом и сортируем его, получаем верхнюю строку
		List<String> alphabetList = new ArrayList<>(messageList);
		Collections.sort(alphabetList);

		// декодируем Б-У
		for (int i = 0; i < messageList.size(); i++) {
			// текущий символ верхней строки
			String letter = alphabetList.get(index);

			// записываем в конечную строку
			decodedMessage.append(letter);

			// количество повторений символа в верхней строке перед текущим элементом
			repeat = 0;

			// позиция предыдущего символа в верхней строке
			position = index - 1;

			// считаем количество повторений символа в верхней строке среди предыдущих
			while ((position >= 0) && (alphabetList.get(position).equals(letter))) {
				repeat++;
				position--;
			}

			// количество повторений символа в нижней строке
			int sum = 0;

			// проходимся по нижней строке
			for (int j = 0; j < alphabetList.size(); j++) {
				// находим соответствие между элементом верхней строки и элементом нижней
				if (messageList.get(j).equals(letter)) {
					// проверяем, какое по счету это повторение элемента
					if (sum == repeat) {
						// изменяем индекс текущего элемента, выходим из цикла если нашли нужное соответствие
						index = j;
						break;
					}
					// считаем повторения
					sum++;
				}
			}
		}

		return decodedMessage.toString();
	}
}
