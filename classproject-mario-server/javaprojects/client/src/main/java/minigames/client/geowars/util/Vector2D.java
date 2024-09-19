package minigames.client.geowars.util;

/**
 * Vector2D class.
 * Represents a 2D vector.
 * https://docs.oracle.com/cd/E17802_01/j2se/javase/technologies/desktop/java3d/forDevelopers/j3dapi/javax/vecmath/Vector2d.html
 */
public class Vector2D {
  public double x;
  public double y;

  // Constructs and initialises a Vector2D with the specified x and y coordinates.
  public Vector2D(double x, double y) {
    this.x = x;
    this.y = y;
  }

  // Constructs and initialises a Vector2D from the specified Vector2D.
  public Vector2D(Vector2D v) {
    this.x = v.x;
    this.y = v.y;
  }

  // Constructs and initialises a Vector2D from the specified array.
  public Vector2D(double[] v) {
    this.x = v[0];
    this.y = v[1];
  }

  // Constructs and initialises a Vector2D to (0,0).
  public Vector2D() {
    this.x = 0;
    this.y = 0;
  }

  // Sets the value of this vector to the specified x and y coordinates.
  public final void set(double x, double y) {
    this.x = x;
    this.y = y;
  }

  // Sets the value of this vector to the values in the specified array.
  public final void set(double[] v) {
    this.x = v[0];
    this.y = v[1];
  }

  // Sets the value of this vector to the value of the argument vector.
  public final void set(Vector2D v1) {
    this.x = v1.x;
    this.y = v1.y;
  }

  // Copies the value of the elements of this vector into the array v.
  public final void get(double[] v) {
    v[0] = this.x;
    v[1] = this.y;
  }

  // Returns the length of this vector.
  public final double length() {
    return Math.sqrt(this.x * this.x + this.y * this.y);
  }

  // Returns the squared length of this vector.
  public final double lengthSquared() {
    return this.x * this.x + this.y * this.y;
  }

  // Computes the dot product of this vector and vector v1.
  public final double dot(Vector2D v1) {
    return this.x * v1.x + this.y * v1.y;
  }

  // Adds the vectors v1 and v2 together and places the result into this vector.
  public final void add(Vector2D v1, Vector2D v2) {
    this.x = v1.x + v2.x;
    this.y = v1.y + v2.y;
  }

  // Adds the vector v1 to this vector.
  public final void add(Vector2D v1) {
    this.x += v1.x;
    this.y += v1.y;
  }

  // Subtracts vector v2 from vector v1 and places the result into this vector.
  public final void sub(Vector2D v1, Vector2D v2) {
    this.x = v1.x - v2.x;
    this.y = v1.y - v2.y;
  }

  // Subtracts vector v1 from this vector.
  public final void sub(Vector2D v1) {
    this.x -= v1.x;
    this.y -= v1.y;
  }

  // Sets this vector to the negation of vector v1.
  public final void negate(Vector2D v1) {
    this.x = -v1.x;
    this.y = -v1.y;
  }

  // Negates this vector in place.
  public final void negate() {
    this.x = -this.x;
    this.y = -this.y;
  }

  // Scales the vector v1 by the value s and places the result into this vector.
  public final void scale(double s, Vector2D v1) {
    this.x = s * v1.x;
    this.y = s * v1.y;
  }

  // Scales this vector by s.
  public final void scale(double s) {
    this.x *= s;
    this.y *= s;
  }

  // Sets the value of this vector to the normalization of vector v1.
  public final void normalize(Vector2D v1) {
    double norm;

    norm = 1.0 / Math.sqrt(v1.x * v1.x + v1.y * v1.y);
    this.scale(norm, v1);
  }

  // Normalizes this vector in place.
  public final void normalize() {
    double norm;

    norm = 1.0 / Math.sqrt(this.x * this.x + this.y * this.y);
    this.scale(norm);
  }

  // Returns the angle in radians between this vector and the vector parameter;
  // the return value is constrained to the range [0,PI].
  public final double angle(Vector2D v1) {
    return Math.acos(this.dot(v1) / (this.length() * v1.length()));
  }

  // Returns true if all of the data members of Vector2d v1 are equal to the
  // corresponding data members in this Vector2d.
  public boolean equals(Vector2D v1) {
    return this.x == v1.x && this.y == v1.y;
  }

  // Returns true if the Object o1 is of type Vector2d and all of the data members
  // of o1 are equal to the corresponding data members in this Vector2d.
  public boolean equals(Object o1) {
    if (o1 instanceof Vector2D) {
      Vector2D v1 = (Vector2D) o1;
      return this.x == v1.x && this.y == v1.y;
    }
    return false;
  }

  // Returns true if the L-infinite distance between this vector and vector v1 is
  // less than or equal to the epsilon parameter, otherwise returns false.
  public boolean epsilonEquals(Vector2D v1, double epsilon) {
    double linfinite = Math.max(Math.abs(this.x - v1.x), Math.abs(this.y - v1.y));
    return linfinite <= epsilon;
  }

  // Clamps this vector to the range [min, max].
  public final void clamp(double min, double max) {
    this.clampMax(max);
    this.clampMin(min);
  }

  // Clamps the minimum value of this vector to the min parameter.
  public final void clampMin(double min) {
    if (this.x < min) {
      this.x = min;
    }

    if (this.y < min) {
      this.y = min;
    }
  }

  // Clamps the maximum value of this vector to the max parameter.
  public final void clampMax(double max) {
    if (this.x > max) {
      this.x = max;
    }

    if (this.y > max) {
      this.y = max;
    }
  }

  // Sets the value of this vector to the absolute value of the specified vector.
  public final void absolute(Vector2D v1) {
    this.x = Math.abs(v1.x);
    this.y = Math.abs(v1.y);
  }

  // Sets the value of this vector to the absolute value of itself.
  public final void absolute() {
    this.x = Math.abs(this.x);
    this.y = Math.abs(this.y);
  }

  // Linearly interpolates between this vector and vector v1 and places the result
  // into this vector: this = (1-alpha)*this + alpha*v1.
  public final void interpolate(Vector2D v1, double alpha) {
    this.x = (1 - alpha) * this.x + alpha * v1.x;
    this.y = (1 - alpha) * this.y + alpha * v1.y;
  }

  // Linearly interpolates between vectors v1 and v2 and places the result into
  // this vector: this = (1-alpha)*v1 + alpha*v2.
  public final void interpolate(Vector2D v1, Vector2D v2, double alpha) {
    this.x = (1 - alpha) * v1.x + alpha * v2.x;
    this.y = (1 - alpha) * v1.y + alpha * v2.y;
  }

  // Returns a string that contains the values of this Vector2d.
  public String toString() {
    return "Vector2D: " + this.x + ", " + this.y;
  }

  // A definition for the 2-D Vector cross product.
  public static final double cross(Vector2D v1, Vector2D v2) {
    return (v1.x * v2.y) - (v1.y * v2.x);
  }
}
