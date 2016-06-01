package unbboolean.solids;

import com.sun.j3d.utils.geometry.GeometryInfo;
import com.sun.j3d.utils.geometry.NormalGenerator;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.StringTokenizer;
import javax.media.j3d.Shape3D;
import javax.vecmath.Color3f;
import javax.vecmath.Matrix4d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

/**
 * Class representing a 3D solid.
 *
 * @author Danilo Balby Silva Castanheira (danbalby@yahoo.com) (modified)
 */
public class Solid extends Shape3D {

    /**
     * array of indices for the vertices from the 'vertices' attribute
     */
    protected int[] indices;
    /**
     * array of points defining the solid's vertices
     */
    protected Point3d[] vertices;
    /**
     * array of color defining the vertices colors
     */
    protected Color3f[] colors;
    protected Vector3d rotation;
    protected Vector3d translation;
    //protected Vector3d scale;
    private SolidTransformationListener listener;

    //--------------------------------CONSTRUCTORS----------------------------------//
    /**
     * Constructs an empty solid.
     */
    public Solid() {
        super();
        setInitialFeatures();
        rotation = new Vector3d();
        translation = new Vector3d();
    }

    /**
     * Construct a solid based on data arrays. An exception may occur in the
     * case of abnormal arrays (indices making references to inexistent
     * vertices, there are less colors than vertices...)
     *
     * @param vertices array of points defining the solid vertices
     * @param indices array of indices for a array of vertices
     * @param colors array of colors defining the vertices colors
     */
    public Solid(Point3d[] vertices, int[] indices, Color3f[] colors) {
        this();
        setData(vertices, indices, colors);
    }

    /**
     * Constructs a solid based on a coordinates file. It contains vertices and
     * indices, and its format is like this:
     *
     * <br><br>4 <br>0 -5.00000000000000E-0001 -5.00000000000000E-0001
     * -5.00000000000000E-0001 <br>1 5.00000000000000E-0001
     * -5.00000000000000E-0001 -5.00000000000000E-0001 <br>2
     * -5.00000000000000E-0001 5.00000000000000E-0001 -5.00000000000000E-0001
     * <br>3 5.00000000000000E-0001 5.00000000000000E-0001
     * -5.00000000000000E-0001
     *
     * <br><br>2 <br>0 0 2 3 <br>1 3 1 0
     *
     * @param solidFile file containing the solid coordinates
     * @param color solid color
     */
    public Solid(File solidFile, Color3f color) {
        this();
        loadCoordinateFile(solidFile, color);
    }

    /**
     * Sets the initial features common to all constructors
     */
    private void setInitialFeatures() {
        vertices = new Point3d[0];
        colors = new Color3f[0];
        indices = new int[0];

        setCapability(Shape3D.ALLOW_GEOMETRY_WRITE);
        setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
        setCapability(Shape3D.ALLOW_APPEARANCE_READ);
    }

    //---------------------------------------GETS-----------------------------------//
    /**
     * Gets the solid vertices
     *
     * @return solid vertices
     */
    public Point3d[] getVertices() {
        Point3d[] newVertices = new Point3d[vertices.length];
        for (int i = 0; i < newVertices.length; i++) {
            newVertices[i] = (Point3d) vertices[i].clone();
        }
        return newVertices;
    }

    /**
     * Gets the solid indices for its vertices
     *
     * @return solid indices for its vertices
     */
    public int[] getIndices() {
        int[] newIndices = new int[indices.length];
        System.arraycopy(indices, 0, newIndices, 0, indices.length);
        return newIndices;
    }

    /**
     * Gets the vertices colors
     *
     * @return vertices colors
     */
    public Color3f[] getColors() {
        Color3f[] newColors = new Color3f[colors.length];
        System.arraycopy(colors, 0, newColors, 0, newColors.length);
        return newColors;
    }

    /**
     * Gets if the solid is empty (without any vertex)
     *
     * @return true if the solid is empty, false otherwise
     */
    public boolean isEmpty() {
        if (indices.length == 0) {
            return true;
        } else {
            return false;
        }
    }

