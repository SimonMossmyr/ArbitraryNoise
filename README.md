# Arbitrary Noise
Implementation of Perlin's 1985 noise algorithm to arbitrary dimensions.
The complexity of the algorithm is O(2^n), so for higher dimensions it is incredibly slow.

## Usage
For full usage documentation, generate Javadoc from Noise.java (or just look inside the file).

- `Noise(n)`: Class constructor for noise in n-dimensional space,
- `evaluate(p)`: Evaluate the noise value at the point p.
