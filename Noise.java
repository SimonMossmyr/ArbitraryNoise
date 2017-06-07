import java.util.ArrayList;
import java.util.Random;
import java.util.Collections;

/**
 * Perlin's unmodified 1985 algorithm to arbitrary dimensions.
 * The complexity of the algorithm is O(n*2^n), so for higher dimensions this program is incredibly slow.
 *
 * @author Simon Mossmyr
 */
public class Noise {

	// Constant variables.
	private final static 	int 	DEFAULT_GRADIENT_LIST_SIZE 				= 256;	
	private final static 	int 	DEFAULT_PERMUTATION_LIST_SIZE 			= 256;	
	private final 			String 	INVALID_POINT_DIMENSION_ERROR_MESSAGE 	= "Input point and Noise object are of different dimensions.";
	private final static	int 	DEFAULT_DIMENSION						= 3;

	// Global variables.
	private ArrayList<ArrayList<Double>>	gradientList;
	private ArrayList<Integer> 				permutationList;
	private int 							dimension;

	/**
	 * Default constructor.
	 */
	public Noise() {
		this(DEFAULT_DIMENSION);
	}

	/**
	 * Constructor with variable dimension.
	 */
	public Noise(int dimension) {
		this(dimension, DEFAULT_GRADIENT_LIST_SIZE, DEFAULT_PERMUTATION_LIST_SIZE);
	}

	/**
	 * Super constructor with variable dimension, gradient- and permutation list size.
	 */
	public Noise(int dimension, int gradientListSize, int permutationListSize) {
		this.dimension = dimension;
		gradientList = generateGradientList(dimension, gradientListSize);
		permutationList = generatePermutationList(permutationListSize);
	}

	/**
	 * Generate a list of normal n-vectors, uniformly distributed on the surface of a n-hypersphere.
	 */
	private ArrayList<ArrayList<Double>> generateGradientList(int dimension, int size) {

		ArrayList<ArrayList<Double>> gradientList = new ArrayList<>();
		Random random = new Random();

		for (int i = 0; i < size; i++) {
			ArrayList<Double> gradient = new ArrayList<>();
			double squareSum = 0.;

			// Generate a n-vector from Gaussian random variables
			for (int j = 0; j < dimension; j++) {
				double element = random.nextGaussian();

				gradient.add(element);
				squareSum += element*element;
			}

			// Calculate 1/sqrt(x1^2 + x2^2 + x3^2 + ... + xn^2) as a factor
			double factor = 1. / Math.sqrt(squareSum);

			// Normalize the n-vector by multiplying every element of it with the above factor.
			// Because every element is of Gaussian distribution, the normalized n-vector will be uniformly distributed on the surface of a n-hypersphere.
			for (int j = 0; j < dimension; j++) {
				gradient.set(j, gradient.get(j) * factor);
			}

			gradientList.add(gradient);
		}
		
		return gradientList;
	}

	/**
	 * Generate a permutation of the list of indices in the gradient list.
	 */
	private ArrayList<Integer> generatePermutationList(int size) {
		ArrayList<Integer> permutationList = new ArrayList<>();

		for (int i = 0; i < size; i++) {
			permutationList.add(i % gradientList.size());
		}

		Collections.shuffle(permutationList);

		return permutationList;
	}

	/**
	 * Evaluate the noise value at a point in n-dimensional space.
	 * The dimension of the input point must equal the dimension of the Noise object.
	 */
	private double evaluate(ArrayList<Double> point) throws Exception {
		if (point.size() != dimension) {
			throw new Exception(INVALID_POINT_DIMENSION_ERROR_MESSAGE);
		}

		// Step 1: Floor all elements of the input point
		ArrayList<Integer> flooredPoint = new ArrayList<>();
		for (int i = 0; i < dimension; i++) {
			flooredPoint.add((int)Math.floor(point.get(i)));
		}

		// Step 2: Find the relative coordinates of the point inside the closest n-hypercube of the n-hypercube point lattice.
		ArrayList<Double> relativePoint = new ArrayList<>();
		for (int i = 0; i < dimension; i++) {
			relativePoint.add(point.get(i) - flooredPoint.get(i));
		}

		// Step 3: 



		return 0.;

	}

	/**
	 * Returns the gradient assigned to the input point on the n-hypercube point lattice.
	 */
	private ArrayList<Double> getGradient(ArrayList<Integer> point) {



		return new ArrayList<Double>();
	}

	private int hash(ArrayList<Integer> point, int index) {

		if (index == dimension - 1) {
			return point.get(index);
		} else {
			return point.get(index) + permutationList.get(hash(index+1) % permutationList.size());
		}

	}

}