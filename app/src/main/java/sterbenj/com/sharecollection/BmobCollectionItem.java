package sterbenj.com.sharecollection;

import cn.bmob.v3.BmobObject;


public class BmobCollectionItem extends BmobObject {
    private String Title;
    private String Context;
    private String mUri;
    private String ParentCategory;
    private Byte[] Image;
    private String UserId;

    public String getUserId() {
        return UserId;
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

    public Byte[] getImage() {
        return Image;
    }

    public void setUserId(String userId) {
        UserId = userId;
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

    public void setImage(Byte[] image) {
        Image = image;
    }
}
