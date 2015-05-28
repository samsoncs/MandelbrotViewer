import javax.media.opengl.GL2;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by samson on 5/26/15.
 */
public class ColorSelector {

    private int colorScheme;
    private static final int COLORS = 2;

    public ColorSelector(){
        colorScheme = 0;
    }

    public int getColorScheme(){
        return colorScheme;
    }

    public void incrementColor(){
        if(colorScheme < COLORS) {
            colorScheme++;
        }
        else{
            colorScheme = 0;
        }
    }

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
        else{
            int col1 = gl.glGetUniformLocation(shaderprogram, "col1");
            gl.glUniform3f(col1, 0.0f, 0.0f, 1.0f);
            int col2 = gl.glGetUniformLocation(shaderprogram, "col2");
            gl.glUniform3f(col2, 1.0f, 1.0f, 1.0f);
            int col3 = gl.glGetUniformLocation(shaderprogram, "col3");
            gl.glUniform3f(col3, 1.0f, 1.0f, 0.0f);
            int col4 = gl.glGetUniformLocation(shaderprogram, "col4");
            gl.glUniform3f(col4, 1.0f, 0.0f, 0.0f);
        }
    }

}