    //---------------------------------------SETS-----------------------------------//
    /**
     * Sets the solid data. Each vertex may have a different color. An exception
     * may occur in the case of abnormal arrays (e.g., indices making references
     * to inexistent vertices, there are less colors than vertices...)
     *
     * @param vertices array of points defining the solid vertices
     * @param indices array of indices for a array of vertices
     * @param colors array of colors defining the vertices colors
     */
    public final void setData(Point3d[] vertices, int[] indices, Color3f[] colors) {
        this.vertices = new Point3d[vertices.length];
        this.colors = new Color3f[colors.length];
        this.indices = new int[indices.length];
        if (indices.length != 0) {
            for (int i = 0; i < vertices.length; i++) {
                this.vertices[i] = (Point3d) vertices[i].clone();
                this.colors[i] = colors[i];
            }
            System.arraycopy(indices, 0, this.indices, 0, indices.length);

            defineGeometry();
        }
    }

    /**
     * Sets the solid data. Defines the same color to all the vertices. An
     * exception may may occur in the case of abnormal arrays (e.g., indices
     * making references to inexistent vertices...)
     *
     * @param vertices array of points defining the solid vertices
     * @param indices array of indices for a array of vertices
     * @param color the color of the vertices (the solid color)
     */
    public void setData(Point3d[] vertices, int[] indices, Color3f color) {
        Color3f[] colors = new Color3f[vertices.length];
        Arrays.fill(colors, color);
        setData(vertices, indices, colors);
    }

    public void setTransformationListener(SolidTransformationListener L) {
        listener = L;
    }

    // ----------------RELATIVE_GEOMETRICAL_TRANSFORMATIONS---------------------//
    /**
     * Applies a translation into a solid
     *
     * @param dx translation on the x axis
     * @param dy translation on the y axis
     * @param dz translation on the z axis
     */
    public void translate(double dx, double dy, double dz) {
        if (dx == 0 && dy == 0 && dz == 0) {
            return;
        }

        translation.add(new Vector3d(dx, dy, dz));
        Point3d theVertex;

        for (int i = 0; i < vertices.length; i++) {
            theVertex = vertices[i];
            theVertex.x += dx;
            theVertex.y += dy;
            theVertex.z += dz;
        }

        if (listener != null) {
            listener.translateSolid(this);
        }
        defineGeometry();
    }

    /**
     * Applies a rotation into a solid
     *
     * @param dx rotation on the x axis
     * @param dy rotation on the y axis
     * @param dz rotation on the z axis
     */
    public void rotate(double dx, double dy, double dz) {
        if (dx == 0 && dy == 0 && dz == 0) {
            return;
        }

        double r, pi = Math.PI, pi2 = pi*2;
        r = rotation.x + dx;
        rotation.x = (r >= 0 ? (r < pi ? r : r - pi2) : (r > -pi ? r : r + pi2));
        r = rotation.y + dy;
        rotation.y = (r >= 0 ? (r < pi ? r : r - pi2) : (r > -pi ? r : r + pi2));
        r = rotation.z + dz;
        rotation.z = (r >= 0 ? (r < pi ? r : r - pi2) : (r > -pi ? r : r + pi2));

        Matrix4d rX = new Matrix4d();
        Matrix4d rY = new Matrix4d();
        Matrix4d rZ = new Matrix4d();
        rX.rotX(dx);
        rY.rotY(dy);
        rZ.rotZ(dz);

        // get mean
        Point3d mean = getMean();
        Point3d theVertex;

        for (int i = 0; i < vertices.length; i++) {
            theVertex = vertices[i];
            theVertex.x -= mean.x;
            theVertex.y -= mean.y;
            theVertex.z -= mean.z;

            //x rotation
            if (dx != 0) {
                rX.transform(theVertex);
            }

            //y rotation
            if (dy != 0) {
                rY.transform(theVertex);
            }

            //z rotation
            if (dz != 0) {
                rZ.transform(theVertex);
            }
            theVertex.x += mean.x;
            theVertex.y += mean.y;
            theVertex.z += mean.z;
        }

        if (listener != null) {
            listener.rotateSolid(this);
        }
        defineGeometry();
    }

