import com.jogamp.opengl.GL2;
import java.util.Random;


/**
 * This class lets the user toggle between different color schemes for the Mandelbrot Set. Each color scheme has its own int value to identify it.
 *
 * @author Samson Svendsen
 * @author Eivind Kristoffersen
 * @author Simen Aakhus
 * @author Anders Kristiansen
 */
public class ColorSelector {

    private int colorScheme;
    private static final int COLORS = 5;
    private Random r;

    private float changer1 = 0;
    private int changeCount = 0;
    private boolean up = true;

    /**
     * Constructor to initialize the ColorSelector.
     */
    public ColorSelector(){
        colorScheme = 0;
        r = new Random();
    }

    /**
     * Returns the current colorScheme
     * @return returns the int value of the current color scheme
     */
    public int getColorScheme(){
        return colorScheme;
    }

    /**
     * Changes the color scheme to the next scheme (or resets to the first iff the user is on the last color scheme).
     */
    public void incrementColor(){
        if(colorScheme < COLORS) {
            colorScheme++;
        }
        else{
            colorScheme = 0;
        }
    }

    /**
     * Oscillates floats for the Mandelbrot color scheme that changes colors.
     */
    public void oscillate(){
        if(up) {
            if (changer1 <= 1.0f) {
                changer1 = changer1 + 0.02f;
            } else {
                up = false;
            }
        }
        else{
            if (changer1 >= 0) {
                changer1 = changer1 - 0.02f;
            } else {
                up = true;
                if(changeCount < 3) {
                    changeCount++;
                }else{
                    changeCount = 0;
                }
            }
        }
    }

    /**
     * Sets the color scheme by sending a three dimensional vector to the shader as a uniform variable.
     * @param gl Entry point to JOGL
     * @param shaderprogram The shaderprogram consisting of a vertex shader and a fragment shader.
     */
    public void selectColor(GL2 gl, int shaderprogram){

        if(colorScheme == 0) {
            int col1 = gl.glGetUniformLocation(shaderprogram, "col1");
            gl.glUniform3f(col1, 0.0f, 0.0f, 0.0f);
            int col2 = gl.glGetUniformLocation(shaderprogram, "col2");
            gl.glUniform3f(col2, 1.0f, 0.0f, 0.0f);
            int col3 = gl.glGetUniformLocation(shaderprogram, "col3");
            gl.glUniform3f(col3, 1.0f, 1.0f, 0.0f);
            int col4 = gl.glGetUniformLocation(shaderprogram, "col4");
            gl.glUniform3f(col4, 1.0f, 0.0f, 0.0f);
        }
        else if(colorScheme == 1){
            int col1 = gl.glGetUniformLocation(shaderprogram, "col1");
            gl.glUniform3f(col1, 0.0f, 0.1f, 0.0f);
            int col2 = gl.glGetUniformLocation(shaderprogram, "col2");
            gl.glUniform3f(col2, 0.0f, 1.0f, 0.0f);
            int col3 = gl.glGetUniformLocation(shaderprogram, "col3");
            gl.glUniform3f(col3, 0.0f, 0.5f, 1.0f);
            int col4 = gl.glGetUniformLocation(shaderprogram, "col4");
            gl.glUniform3f(col4, 0.0f, 0.2f, 0.0f);
        }
        else if(colorScheme == 2){
            int col1 = gl.glGetUniformLocation(shaderprogram, "col1");
            gl.glUniform3f(col1, 0.0f, 0.0f, 1.0f);
            int col2 = gl.glGetUniformLocation(shaderprogram, "col2");
            gl.glUniform3f(col2, 1.0f, 1.0f, 1.0f);
            int col3 = gl.glGetUniformLocation(shaderprogram, "col3");
            gl.glUniform3f(col3, 1.0f, 1.0f, 0.0f);
            int col4 = gl.glGetUniformLocation(shaderprogram, "col4");
            gl.glUniform3f(col4, 1.0f, 0.0f, 0.0f);
        }
        else if(colorScheme == 3){
            int col1 = gl.glGetUniformLocation(shaderprogram, "col1");
            gl.glUniform3f(col1, 0.0f, 0.0f, 0.0f);
            int col2 = gl.glGetUniformLocation(shaderprogram, "col2");
            gl.glUniform3f(col2, 0.7f, 0.0f, 0.7f);
            int col3 = gl.glGetUniformLocation(shaderprogram, "col3");
            gl.glUniform3f(col3, 0.0f, 0.5f, 1.0f);
            int col4 = gl.glGetUniformLocation(shaderprogram, "col4");
            gl.glUniform3f(col4, 0.0f, 0.2f, 0.0f);
        }
        else if(colorScheme == 4){
            int col1 = gl.glGetUniformLocation(shaderprogram, "col1");
            gl.glUniform3f(col1, 1.0f, 0.2f, 0.0f);
            int col2 = gl.glGetUniformLocation(shaderprogram, "col2");
            gl.glUniform3f(col2, 1.0f, 1.0f, 0.0f);
            int col3 = gl.glGetUniformLocation(shaderprogram, "col3");
            gl.glUniform3f(col3, 0.0f, 0.0f, 1.0f);
            int col4 = gl.glGetUniformLocation(shaderprogram, "col4");
            gl.glUniform3f(col4, 0.0f, 0.2f, 0.0f);
        }
        else{

            oscillate();

            int col1 = gl.glGetUniformLocation(shaderprogram, "col1");
            gl.glUniform3f(col1, 0.0f, 0.0f, 0.0f);
            int col2 = gl.glGetUniformLocation(shaderprogram, "col2");
            gl.glUniform3f(col2, 1.0f, 0.0f, 0.0f);
            int col3 = gl.glGetUniformLocation(shaderprogram, "col3");
            if(changeCount == 0) {
                gl.glUniform3f(col3, 1.0f, changer1, 0.0f);
            }
            else if(changeCount == 1){
                gl.glUniform3f(col3, 1.0f, 1.0f, changer1);

            }
            else{
                gl.glUniform3f(col3, changer1, 1.0f, 0.0f);

            }
            int col4 = gl.glGetUniformLocation(shaderprogram, "col4");
            gl.glUniform3f(col4, 1.0f, 0.0f, 0.0f);
        }
    }

}
