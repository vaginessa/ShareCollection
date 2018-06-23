package sterbenj.com.sharecollection;


import org.litepal.crud.LitePalSupport;

/**
 * XJB Created by 野良人 on 2018/6/18.
 */
public class mApp extends LitePalSupport {
    private long id;
    private String Name;
    private String PackageName;
    private byte[] Icon;

    public mApp(String Name, String PackageName, byte[] Icon){
        this.Name = Name;
        this.PackageName = PackageName;
        this.Icon = Icon;
    }

    public void setPackageName(String packageName) {
        PackageName = packageName;
    }

    public void setIcon(byte[] icon) {
        Icon = icon;
    }

    public void setName(String name) {
        Name = name;
    }

    public long getId() {
        return id;
    }

    public String getPackageName() {
        return PackageName;
    }

    public String getName() {
        return Name;
    }

    public byte[] getIcon() {
        return Icon;
    }
}
