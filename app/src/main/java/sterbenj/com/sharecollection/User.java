package sterbenj.com.sharecollection;

import cn.bmob.v3.BmobUser;

/**
 * 这些代码是 83405 在 2018/7/22 XJB敲的.
 */
public class User extends BmobUser {
    private Byte[] icon;

    public Byte[] getIcon() {
        return icon;
    }

    public void setIcon(Byte[] icon) {
        this.icon = icon;
    }
}
