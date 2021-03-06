import java.util.ArrayList;
import java.util.Random;
import java.util.Collections;

/**
 * Perlin's unmodified 1985 algorithm to arbitrary dimensions.
 * The complexity of the algorithm is O(2^n), so for higher dimensions it is incredibly slow.
 *
 * The implementation isn't especially optimized, it focuses readability over performance.
 *
 * @author 	Simon Mossmyr
 * @version	June 2017
 * @see		<a href="https://github.com/SimonMossmyr/ArbitraryNoise">GitHub repository</a>
 * @see		<a href="http://doi.acm.org/10.1145/325165.325247">An Image Synthesizer</a> (Perlin, 1985). DOI: 10.1145/325165.325247
 */
public class Noise {

	// Constant variables.
	private	static	final	int 	DEFAULT_GRADIENT_LIST_SIZE 		= 256;	
	private	static	final	int 	DEFAULT_INDEX_LIST_SIZE 		= 256;	
	private	static	final	int 	DEFAULT_DIMENSION			= 3;
	private		final	String 	INVALID_POINT_DIMENSION_ERROR_MESSAGE 	= "Size of input differs from dimension of Noise object.";
	private		final	String 	INVALID_DIMENSION_ERROR_MESSAGE 	= "Dimension must be greater than 0.";

	// Global variables.
	private ArrayList<ArrayList<Double>> gradientList;
	private ArrayList<Integer> indexList;
	private int dimension;

	/**
	 * Class constructor with no specifications. Defaults to {@value Noise#DEFAULT_DIMENSION}-dimensional space.
	 */
	public Noise() {
		this(DEFAULT_DIMENSION);
	}

	/**
	 * Class constructor specifying what dimension to use.
	 *
	 * @param	dimension	The dimension to evaluate the noise values in.
	 */
	public Noise(int dimension) {
		this(dimension, DEFAULT_GRADIENT_LIST_SIZE, DEFAULT_INDEX_LIST_SIZE);
	}

	/**
	 * Class constructor specifying what dimension to use, and what sizes to use for generating the gradient- and index list.
	 *
	 * @param	dimension 		The dimension to evaluate the noise values in.
	 *					Defaults to {@value #DEFAULT_DIMENSION} in the other constructors.
	 * @param 	gradientListSize	Size of the gradient list used when assigning gradients to the verticies of the surrounding n-hypercube.
	 *					Defaults to {@value #DEFAULT_GRADIENT_LIST_SIZE} in the other constructors.
	 * @param 	indexListSize		Size of the index list used when assigning gradients to the verticies of the surrounding n-hypercube.
	 *					Defaults to {@value #DEFAULT_INDEX_LIST_SIZE} in the other constructors.
	 * @exception	RuntimeException	If the dimension is less than 1
	 */
	public Noise(int dimension, int gradientListSize, int indexListSize) throws RuntimeException {
		if (dimension < 1) {
			throw new RuntimeException(INVALID_DIMENSION_ERROR_MESSAGE);
		}

		this.dimension = dimension;
		gradientList = generateGradientList(gradientListSize);
		indexList = generateIndexList(indexListSize);
	}

	/**
	 * Generate a list of normalized n-vectors uniformly distributed on the surface of a n-hypersphere.
	 *
	 * @param	size	Size of the list.
	 * @return		A list of normalized n-vectors uniformly distributed on the surface of a n-hypersphere.
	 */
	private ArrayList<ArrayList<Double>> generateGradientList(int size) {
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

			// Calculate the normalization factor 1/sqrt(x1^2 + x2^2 + x3^2 + ... + xn^2)
			double normalizationFactor = 1. / Math.sqrt(squareSum);

			// Normalize the n-vector by multiplying every element of it with the normalization factor.
			// Because every element is of Gaussian distribution, the normalized n-vector will be uniformly distributed on the surface of a n-hypersphere.
			for (int j = 0; j < dimension; j++) {
				gradient.set(j, gradient.get(j) * normalizationFactor);
			}

			gradientList.add(gradient);
		}
		
