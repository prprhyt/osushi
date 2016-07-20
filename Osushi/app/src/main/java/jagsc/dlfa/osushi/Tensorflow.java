package jagsc.dlfa.osushi;

/**
 * Created by NNM on 2016/06/30.
 */
public class Tensorflow {
    static {
        System.loadLibrary("tensorflow-jni");
    }
    public native String test();
}
