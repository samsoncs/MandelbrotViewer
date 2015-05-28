/**
 * Created by samson on 5/26/15.
 */
public class Camera {

    private MandelbrotSetting setting;
    private float zoomFactor = 0.95f;
    private float currentZoom = 1;
    private float scrollSpeed = 0.02f;


    public Camera(MandelbrotSetting setting){
        this.setting = setting;
    }

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

    public void left(){
        setting.setX(setting.getX() - (scrollSpeed * (1/currentZoom)));
    }

    public void right(){
        setting.setX(setting.getX() + (scrollSpeed * (1/currentZoom)));

    }

    public void north(){
        setting.setY(setting.getY() - (scrollSpeed * (1/currentZoom)));
    }

    public void south(){
        setting.setY(setting.getY() + (scrollSpeed * (1/currentZoom)));

    }

    public float getZoom(){
        return currentZoom;
    }

    public float getZoomFactor(){
        return zoomFactor;
    }

    public void setZoomFactor(float i){
        zoomFactor = i;
    }

    public void setScrollSpeed(float i){
        scrollSpeed = i;
    }
}
