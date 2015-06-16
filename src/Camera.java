/**
 * This class handles camera operations such as zooming in/out of the mandelbrot set, as well as panning left/right/north/south
 *
 * @author Samson Svendsen
 * @author Eivind Kristoffersen
 * @author Simen Aakhus
 * @author Anders Kristiansen
 */
public class Camera {

    private Setting setting;
    private float zoomFactor = 0.95f;
    private float currentZoom = 1;
    private float scrollSpeed = 0.02f;

    /**
     * This constructor initializes the camera class with the  {@link Setting} object from the  {@link Mandelbrot} class.
     * @param setting used to alter positional information of the Mandelbrot set.
     */
    public Camera(Setting setting){
        this.setting = setting;
    }

    /**
     * This class zooms in on the Mandelbrot set
     */
    public void zoom(){

        float width = setting.getWidth();
        float height = setting.getHeight();
        float newWidth = width * zoomFactor;
        float newHeight = height * zoomFactor;

        //Zooming in on the mandelbrot set
        setting.setHeight(newHeight);
        setting.setWidth(newWidth);

        currentZoom = currentZoom / zoomFactor;

        //Offset for adjusting changes in width and height
        float dx = (newWidth - width)/2;
        float dy = (newHeight - height)/2;
        setting.setX(setting.getX() - dx);
        setting.setY(setting.getY() - dy);
    }

    /**
     * This class zooms out of the Mandelbrot set
     */
    public void unzoom(){

        float width = setting.getWidth();
        float height = setting.getHeight();
        float newWidth = width / zoomFactor;
        float newHeight = height / zoomFactor;

        //Zooming in on the mandelbrot set
        setting.setHeight(newHeight);
        setting.setWidth(newWidth);

        currentZoom = currentZoom * zoomFactor;

        //Offset for adjusting changes in width and height
        float dx = (newWidth - width)/2;
        float dy = (newHeight - height)/2;
        setting.setX(setting.getX() - dx);
        setting.setY(setting.getY() - dy);
    }

    /**
     * Moves camera to the left.
     */
    public void left(){
        setting.setX(setting.getX() - (scrollSpeed * (1/currentZoom)));
    }

    /**
     * Moves camera to the right.
     */
    public void right(){
        setting.setX(setting.getX() + (scrollSpeed * (1/currentZoom)));

    }

    /**
     * Moves the camera north.
     */
    public void north(){
        setting.setY(setting.getY() - (scrollSpeed * (1/currentZoom)));
    }

    /**
     * Moves the camera south
     */
    public void south(){
        setting.setY(setting.getY() + (scrollSpeed * (1/currentZoom)));

    }

    /**
     * Gets the current zoom level
     * @return
     */
    public float getZoom(){
        return currentZoom;
    }

    /**
     * Gets the zoom factor (how fast the camera zooms).
     * @return
     */
    public float getZoomFactor(){
        return zoomFactor;
    }

    /**
     * Sets the zoom factor (how fast the camera zooms)
     * @param i value the zoom factor is set as.
     */
    public void setZoomFactor(float i){
        zoomFactor = i;
    }

    /**
     * Determines how fast the camera moves in the Mandelbrot set.
     * @param i determines the scroll speed
     */
    public void setScrollSpeed(float i){
        scrollSpeed = i;
    }
}
