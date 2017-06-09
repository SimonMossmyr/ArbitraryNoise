# Arbitrary Noise
Implementation of Perlin's 1985 noise algorithm to arbitrary dimensions. 

The algorithm has many uses within the computer graphics field, mostly for adding realism to textures but also for generating naturalistic terrain, liquids and animation. The dimension used is almost always 2, 3 or 4 (although Perlin has mentioned 5 being used), so the implementations are hard-coded for these dimensions. This implementation, however, can be used for any dimension.

The time- and space complexity of the algorithm is O(2^n), so for higher dimensions it is incredibly slow and memory consuming.

## Usage
For full usage documentation, generate Javadoc from Noise.java (or just look at the code comments).

- `Noise(n)`: Class constructor for noise in n-dimensional space,
- `evaluate(p)`: Evaluate the noise value at the point p.
