import java.util.ArrayList;
import java.util.Random;
import java.util.Collections;
import java.util.BitSet;

/**
 * Perlin's unmodified 1985 algorithm to arbitrary dimensions.
 * The complexity of the algorithm is O(2^n), so for higher dimensions this program is incredibly slow.
 *
 * @author Simon Mossmyr
 */
public class Noise {

	// Constant variables.
	private final static 	int 	DEFAULT_GRADIENT_LIST_SIZE 				= 256;	
	private final static 	int 	DEFAULT_PERMUTATION_LIST_SIZE 			= 256;	
	private final static	int 	DEFAULT_DIMENSION						= 3;
	private final 			String 	INVALID_POINT_DIMENSION_ERROR_MESSAGE 	= "Input point and Noise object are of different dimensions.";
	private final 			String 	INVALID_DIMENSION_ERROR_MESSAGE 		= "Dimension must be greater than 0.";

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
	public Noise(int dimension, int gradientListSize, int permutationListSize) throws RuntimeException {
		if (dimension < 1) {
			throw new RuntimeException(INVALID_DIMENSION_ERROR_MESSAGE);
		}

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
	public double evaluate(ArrayList<Double> point) throws RuntimeException {
		// Input validation
		if (point.size() != dimension) {
			throw new RuntimeException(INVALID_POINT_DIMENSION_ERROR_MESSAGE);
		}

		// Variables used throughout the method
		int derp = (int)Math.pow(2,dimension);

		// Step 1: Floor all elements of the input point
		ArrayList<Integer> flooredPoint = new ArrayList<>();
		for (int i = 0; i < dimension; i++) {
			flooredPoint.add((int)Math.floor(point.get(i)));
		}

		// Step 2: Find the relative coordinates of the point inside the surrounding n-hypercube
		ArrayList<Double> relativePoint = new ArrayList<>();
		for (int i = 0; i < dimension; i++) {
			relativePoint.add(point.get(i) - flooredPoint.get(i));
		}

		// Step 3: Find the gradients assigned to the vertices of the surrounding n-hypercube
		ArrayList<ArrayList<Double>> gradients = new ArrayList<>();
		for(int i = 0; i < derp; i++) {
			ArrayList<Integer> vertex = new ArrayList<>();
			for (int j = 0; j < dimension; j++) {
				vertex.add(flooredPoint.get(j) + (((i >> j) & 1) == 1 ? 1 : 0));
			}
			gradients.add(getGradient(vertex));
		}

		// Step 4: Find the distance vectors
		ArrayList<ArrayList<Double>> distanceVectors = new ArrayList<>();
		for (int i = 0; i < derp; i++) {
			ArrayList<Double> distanceVector = new ArrayList<>();
			for (int j = 0; j < dimension; j++) {
				distanceVector.add(point.get(j) - (((i >> j) & 1) == 1 ? 1 : 0));
			}
			distanceVectors.add(distanceVector);
		}

		// Step 5: Find the noise contributions
		ArrayList<Double> noiseContributions = new ArrayList<>();
		for (int i = 0; i < derp; i++) {
			noiseContributions.add(dot(gradients.get(i), distanceVectors.get(i)));
		}

		// Step 6: Blend the noise contributions by iterating over every dimension
		ArrayList<ArrayList<Double>> merp = new ArrayList<>();
		ArrayList<Double> gerp = new ArrayList<>();
		for (int j = 0; j < noiseContributions.size(); j += 2) {
			double blend = blend(relativePoint.get(0));
			gerp.add(noiseContributions.get(j) * (1 - blend) + noiseContributions.get(j+1) * blend);
		}
		merp.add(gerp);

		for (int i = 1; i < dimension; i++) {
			ArrayList<Double> berp = new ArrayList<>();
			for (int j = 0; j < merp.get(i-1).size(); j += 2) {
				double blend = blend(relativePoint.get(i));
				berp.add(merp.get(i-1).get(j) * (1 - blend) + merp.get(i-1).get(j+1) * blend);
			}
			merp.add(berp);
		}

		// Return final noise value
		return merp.get(dimension-1).get(0);

	}

	/**
	 * Returns the gradient assigned to the input point on the n-hypercube point lattice.
	 */
	private ArrayList<Double> getGradient(ArrayList<Integer> point) {
		return gradientList.get(hash(point, 0));
	}

	/**
	 * Recursive hash function used by getGradient().
	 */
	private int hash(ArrayList<Integer> point, int index) {
		if (index == dimension - 1) {
			return permutationList.get(point.get(index) % permutationList.size());
		} else {
			return permutationList.get((point.get(index) + hash(point, index+1)) % permutationList.size());
		}
	}

	/**
	 * Dot (scalar) product of two ArrayList objects.
	 */
	private double dot(ArrayList<Double> a, ArrayList<Double> b) {
		double sum = 0;

		for (int i = 0; i < a.size(); i++) {
			sum += a.get(i) * b.get(i);
		}

		return sum;
	}

	/**
	 * Blending function.
	 */
	private double blend(double t) {
		return t * t * t * (3 * t * (2 * t - 5) + 10); // 6t^5 - 15t^4 + 10t^3
	}
}