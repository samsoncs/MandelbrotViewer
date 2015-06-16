/**
 * This class is used to store all the information about the Mandelbrot set, such as positional information, iteration limit etc.
 *
 * @author Samson Svendsen
 * @author Eivind Kristoffersen
 * @author Simen Aakhus
 * @author Anders Kristiansen
 */
public class Setting {
    private float x = -2;
    private float y = -2;
    private float width = 4;
    private float height = 4;
    private int colorSmoothing;
    private boolean capture = false;
    private int imageCount = 0;
    private int iterations = 128;


    public Setting(){
        colorSmoothing = 1;
    }

    public float getX() {
        return x;
    }

    public boolean getCaptureStatus(){
        return capture;
    }

    /**
     * Toggles the screencapturing on/off
     */
    public void toggleCaptureStatus(){
        if(capture){
            capture = false;
        }
        else{
            imageCount = 0;
            capture = true;
        }
    }

    /**
     * Gets the number of images taken.
     * @return returns the number of images taken at that given point.
     */
    public int getImageCount(){
        return imageCount;
    }

    /**
     * Increments the image count.
     */
    public void incrementImageCount(){
        imageCount++;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public int getIterations() {
        return iterations;
    }

    public int getColorSmoothing(){
        return colorSmoothing;
    }

    /**
     * Toggles the color smoothing on/off
     */
    public void toggleColorSmoothing(){
        if(colorSmoothing == 1){
            colorSmoothing = 0;
        }else{
            colorSmoothing = 1;
        }
    }

    public void setIterations(int iterations) {
        this.iterations = iterations;
    }
}
