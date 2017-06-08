import java.util.ArrayList;

/**
 * Simple testing class for the Noise class.
 *
 * @author Simon Mossmyr
 * 
 */
public class Main{

	public static void main(String[] args) {
		Noise noise = new Noise(8);

		for (double i = 0.1; i < 1.; i += 0.1) {
			for (double j = 0.1; j < 1.; j += 0.1) {
				ArrayList<Double> point = new ArrayList<>();
				point.add(i);
				point.add(j);
				point.add(0.);
				point.add(1.);
				point.add(2.);
				point.add(3.);
				point.add(4.);
				point.add(5.);
				System.out.printf("%f ", noise.evaluate(point));
			}
			System.out.println();
		}
	}
}