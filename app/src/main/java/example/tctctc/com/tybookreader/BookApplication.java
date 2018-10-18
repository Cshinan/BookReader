package example.tctctc.com.tybookreader;

import android.app.Application;
import android.content.Context;

import example.tctctc.com.tybookreader.utils.FontUtils;

/**
 * Created by tctctc on 2017/3/15.
 */

public class BookApplication extends Application {

    private static Context sContext;

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = getApplicationContext();
        FontUtils.init(sContext);
    }

    public static Context getContext(){
        return sContext;
    }

}
