import javax.media.opengl.GL2;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ColorSelector {

    private int colorScheme;
    private static final int COLORS = 5;
    private Random r;

    private float changer1 = 0;
    private int changeCount = 0;
    private boolean up = true;


    public ColorSelector(){
        colorScheme = 0;
        r = new Random();
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
            /*
            int col1 = gl.glGetUniformLocation(shaderprogram, "col1");
            gl.glUniform3f(col1, r.nextFloat(), r.nextFloat(), r.nextFloat());
            int col2 = gl.glGetUniformLocation(shaderprogram, "col2");
            gl.glUniform3f(col2, r.nextFloat(), r.nextFloat(), r.nextFloat());
            int col3 = gl.glGetUniformLocation(shaderprogram, "col3");
            gl.glUniform3f(col3, r.nextFloat(), r.nextFloat(), r.nextFloat());
            int col4 = gl.glGetUniformLocation(shaderprogram, "col4");
            gl.glUniform3f(col4, r.nextFloat(), r.nextFloat(), r.nextFloat());
            */
        }
    }

}
