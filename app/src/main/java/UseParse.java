/**
 * Created by admin on 1/20/16.
 */

import com.parse.Parse;
import android.app.Application;

public class UseParse extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Parse.initialize(this);
    }
}
