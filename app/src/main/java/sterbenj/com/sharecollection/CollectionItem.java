package sterbenj.com.sharecollection;

import org.litepal.crud.LitePalSupport;

import java.io.Serializable;

/**
 * XJB Created by 野良人 on 2018/6/13.
 */
public class CollectionItem extends LitePalSupport implements Serializable {
    private long id;
    private String Title;
    private String Context;
    private String mUri;
    private String ParentCategory;
    private byte[] Image;

    public long getId() {
        return id;
    }

    public String getTitle() {
        return Title;
    }

    public String getContext() {
        return Context;
    }

    public String getmUri() {
        return mUri;
    }

    public String getParentCategory() {
        return ParentCategory;
    }

    public byte[] getImage() {
        return Image;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public void setContext(String context) {
        Context = context;
    }

    public void setmUri(String mUri) {
        this.mUri = mUri;
    }

    public void setParentCategory(String parentCategory) {
        ParentCategory = parentCategory;
    }

    public void setImage(byte[] image) {
        Image = image;
    }
}
