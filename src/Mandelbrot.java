import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.FPSAnimator;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.*;
import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.Set;

/**
 * This class is where all the OpenGL is rendered to the screen. It implements the GLEventListener interface from jogamp, and KeyListener for handeling user inputs.
 * It also uses Java swing, and JFrame to display the Mandelbrot set to the screen.
 * @author Samson Svendsen
 * @author Eivind Kristoffersen
 * @author Simen Aakhus
 * @author Anders Kristiansen
 */
public class Mandelbrot extends JFrame implements GLEventListener, KeyListener{

    private int vertexShaderProgram;
    private int fragmentShaderProgram;
    private int shaderprogram;
    private int frameCount;
    private Setting settings;
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
    private JLabel res;
    private JLabel recording;
    private JSlider controlSlider = new JSlider(JSlider.HORIZONTAL, 85, 99, 95);
    private JComboBox iterations = new JComboBox(new Object[]{32,64,128,256,512,1024,2048,4096});
    private JComboBox size = new JComboBox(new Object[]{700, 800, 900,1000});

    private ColorSelector colors;

    /**
     * This constructor initiates the class with all the necessary variables and java swing components.
     */
    public Mandelbrot() {
        super("MandelbrotViewer");
        settings = new Setting();
        camera = new Camera(settings);
        colors = new ColorSelector();
        setLayout(new BorderLayout());
        getContentPane().add(subPanel, BorderLayout.SOUTH);
        recording = new JLabel("Recoring: off |" );
        controlPanel.add(recording);
        controlPanel.add(new JLabel("x - zoom in | z - zoom out | arrow keys - move | c - change color scheme | v - toggle color smoothing"));
        zoom = new JLabel("Zoom: " + 1 + "X");
        fps = new JLabel("FPS: ");
        iterationLimit = new JLabel("Iteration limit: ");
        controlSpeed = new JLabel("" + camera.getZoomFactor());
        res = new JLabel("Resolution: ");

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
        size.setSelectedItem(1000);
        infoPanel.add(res);
        infoPanel.add(size);
        iterations.setSelectedItem(128);
        settings.setIterations((int) iterations.getSelectedItem());

        iterations.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                settings.setIterations((int) iterations.getSelectedItem());
            }
        });

        size.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int i = (int) size.getSelectedItem();
                setSize(new Dimension(i, i));
                subPanel.setPreferredSize(new Dimension(i, 100));
            }
        });

        subPanel.add(infoPanel);
        subPanel.add(controlPanel);

        GLProfile profile = GLProfile.get(GLProfile.GL2);
        GLCapabilities capabilities = new GLCapabilities(profile);

        GLCanvas canvas = new GLCanvas(capabilities);
        canvas.addGLEventListener(this);

        this.setName("MandelbrotViewer");
        this.getContentPane().add(canvas);
        this.setSize(1000, 1000);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
        this.setResizable(false);
        canvas.requestFocusInWindow();
        canvas.addKeyListener(this);
        frameCount = 0;

        animator = new FPSAnimator(60);
        animator.add(canvas);
        animator.start();
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();

        try {
            attachShaders(gl);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void dispose(GLAutoDrawable glAutoDrawable) {

    }

    /**
     * Loads the shader from the shader file
     * @param name the name of the shader file
     * @return returns a string array
     */
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

    /**
     * Attaches the shaders to OpenGL
     * @param gl Entry point to JOGL
     * @throws Exception
     */
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
        gl.glUseProgram(shaderprogram);

    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {

        GL2 gl = drawable.getGL().getGL2();
        GLU glu = new GLU();

        gl.glViewport(0, 0, width, height);
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        glu.gluOrtho2D(0, 1, 0, 1);
        gl.glMatrixMode(GL2.GL_MODELVIEW);
    }

    @Override
    //This method is loosely based off of Morten Nobel's work (2010, Feb 23),http://blog.nobel-joergensen.com/2010/02/23/real-time-mandelbrot-in-java-%E2%80%93-part-2-jogl/
    public void display(GLAutoDrawable drawable) {

        GL2 gl = drawable.getGL().getGL2();

        updateUniformVars(gl);

        gl.glLoadIdentity();

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
        gl.glEnd();
        gl.glFlush();

        zoom.setText("Zoom: " + Math.round(camera.getZoom()) + "X | ");
        fps.setText("FPS: " + animator.getFPS() + " | Adjust control speed: ");
        controlSpeed.setText("" + camera.getZoomFactor());
        iterationLimit.setText("Iteration limit: ");

        boolean captureStatus = settings.getCaptureStatus();

        if(captureStatus){
            recording.setText("s - Recording: on | ");

            //Limits picture writing to every 20th frame
            if(frameCount % 20 == 0) {
                BufferedImage snapshot = snapImage(gl, (int) size.getSelectedItem(), (int) size.getSelectedItem() - 100);
                this.getClass().getProtectionDomain().getCodeSource().getLocation();
                File outputfile = new File("snap//image" + settings.getImageCount() + ".png");
                settings.incrementImageCount();
                try {
                    ImageIO.write(snapshot, "png", outputfile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        else{
            recording.setText("s - Recording: off | ");
        }

        frameCount++;
    }

    /**
     * Takes a snapshot of the screen, and returns it as a buffered image.
     * @param gl Entry point to JOGL
     * @param width width of the screen
     * @param height height of the screen
     * @return returns a buffered image of the screen.
     */
    public BufferedImage snapImage(GL2 gl, int width, int height) {

        gl.glReadBuffer(GL.GL_FRONT);

        ByteBuffer byteBuffer = ByteBuffer.allocate(3 * width * height);
        gl.glReadPixels(0, 0, width, height, GL2.GL_BGR, GL.GL_BYTE, byteBuffer);

        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        int[] bd = ((DataBufferInt) bufferedImage.getRaster().getDataBuffer()).getData();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int b = 2 * byteBuffer.get();
                int g = 2 * byteBuffer.get();
                int r = 2 * byteBuffer.get();

                bd[(height - y - 1) * width + x] = (r << 16) | (g << 8) | b | 0xFF000000;
            }
        }

        return bufferedImage;
    }

    /**
     * Updates all the uniform variables needed for the fragment shader.
     * @param gl entry point to JOGL
     */
    private void updateUniformVars(GL2 gl) {

        int mandel_x = gl.glGetUniformLocation(shaderprogram, "mandel_x");
        gl.glUniform1f(mandel_x, settings.getX());

        int mandel_y = gl.glGetUniformLocation(shaderprogram, "mandel_y");
        gl.glUniform1f(mandel_y, settings.getY());

        int mandel_width = gl.glGetUniformLocation(shaderprogram, "mandel_width");
        gl.glUniform1f(mandel_width, settings.getWidth());

        int mandel_height = gl.glGetUniformLocation(shaderprogram, "mandel_height");
        gl.glUniform1f(mandel_height, settings.getHeight());

        int mandel_iterations = gl.glGetUniformLocation(shaderprogram, "mandel_iterations");
        gl.glUniform1f(mandel_iterations, settings.getIterations());

        int color_smoothing = gl.glGetUniformLocation(shaderprogram, "color_smoothing");
        gl.glUniform1f(color_smoothing, settings.getColorSmoothing());

        //Selecting color
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
                if(key == KeyEvent.VK_S){
                    settings.toggleCaptureStatus();
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
            if(key == KeyEvent.VK_S){
                settings.toggleCaptureStatus();
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent keyEvent) {

        pressedKey.remove(keyEvent.getExtendedKeyCode());

    }
}