		return gradientList;
	}

	/**
	 * Generate a permutation of the list of indices in the gradient list.
	 *
	 * @param	size	Size of the list.
	 * @return		A permutation of the list of indices in the gradient list.
	 */
	private ArrayList<Integer> generateIndexList(int size) {
		ArrayList<Integer> indexList = new ArrayList<>();

		for (int i = 0; i < size; i++) {
			indexList.add(i % gradientList.size());
		}

		Collections.shuffle(indexList);

		return indexList;
	}

	/**
	 * Evaluate the noise value at a point in n-dimensional space.
	 * The dimension of the input point must equal the dimension of the Noise object.
	 *
	 * @param	point			The point to be evaluated.
	 * @exception	RuntimeException	If the size of the point parameter is not equal to the specified dimension of the Noise object.
	 * @return				The evaluated noise value.
	 */
	public double evaluate(ArrayList<Double> point) throws RuntimeException {
		// Input validation
		if (point.size() != dimension) {
			throw new RuntimeException(INVALID_POINT_DIMENSION_ERROR_MESSAGE);
		}

		// Step 1: Floor all elements of the input point.
		// Time: O(n)
		ArrayList<Integer> flooredPoint = new ArrayList<>();
		for (int i = 0; i < dimension; i++) {
			flooredPoint.add((int)Math.floor(point.get(i)));
		}

		// Step 2: Find the relative coordinates of the point inside the surrounding n-hypercube.
		// Time: O(n)
		ArrayList<Double> relativePoint = new ArrayList<>();
		for (int i = 0; i < dimension; i++) {
			relativePoint.add(point.get(i) - flooredPoint.get(i));
		}

		// Step 3: Find the gradients assigned to the vertices of the surrounding n-hypercube.
		// Time: O(2^n)
		int numberOfVertices = (int) Math.pow(2, dimension);
		ArrayList<ArrayList<Double>> gradients = new ArrayList<>();
		for(int i = 0; i < numberOfVertices; i++) {
			ArrayList<Integer> vertex = new ArrayList<>();

			// You can find all the vertices of a n-hypercube by iterating over an integer while checking bits.
			// In 3-dimensional space, the coordinates of the vertices of the surrounding cube with an origin vertex [4 3 7] will be 
			//
			// [4 3 7] --> 0b000 --> 0
			// [4 3 8] --> 0b001 --> 1
			// [4 4 7] --> 0b010 --> 2
			// [4 4 8] --> 0b011 --> 3
			// [5 3 7] --> 0b100 --> 4
			// [5 3 8] --> 0b101 --> 5
			// [5 4 7] --> 0b110 --> 6
			// [5 4 7] --> 0b111 --> 7
			//
			// (supposing the origin vertex's coordinates are [4 3 7]).
			// You can thus iterate over an integer 0 --> 7 and check the lowest 3 bits,
			// starting with [4 3 7] and adding 1 for every bit that's set to 1.
			//
			// This technique is in a similar manner used in Step 4 to find the distance vectors.
			for (int j = 0; j < dimension; j++) {
				vertex.add(flooredPoint.get(j) + (((i >> j) & 1) == 1 ? 1 : 0));
			}
			gradients.add(getGradient(vertex));
		}

		// Step 4: Find the distance vectors from the vertices of the surrounding n-hypercube to the point.
		// Time: O(2^n)
		ArrayList<ArrayList<Double>> distanceVectors = new ArrayList<>();
		for (int i = 0; i < numberOfVertices; i++) {
			ArrayList<Double> distanceVector = new ArrayList<>();
			for (int j = 0; j < dimension; j++) {
				distanceVector.add(point.get(j) - (((i >> j) & 1) == 1 ? 1 : 0));
			}
			distanceVectors.add(distanceVector);
		}

		// Step 5: Find the noise contributions as the dot product of the gradients and the distance vectors.
		// Time: O(2^n)
		ArrayList<Double> noiseContributions = new ArrayList<>();
		for (int i = 0; i < numberOfVertices; i++) {
			noiseContributions.add(dot(gradients.get(i), distanceVectors.get(i)));
		}

		// Step 6: Blend the noise contributions by iterating over every dimension.
		// Time: O(2^n)
		ArrayList<ArrayList<Double>> b = new ArrayList<>();
		ArrayList<Double> interpolation = new ArrayList<>();
		for (int j = 0; j < numberOfVertices; j += 2) {
			double blend = blend(relativePoint.get(0));
			interpolation.add(noiseContributions.get(j) * (1 - blend) + noiseContributions.get(j+1) * blend);
		}
		b.add(interpolation);

		for (int i = 1; i < dimension; i++) {
			interpolation = new ArrayList<>();
			for (int j = 0; j < b.get(i-1).size(); j += 2) {
				double blend = blend(relativePoint.get(i));
				interpolation.add(b.get(i-1).get(j) * (1 - blend) + b.get(i-1).get(j+1) * blend);
			}
			b.add(interpolation);
		}

		// Return final noise value
		return b.get(dimension-1).get(0);

	}

	/**
	 * Returns the gradient assigned to the input vertex of the n-hypercube point lattice.
	 *
	 * @param	vertex	The n-hypercube vertex.
	 * @return		The gradient assigned to the vertex.
	 */
	private ArrayList<Double> getGradient(ArrayList<Integer> vertex) {
		return gradientList.get(hash(vertex, 0));
	}

	/**
	 * Hash function used by getGradient(). Recursively checks the index list using the elements of the input vertex.
	 *
	 * @param	vertex	The n-hypercube vertex.
	 * @param	index	The index of the recursion.
	 * @return		A psuedo-random element of the index list.
	 */
	private int hash(ArrayList<Integer> vertex, int index) {
		if (index == dimension - 1) {
			return indexList.get(vertex.get(index) % indexList.size());
		} else {
			return indexList.get((vertex.get(index) + hash(vertex, index+1)) % indexList.size());
		}
	}

	/**
	 * Dot (scalar) product of two ArrayList objects.
	 *
	 * @param	a	One of the vectors to calculate the dot product from.
	 * @param	b	The other vector to calculate the dot product from.
	 * @return		The dot product.
	 */
	private double dot(ArrayList<Double> a, ArrayList<Double> b) {
		double sum = 0;

		for (int i = 0; i < a.size(); i++) {
			sum += a.get(i) * b.get(i);
		}

		return sum;
	}

	/**
	 * The blending function used in the dimensional blending of the evaluate() method.
	 *
	 * @param	t	An input.
	 * @return		A blended value.
	 * @see			Improved version introduced in the article <a href="http://doi.acm.org/10.1145/566570.566636">Improving Noise</a> (Perlin, 2002). DOI: 10.1145/566570.566636.
	 */
	private double blend(double t) {
		return t * t * t * (3 * t * (2 * t - 5) + 10); // 6t^5 - 15t^4 + 10t^3
	}
}