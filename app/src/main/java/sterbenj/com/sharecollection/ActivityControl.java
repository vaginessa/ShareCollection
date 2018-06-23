package sterbenj.com.sharecollection;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * XJB Created by 野良人 on 2018/4/17.
 */
public class ActivityControl {

    public static List<Activity> activities = new ArrayList<>();
    public static void addActivity(Activity activity){
        activities.add(activity);
    }
    public static void finishAll(){
        for (Activity activity : activities){
            activity.finish();
        }
        activities.clear();
    }
    public static void removeActivity(Activity activity){
        activities.remove(activity);
    }
    public static void recreateAll(){
        for (Activity activity : activities){
            activity.recreate();
        }
    }
}