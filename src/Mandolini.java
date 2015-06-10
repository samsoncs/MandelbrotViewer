import com.jogamp.opengl.util.FPSAnimator;

import javax.imageio.ImageIO;
import javax.media.opengl.*;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.glu.GLU;
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

public class Mandolini extends JFrame implements GLEventListener, KeyListener, MouseListener{

    private int vertexShaderProgram;
    private int fragmentShaderProgram;
    private int shaderprogram;
    private int frameCount;
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
    private JLabel res;
    private JLabel recording;
    private JSlider controlSlider = new JSlider(JSlider.HORIZONTAL, 85, 99, 95);
    private JComboBox iterations = new JComboBox(new Object[]{32,64,128,256,512,1024,2048,4096});
    private JComboBox size = new JComboBox(new Object[]{700, 800, 900,1000});



    private ColorSelector colors;

    public Mandolini() {
        super("MandelbrotViewer");
        settings = new MandelbrotSetting();
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
        canvas.addMouseListener(this);
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
            recording.setText("Recording: on | ");
        }
        else{
            recording.setText("Recording: off | ");
        }

        if(captureStatus) {
            //Limits picture writing to every 20th frame
            if(frameCount % 20 == 0) {
                BufferedImage snapshot = toImage(gl, (int) size.getSelectedItem(), (int) size.getSelectedItem() - 100);
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

        frameCount++;
    }

    public BufferedImage toImage(GL2 gl, int w, int h) {

        gl.glReadBuffer(GL.GL_FRONT); // or GL.GL_BACK

        ByteBuffer glBB = ByteBuffer.allocate(3 * w * h);
        gl.glReadPixels(0, 0, w, h, GL2.GL_BGR, GL.GL_BYTE, glBB);

        BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        int[] bd = ((DataBufferInt) bi.getRaster().getDataBuffer()).getData();

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int b = 2 * glBB.get();
                int g = 2 * glBB.get();
                int r = 2 * glBB.get();

                bd[(h - y - 1) * w + x] = (r << 16) | (g << 8) | b | 0xFF000000;
            }
        }

        return bi;
    }

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

    @Override
    public void mouseClicked(MouseEvent mouseEvent) {

    }

    @Override
    public void mousePressed(MouseEvent mouseEvent) {
        int modifiers = mouseEvent.getModifiers();
        if ((modifiers & InputEvent.BUTTON1_MASK) == InputEvent.BUTTON1_MASK) {
            camera.zoom();
        }
        if ((modifiers & InputEvent.BUTTON3_MASK) == InputEvent.BUTTON3_MASK) {
            camera.unzoom();
        }

    }

    @Override
    public void mouseReleased(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseEntered(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseExited(MouseEvent mouseEvent) {

    }
}
