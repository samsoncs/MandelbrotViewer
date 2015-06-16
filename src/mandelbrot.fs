uniform float mandel_x;
uniform float mandel_y;
uniform float mandel_width;
uniform float mandel_height;
uniform float mandel_iterations;
uniform float color_smoothing;
uniform vec3 col1;
uniform vec3 col2;
uniform vec3 col3;
uniform vec3 col4;


//This shader is based off of Morten Nobel's work (2010, Feb 23),http://blog.nobel-joergensen.com/2010/02/23/real-time-mandelbrot-in-java-%E2%80%93-part-2-jogl/
//and modified with functionality for color smoothing, and letting the user select the color scheme.
float calculateMandelbrotIterations(float x, float y) {
    float xx = 0.0;
    float yy = 0.0;
    float iter = 0.0;
    while (xx * xx + yy * yy <= 4.0 && iter<mandel_iterations) {
        float temp = xx*xx - yy*yy + x;
        yy = 2.0*xx*yy + y;
        xx = temp;
        iter ++;
    }

    if ( iter < mandel_iterations) {
        if(color_smoothing == 1){
        //To reduce the errors where the colors do not blend, we do some
        //extra iterations, so that the coloring is smooth.
        for(int i = 0; i < 3; i++){
            float temp = xx*xx - yy*yy + x;
                yy = 2.0*xx*yy + y;
                xx = temp;
                iter ++;
        }
        float zn = xx*xx + yy*yy;
        //To avoid calculating the sqrt(xx*xx + yy*yy) we can just multiply
        //log(zn) by 0.5.
        float mu = iter - log(log(zn)*0.5)/log(2);
        iter = mu;
        }
    }

    return iter;
}

const float colorResolution = 16.0; // how many iterations the first color band should use (2nd use the double amount)
 
vec3 getColorByIndex(float index){
    float i = mod(index,4.0);
    if (i<0.5){
        return col1;
    }
    if (i<1.5){
        return col2;
    }
    if (i<2.5){
        return col3;
    }
    return col4;
}
 
vec4 getColor(float iterations) {
    if (iterations==mandel_iterations){
        return vec4(0.0,0.0,0.0,1.0);
    }
    float colorIndex = 0.0;
    float iterationsFloat = iterations;
    float colorRes = colorResolution;
    while (iterationsFloat>colorRes){
        iterationsFloat -= colorRes;
        colorRes = colorRes*2.0;
        colorIndex ++;
    }
    float fraction = iterationsFloat/colorRes;
    vec3 from = getColorByIndex(colorIndex);
    vec3 to = getColorByIndex(colorIndex+1.0);
    //Linear interpolation
    vec3 res = mix(from,to,fraction);
    return vec4(res.x,res.y,res.z,1.0);
}
 
void main()
{
    float x = mandel_x+gl_TexCoord[0].x*mandel_width;
    float y = mandel_y+gl_TexCoord[0].y*mandel_height;
    float iterations = calculateMandelbrotIterations(x,y);

    gl_FragColor = getColor(iterations);
}