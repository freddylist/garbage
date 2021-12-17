import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.function.Supplier;

/**
 * User input helpers
 */
public class Input<T> {
	interface Transformer<T, R> {
		public R transform(T value) throws InputMismatchException;

		public static <T> Transformer<T, T> identity() {
			return (T v) -> v;
		}

		public default <V> Transformer<T, V> andThen(Transformer<R, V> transformer) {
			return (T value) -> transformer.transform(this.transform(value));
		}
	}

	static Scanner stdin = new Scanner(System.in);

	Supplier<T> supplier;

	String message;
	String prompt = "> ";
	String errorMessage = "Invalid input, please try again!";

	static void flush() {
		try {
			// Add 10 just in cases :)
			System.in.skip(System.in.available() + 10);
		} catch(Exception e) {
			// We have no idea what happened so just print the stack trace.
			e.printStackTrace();
		}
	}

	/**
	 * Requires user to press the [Enter] key.
	 */
	public static void pause() {
		System.out.println("Press [Enter] to continue...");

		try {
			flush();

			System.in.read();
		} catch(Exception e) {
			// We only wanted to pause for a bit, so just print the stack trace
			// and be done with it!
			e.printStackTrace();
		}
	}

	Input(Supplier<T> supplier) {
		this.supplier = supplier;
	}

	public static Input<Integer> nextInt() {
		return new Input<Integer>(stdin::nextInt).setPrompt("(Enter an integer): ");
	}

	public static Input<String> nextLine() {
		return new Input<String>(() -> {
			String line;

			do {
				line = stdin.nextLine();
			} while(line.matches("\\s*"));

			return line;
		});
	}

	public static boolean binaryChoice(String message) {
		return Input.nextLine()
			.setMessage(message)
			.setPrompt("( [y]es / [n]o ): ")
			.setErrorMessage("Please enter 'y' for yes or 'n' for no!")
			.fetch((String choice) -> {
				if (!choice.matches("[yn].*")) {
					throw new InputMismatchException();
				}
				
				return choice.charAt(0) == 'y';
			});
	}

	public static boolean binaryChoice() {
		return binaryChoice(null);
	}


	public Input<T> setMessage(String message) {
		this.message = message;

		return this;
	}

	public Input<T> setPrompt(String prompt) {
		this.prompt = prompt;

		return this;
	}

	public Input<T> setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;

		return this;
	}

	public <R> R fetch(Transformer<T, R> transformer) {
		boolean valid = false;
		R value = null;

		while (!valid) {
			if (this.message != null)
				System.out.println(this.message);
			
			System.out.print(this.prompt);

			try {
				value = transformer.transform(this.supplier.get());
				valid = true;
			} catch(InputMismatchException e) {
				flush();

				System.out.println(this.errorMessage);
			}
		}

		return value;
	}

	public T fetch() {
		return this.fetch(Transformer.identity());
	}
}