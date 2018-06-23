package sterbenj.com.sharecollection;

import org.litepal.crud.LitePalSupport;

import java.io.Serializable;

/**
 * XJB Created by 野良人 on 2018/5/25.
 */
public class Category extends LitePalSupport implements Serializable {
    private long id;
    private String Title;
    private byte[] Icon;
    private String PackageName;
    private String Context;


    public Category(){
        Title = "";
        Context = "（无）";
        PackageName = "";
        Icon = null;
    }

    public Category(String Title, byte[] Icon, String PackageName) {
        this.Title = Title;
        this.Icon = Icon;
        this.PackageName = PackageName;
    }

    public Category(String Title, byte[] Icon, String PackageName, String Context) {
        this.Title = Title;
        this.Icon = Icon;
        this.PackageName = PackageName;
        this.Context = Context;
    }

    public String getTitle() {
        return Title;
    }

    public long getId() {
        return id;
    }

    public byte[] getIcon() {
        return Icon;
    }

    public String getPackageName() {
        return PackageName;
    }

    public String getContext() {
        return Context;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public void setIcon(byte[] icon) {
        Icon = icon;
    }

    public void setPackageName(String packageName) {
        PackageName = packageName;
    }

    public void setContext(String context) {
        Context = context;
    }
}
