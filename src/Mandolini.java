import com.jogamp.opengl.util.FPSAnimator;

import javax.media.opengl.*;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.glu.GLU;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by samson on 5/25/15.
 */
public class Mandolini extends JFrame implements GLEventListener, KeyListener{

    private boolean updateUniformVars = true;
    private int vertexShaderProgram;
    private int fragmentShaderProgram;
    private int shaderprogram;
    private MandelbrotSetting settings;
    private FPSAnimator animator;
    private final Set<Integer> pressedKey = new HashSet<Integer>();
    private Camera camera;
    private JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    private JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    private JPanel subPanel = new JPanel(new GridLayout(0,1));
    private JLabel zoom;
    private JLabel controlSpeed;
    private JLabel fps;
    private JLabel iterationLimit;
    private JSlider controlSlider = new JSlider(JSlider.HORIZONTAL, 85, 99, 95);
    private JComboBox iterations = new JComboBox(new Object[]{32,64,128,256,512,1024,2048,4096});

    //private JSlider controlSlider = new JSlider(JSlider.HORIZONTAL, 85, 99, 95);


    private ColorSelector colors;

    public Mandolini() {
        super("MinimalTest");
        settings = new MandelbrotSetting();
        camera = new Camera(settings);
        colors = new ColorSelector();
        getContentPane().add(subPanel, BorderLayout.SOUTH);
        controlPanel.add(new JLabel("x - zoom in | z - zoom out | arrow keys - move | c - change color scheme | v - toggle color smoothing"));
        //controlPanel.add(new JLabel("BB"));
        zoom = new JLabel("Zoom: " + 1 + "X");
        fps = new JLabel("FPS: ");
        iterationLimit = new JLabel("Iteration limit: ");
        controlSpeed = new JLabel("" + camera.getZoomFactor());

        infoPanel.add(zoom);
        infoPanel.add(fps);

        controlSlider.setPaintTicks(true);
        controlSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent changeEvent) {
                camera.setZoomFactor(controlSlider.getValue() / 100f);
                camera.setScrollSpeed(1 - controlSlider.getValue() / 100f);
            }
        });
        infoPanel.add(controlSlider);
        infoPanel.add(controlSpeed);
        infoPanel.add(iterationLimit);
        infoPanel.add(iterations);
        settings.setIterations((int) iterations.getSelectedItem());

        iterations.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                settings.setIterations((int) iterations.getSelectedItem());
            }
        });

        subPanel.add(infoPanel);
        subPanel.add(controlPanel);

        GLProfile profile = GLProfile.get(GLProfile.GL2);
        GLCapabilities capabilities = new GLCapabilities(profile);

        GLCanvas canvas = new GLCanvas(capabilities);
        canvas.addGLEventListener(this);


        this.setName("MinimalTest");
        this.getContentPane().add(canvas);
        this.setSize(1000, 1000);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
        this.setResizable(false);
        canvas.requestFocusInWindow();
        canvas.addKeyListener(this);

        animator = new FPSAnimator(60);
        animator.add(canvas);
        animator.start();
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();

        // Enable VSync
        gl.setSwapInterval(1);
        gl.glShadeModel(GL2.GL_FLAT);
        try {
            attachShaders(gl);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void dispose(GLAutoDrawable glAutoDrawable) {

    }

    private String[] loadShaderSrc(String name){
        StringBuilder sb = new StringBuilder();
        try{
            InputStream is = getClass().getResourceAsStream(name);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = br.readLine())!=null){
                sb.append(line);
                sb.append('\n');
            }
            is.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return new String[]{sb.toString()};
    }

    private void attachShaders(GL2 gl) throws Exception {

        vertexShaderProgram = gl.glCreateShader(GL2.GL_VERTEX_SHADER);
        fragmentShaderProgram = gl.glCreateShader(GL2.GL_FRAGMENT_SHADER);

        String[] vsrc = loadShaderSrc("mandelbrot.vs");
        gl.glShaderSource(vertexShaderProgram, 1, vsrc, null, 0);
        gl.glCompileShader(vertexShaderProgram);

        String[] fsrc = loadShaderSrc("mandelbrot.fs");
        gl.glShaderSource(fragmentShaderProgram, 1, fsrc, null, 0);
        gl.glCompileShader(fragmentShaderProgram);

        shaderprogram = gl.glCreateProgram();
        gl.glAttachShader(shaderprogram, vertexShaderProgram);
        gl.glAttachShader(shaderprogram, fragmentShaderProgram);
        gl.glLinkProgram(shaderprogram);
        //gl.glValidateProgram(shaderprogram);
        /*
        IntBuffer intBuffer = IntBuffer.allocate(1);
        gl.glGetProgramiv(shaderprogram, GL2.GL_LINK_STATUS, intBuffer);
        if (intBuffer.get(0)!=1){
            gl.glGetProgramiv(shaderprogram, GL2.GL_INFO_LOG_LENGTH,intBuffer);
            int size = intBuffer.get(0);
            System.err.println("Program link error: ");
            if (size>0){
                ByteBuffer byteBuffer = ByteBuffer.allocate(size);
                gl.glGetProgramInfoLog(shaderprogram, size, intBuffer, byteBuffer);
                for (byte b:byteBuffer.array()){
                    System.err.print((char)b);
                }
            } else {
                System.out.println("Unknown error");
            }
            System.exit(1);
        }
        */
        gl.glUseProgram(shaderprogram);

    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        GL2 gl = drawable.getGL().getGL2();
        GLU glu = new GLU();

        if (height <= 0) { // avoid a divide by zero error!

            height = 1;
        }
        gl.glViewport(0, 0, width, height);
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        glu.gluOrtho2D(0, 1, 0, 1);
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();

        if (updateUniformVars){
            updateUniformVars(gl);
        }

        // Reset the current matrix to the "identity"
        gl.glLoadIdentity();

        // Draw A Quad
        gl.glBegin(GL2.GL_QUADS);
        {
            gl.glTexCoord2f(0.0f, 0.0f);
            gl.glVertex3f(0.0f, 1.0f, 1.0f);
            gl.glTexCoord2f(1.0f, 0.0f);
            gl.glVertex3f(1.0f, 1.0f, 1.0f);
            gl.glTexCoord2f(1.0f, 1.0f);
            gl.glVertex3f(1.0f, 0.0f, 1.0f);
            gl.glTexCoord2f(0.0f, 1.0f);
            gl.glVertex3f(0.0f, 0.0f, 1.0f);
        }
        // Done Drawing The Quad
        gl.glEnd();

        // Flush all drawing operations to the graphics card
        gl.glFlush();
        zoom.setText("Zoom: " + Math.round(camera.getZoom()) + "X | ");
        fps.setText("FPS: " + animator.getFPS() + " | Adjust control speed: ");
        controlSpeed.setText("" + camera.getZoomFactor());
        iterationLimit.setText("Iteration limit: ");
    }

    private void updateUniformVars(GL2 gl) {
        // get memory address of uniform shader variables
        int mandel_x = gl.glGetUniformLocation(shaderprogram, "mandel_x");
        int mandel_y = gl.glGetUniformLocation(shaderprogram, "mandel_y");
        int mandel_width = gl.glGetUniformLocation(shaderprogram, "mandel_width");
        int mandel_height = gl.glGetUniformLocation(shaderprogram, "mandel_height");
        int mandel_iterations = gl.glGetUniformLocation(shaderprogram, "mandel_iterations");
        int color_smoothing = gl.glGetUniformLocation(shaderprogram, "color_smoothing");
        assert(mandel_x!=-1);
        assert(mandel_y!=-1);
        assert(mandel_width!=-1);
        assert(mandel_height!=-1);
        assert(mandel_iterations!=-1);
        assert(color_smoothing!=1);
        // set uniform shader variables
        gl.glUniform1f(mandel_x, settings.getX());
        gl.glUniform1f(mandel_y, settings.getY());
        gl.glUniform1f(mandel_width, settings.getWidth());
        gl.glUniform1f(mandel_height, settings.getHeight());
        gl.glUniform1f(mandel_iterations, settings.getIterations());
        gl.glUniform1f(color_smoothing, settings.getColorSmoothing());

        //Selects correct color
        colors.selectColor(gl, shaderprogram);
    }

    @Override
    public void keyTyped(KeyEvent keyEvent) {

    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {

        pressedKey.add(keyEvent.getKeyCode());

        if(pressedKey.size() > 1){

            for(Integer key: pressedKey){
                if(key == KeyEvent.VK_Z){
                    camera.unzoom();
                }

                if(key == KeyEvent.VK_X){
                    camera.zoom();
                }

                if(key == KeyEvent.VK_UP){
                    camera.north();
                }

                if(key == KeyEvent.VK_DOWN){
                    camera.south();
                }

                if(key == KeyEvent.VK_LEFT){
                    camera.left();
                }

                if(key == KeyEvent.VK_RIGHT){
                    camera.right();
                }
                if(key == KeyEvent.VK_C){
                    colors.incrementColor();
                }
                if(key == KeyEvent.VK_V){
                    settings.toggleColorSmoothing();
                }
            }
        }
        else{
            int key = keyEvent.getKeyCode();

            if(key == KeyEvent.VK_Z){
                camera.unzoom();
            }

            if(key == KeyEvent.VK_X){
                camera.zoom();
            }

            if(key == KeyEvent.VK_UP){
                camera.north();
            }

            if(key == KeyEvent.VK_DOWN){
                camera.south();
            }

            if(key == KeyEvent.VK_LEFT){
                camera.left();
            }

            if(key == KeyEvent.VK_RIGHT){
                camera.right();
            }
            if(key == KeyEvent.VK_C){
                colors.incrementColor();
            }

            if(key == KeyEvent.VK_V){
                settings.toggleColorSmoothing();
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent keyEvent) {

        pressedKey.remove(keyEvent.getExtendedKeyCode());

    }
}
