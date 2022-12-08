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
			String message = sc.nextLine();
			int indexBWT = sc.nextInt();

			File file = new File("src/main/java/file/stopka.txt");

			FileReader fr = new FileReader(file);

			BufferedReader reader = new BufferedReader(fr);

			String alphabet = reader.readLine();
			String decodedMTF = decodeMTF(alphabet, message);

			System.out.println(decodedMTF);
			System.out.println(decodeBWT(decodedMTF, indexBWT));

		} catch (
				IOException e) {
			e.printStackTrace();
		}
	}

	private static String decodeMTF(String alphabet, String message) {
		List<String> alphabetConstList = new ArrayList<>();

		alphabetConstList = Stream.of(alphabet.split(" "))
				.sorted()
				.map(String::new).toList();

		List<String> unmodifiableList = Collections.unmodifiableList(alphabetConstList);

		List<String> alphabetList = new ArrayList<>(unmodifiableList);

		List<String> code = new ArrayList<>();

		for (int i = 0; i < alphabetList.size(); i++) {
			code.add(String.valueOf(i));
		}

		StringBuilder decodedMessage = new StringBuilder();

		String letters = "";

		for (char letter : message.toCharArray()) {
			letters = letters + letter;

			if (code.contains(letters)) {
				int ind = code.indexOf(letters);
				String let = alphabetList.get(ind);
				decodedMessage.append(let);

				alphabetList.remove(ind);
				alphabetList.add(0, let);

				letters = "";
			}

		}
		return decodedMessage.toString();
	}

	private static String decodeBWT(String message, int index) {
		StringBuilder decodedMessage = new StringBuilder();

		int repeat;

		List<String> messageList = new ArrayList<>(Arrays.asList(message.split("")));

		List<String> alphabetList = new ArrayList<>(messageList);

		List<Integer> indexes = new ArrayList<>();
		for (int i = 0; i < messageList.size(); i++) {
			indexes.add(i);
		}

		Collections.sort(alphabetList);
		System.out.println(alphabetList);

		for (int i = 0; i < messageList.size(); i++) {
			String letter = alphabetList.get(index);
			decodedMessage.append(letter);
			int j = 0;
			for (Integer ind : indexes) {
				if (messageList.get(ind).equals(letter)) {
					j = ind;
					indexes.remove(ind);
					break;
				}
			}
			index = j;
		}

		return decodedMessage.toString();
	}
}
