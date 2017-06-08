import java.util.ArrayList;

public class Main{
	public static void main(String[] args) {

		Noise noise = new Noise(2);

		for (double i = 0.1; i < 1.; i += 0.1) {
			for (double j = 0.1; j < 1.; j += 0.1) {
				ArrayList<Double> point = new ArrayList<>();
				point.add(i);
				point.add(j);
				System.out.printf("%f.5 ", noise.evaluate(point));
			}
			System.out.println();

		}



	}
}