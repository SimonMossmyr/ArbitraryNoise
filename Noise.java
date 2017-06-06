public class Noise {

	public Noise(int dimension) {

	}

	/**
	 * Generate a list of gradients as the set of vectors from the center of a n-dimensional hypercube to the mid-point of its edges.
	 * To increase performance, the list is padded with 
	 *
	 * Complexity: O(n*2^n)
	 */
	private List<List<Integer>> generateGradients(int dimension) {

		// n*2^(n-1) number of edges

		// 2D: 	( 1, 0) (0,  1) 
		// 		(-1, 0) (0, -1)
		//
		// 3D: 	( 1,  1, 0) ( 1, 0,  1) (0,  1,  1)
		//		( 1, -1, 0) ( 1, 0, -1) (0,  1, -1)
		//		(-1,  1, 0) (-1, 0,  1) (0, -1,  1)
		//		(-1, -1, 0) (-1, 0, -1) (0, -1, -1)
		//
		// 4D:	( 1,  1,  1, 0) ( 1,  1, 0,  1) ( 1, 0,  1,  1) (0,  1,  1,  1)
		// 		( 1,  1, -1, 0) ( 1,  1, 0, -1) ( 1, 0,  1, -1) (0,  1,  1, -1)
		// 		( 1, -1,  1, 0) ( 1, -1, 0,  1) ( 1, 0, -1,  1) (0,  1, -1,  1)
		// 		( 1, -1, -1, 0) ( 1, -1, 0, -1) ( 1, 0, -1, -1) (0,  1, -1, -1)
		//  	(-1,  1,  1, 0) (-1,  1, 0,  1) (-1, 0,  1,  1) (0, -1,  1,  1)
		// 		(-1,  1, -1, 0) (-1,  1, 0, -1) (-1, 0,  1, -1) (0, -1,  1, -1)
		// 		(-1, -1,  1, 0) (-1, -1, 0,  1) (-1, 0, -1,  1) (0, -1, -1,  1)
		// 		(-1, -1, -1, 0) (-1, -1, 0, -1) (-1, 0, -1, -1) (0, -1, -1, -1)

		


	}

}