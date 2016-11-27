package will.neat.encog.gui;

/* Code for COMP261 Assignment
 * Author: pondy
 */

/** 3x4 array representing an affine transformation
 (= a 4x4 matrix in which the bottom row is always {0 0 0 1} )
 Note that this cannot be used for perspective projection tranformations
 since these require a non-0 bottom row.

 The class provides static methods to construct translation, scaling, and rotation
 matrices, and methods to multiply a translation by a vector or another matrix.
 */

public class Transform{

    private final double[][] values;

    /** Construct a Transformation given 3x4 array of elements */
    private Transform(double[][] v){
        if (v.length != 3 || v[0].length!=4)
            throw new IllegalArgumentException("Transform: Wrong size array for argument: "+ v.length + "," + v[0].length);
        else
            values = v;
    }

    /** Construct an identity Transformation */
    public static Transform identity(){
        return new Transform(new double[][]{{1.0f, 0.0f, 0.0f, 0.0f},
                {0.0f, 1.0f, 0.0f, 0.0f},
                {0.0f, 0.0f, 1.0f, 0.0f}});
    }

    /** Construct a translation Transformation based on a vector */
    public static Transform newTranslation(Vector3D tr){
        return newTranslation(tr.x, tr.y, tr.z);
    }

    /** Construct a translation Transformation given dx, dy, dz */
    public static Transform newTranslation(double tx, double ty, double tz){
        return new Transform(new double[][]{{1.0f, 0.0f, 0.0f, tx},
                {0.0f, 1.0f, 0.0f, ty},
                {0.0f, 0.0f, 1.0f, tz}});
    }

    /** Construct a scaling Transformation given values in a vector */
    public static Transform newScale(Vector3D sc){
        return newScale(sc.x, sc.y, sc.z);
    }
    /** Construct a scaling Transformation given sx, sy, sz */
    public static Transform newScale(double sx, double sy, double sz){
        return new Transform(new double[][]{{sx,  0.0f, 0.0f, 0.0f},
                {0.0f, sy,  0.0f, 0.0f},
                {0.0f, 0.0f, sz,  0.0f}});
    }

    /** Construct a rotation Transformation given angle around x axis */
    public static Transform newXRotation(double th){
        double sinth = (double)Math.sin(th);
        double costh = (double)Math.cos(th);
        return new Transform(new double[][]{{1.0f, 0.0f,  0.0f,  0.0f},
                {0.0f, costh,-sinth, 0.0f},
                {0.0f, sinth, costh, 0.0f}});
    }

    /** Construct a rotation Transformation given angle around y axis */
    public static Transform newYRotation(double th){
        double sinth = (double)Math.sin(th);
        double costh = (double)Math.cos(th);
        return new Transform(new double[][]{{costh, 0.0f, sinth, 0.0f},
                {0.0f,  1.0f, 0.0f,  0.0f},
                {-sinth, 0.0f, costh, 0.0f}});
    }

    /** Construct a rotation Transformation given angle around z axis */
    public static Transform newZRotation(double th){
        double sinth = (double)Math.sin(th);
        double costh = (double)Math.cos(th);
        return new Transform(new double[][]{{costh,-sinth, 0.0f, 0.0f},
                {sinth, costh, 0.0f, 0.0f},
                {0.0f,  0.0f,  1.0f, 0.0f}});
    }

    /* post multiply this transform by another (this * other) */
    public Transform compose(Transform other){
        double[][] ans = new double[3][4];
        for (int row=0; row<3; row++){
            for (int col=0; col<4; col++){
                for (int i=0; i<3; i++){
                    ans[row][col] +=this.values[row][i]*other.values[i][col];
                }
            }
            ans[row][3] +=this.values[row][3];
        }
        return new Transform(ans);
    }

    /* apply this transform to a vector*/
    public Vector3D multiply(Vector3D vect){
        if (values==null || values[0]==null || values[1]==null || values[2]==null){
            throw new IllegalStateException("Ill-formed transform");}
        if (vect==null){
            throw new IllegalArgumentException("multiply by null vector");}
        double x = values[0][3];
        double y = values[1][3];
        double z = values[2][3];
        x += values[0][0]*vect.x + values[0][1]*vect.y + values[0][2]*vect.z;
        y += values[1][0]*vect.x + values[1][1]*vect.y + values[1][2]*vect.z;
        z += values[2][0]*vect.x + values[2][1]*vect.y + values[2][2]*vect.z;
        return new Vector3D(x, y, z);
    }

    public String toString(){
        StringBuilder ans = new StringBuilder();
        for (int row=0; row<3; row++){
            for (int col=0; col<4; col++){
                ans.append(values[row][col]).append(' ');
            }
            ans.append('\n');
        }
        return ans.toString();
    }

}