    /**
     * Applies a scale changing into the solid
     *
     * @param dx scale changing for the x axis
     * @param dy scale changing for the y axis
     * @param dz scale changing for the z axis
     */
    public void scale(double dx, double dy, double dz) {
        for (int i = 0; i < vertices.length; i++) {
            vertices[i].x *= dx;
            vertices[i].y *= dy;
            vertices[i].z *= dz;
        }

        defineGeometry();
    }

    // ---------------ABSOLUTE_GEOMETRICAL_TRANSFORMATIONS--------------------//
    /**
     * @param rotation the rotation to set in radians
     */
    public void setRotation(Vector3d rotation) {
        rotation.sub(this.rotation);
        rotate(rotation.x, rotation.y, rotation.z);
    }

    /**
     * @param translation the translation to set
     */
    public void setTranslation(Vector3d translation) {
        translation.sub(this.translation);
        translate(translation.x, translation.y, translation.z);
    }

    /**
     * @return the rotation in radians
     */
    public Vector3d getRotation() {
        return rotation;
    }

    /**
     * @return the translation
     */
    public Vector3d getTranslation() {
        return translation;
    }

    //-----------------------------------PRIVATES--------------------------------//
    /**
     * Creates a geometry based on the indexes and vertices set for the solid
     */
    protected final void defineGeometry() {
        GeometryInfo gi = new GeometryInfo(GeometryInfo.TRIANGLE_ARRAY);
        gi.setCoordinateIndices(indices);
        gi.setCoordinates(vertices);
        NormalGenerator ng = new NormalGenerator();
        ng.generateNormals(gi);

        gi.setColors(colors);
        gi.setColorIndices(indices);
        gi.recomputeIndices();

        setGeometry(gi.getIndexedGeometryArray());
    }

    /**
     * Loads a coordinates file, setting vertices and indices
     *
     * @param solidFile file used to create the solid
     * @param color solid color
     */
    protected final void loadCoordinateFile(File solidFile, Color3f color) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(solidFile));
            // TODO Check
            String line = reader.readLine();
            int numVertices = Integer.parseInt(line);
            vertices = new Point3d[numVertices];

            StringTokenizer tokens;

            for (int i = 0; i < numVertices; i++) {
                line = reader.readLine();
                tokens = new StringTokenizer(line);
                tokens.nextToken();
                vertices[i] = new Point3d(Double.parseDouble(tokens.nextToken()), Double.parseDouble(tokens.nextToken()), Double.parseDouble(tokens.nextToken()));
            }

            reader.readLine();

            line = reader.readLine();
            int numTriangles = Integer.parseInt(line);
            indices = new int[numTriangles * 3];

            for (int i = 0, j = 0; i < numTriangles * 3; i = i + 3, j++) {
                line = reader.readLine();
                tokens = new StringTokenizer(line);
                tokens.nextToken();
                indices[i] = Integer.parseInt(tokens.nextToken());
                indices[i + 1] = Integer.parseInt(tokens.nextToken());
                indices[i + 2] = Integer.parseInt(tokens.nextToken());
            }

            colors = new Color3f[vertices.length];
            Arrays.fill(colors, color);

            defineGeometry();
        } catch (IOException e) {
            System.out.println("invalid file!");
            e.printStackTrace();
        }
    }

    /**
     * Gets the solid mean
     *
     * @return point representing the mean
     */
    protected Point3d getMean() {
        Point3d mean = new Point3d();
        for (int i = 0; i < vertices.length; i++) {
            mean.x += vertices[i].x;
            mean.y += vertices[i].y;
            mean.z += vertices[i].z;
        }
        mean.x /= vertices.length;
        mean.y /= vertices.length;
        mean.z /= vertices.length;

        return mean;
    }
}