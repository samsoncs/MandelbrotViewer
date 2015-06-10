
public class MandelbrotSetting {
    private float x = -2;
    private float y = -2;
    private float width = 4;
    private float height = 4;
    private int iterations = 128;
    private int colorSmoothing;
    private boolean capture = false;
    private int imageCount = 0;


    public MandelbrotSetting(){
        colorSmoothing = 1;
    }

    public float getX() {
        return x;
    }

    public boolean getCaptureStatus(){
        return capture;
    }

    public void toggleCaptureStatus(){
        if(capture){
            capture = false;
        }
        else{
            imageCount = 0;
            capture = true;
        }
    }

    public int getImageCount(){
        return imageCount;
    }

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
