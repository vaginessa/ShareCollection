package sterbenj.com.sharecollection;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TextInputEditText;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.litepal.LitePal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * XJB Created by 野良人 on 2018/6/14.
 */
public class AcceptCollectionitemAndEditActivity extends BaseActivity {

    private CardView cardView;
    private TextInputEditText Title;
    private String title;
    private TextInputEditText Context;
    private String context;
    private AppCompatImageView appCompatImageView;
    private ContentLoadingProgressBar progressBar;
    private CollectionItem collectionItem;
    private AppCompatSpinner spinner;
    private Toolbar toolbar;
    private String data;
    private String uri;
    private Uri mImageUri = null;
    private String PackageName;
    private int SpinnerIndex;
    private List<String> categoryName = new ArrayList<>();
    private Intent intent;
    private boolean hasFinishImage;
    public static final int SET_IMAGE = 1;
    public static final int NO_IMAGE = 2;
    private cacheData cache = new cacheData("", "");


    //缓存原有数据
    static class cacheData{
        String Title;
        String Context;

        public cacheData(String title, String context){
            Context = context;
            Title = title;
        }

        public String getContext() {
            return Context;
        }

        public String getTitle() {
            return Title;
        }

        public void setContext(String context) {
            Context = context;
        }

        public void setTitle(String title) {
            Title = title;
        }
    }

    //子线程获取uri信息
    private Thread thread = new Thread(new Runnable() {
        @Override
        public void run() {
            try
            {
                Document document;
                Elements images;
                Elements ImageUri;
                String single = null;
                document = Jsoup.connect(uri).timeout(10000).get();

                //获取标题
                title = document.getElementsByTag("title").text();

                //微信公众号
                if (uri.indexOf("weixin.qq.com") != -1){
                    images = document.getElementsByTag("img");
                    ImageUri = images.select("[data-src$=jpeg],[data-src$=png]");
                    Log.d("++++++++++++++++++++", "run: " + ImageUri.size() + "and" + images.size());
                    if (ImageUri.size() != 0) {
                        single = ImageUri.first().attr("abs:data-src");
                    }
                    Log.d("Accept", "run: weixin");
                }

                //bilibili
                else if (uri.indexOf("bilibili.com") != -1){

                    //获取所有meta标签数据
                    images = document.getElementsByTag("meta");

                    //获取图片uri
                    ImageUri = images.select("[content$=.jpg]");
                    if (ImageUri.size() != 0){
                        single = ImageUri.first().attr("abs:content");
                    }

                    Log.d("+++++++++++++", "run: "+ document.getElementsByTag("h1").size());

                    //获取并设置context
                    ImageUri= images.select("[name=description]");
                    if (ImageUri.size() != 0){
                        context = ImageUri.attr("content");
                    }

                    Log.d("Accept", "run: bilibili");
                }

                //知乎
                else if (uri.indexOf("zhihu.com") != -1){
                    images = document.getElementsByTag("link");
                    ImageUri = images.select("[rel=shortcut icon]");
                    if (images.size() != 0){
                        single = ImageUri.attr("abs:href");
                    }
                }

                //酷安
                else if (uri.indexOf("www.coolapk.com") != -1){
                    images = document.getElementsByTag("img");
                    ImageUri = images.select("[src$=under_logo.png]");
                    if (images.size() != 0){
                        single = ImageUri.first().attr("abs:src");
                    }
                }

                //普通微博
                else if (uri.indexOf("m.weibo.cn") != -1){
                    images = document.getElementsByTag("script");
                    String[] jsData = images.get(1).data().toString().split("var");
                    for (String str : jsData){
                        if (str.contains("$render_data")){
                            String[] ineed = str.split("\",");
                            for (String str2 : ineed){
                                if (str2.trim().contains("original_pic")){
                                    Log.d("MainActivity233", "run: "+ str2.trim() + "  " + str2);
                                    single = str2.substring(str2.indexOf("http"));
                                }
                            }
                        }
                    }
                }

                //微博图文
                else if (uri.indexOf("media.weibo.cn") != -1){
                    images = document.getElementsByTag("script");
                    String[] jsData = images.get(1).data().toString().split("var");
                    for (String str : jsData){
                        if (str.contains("$render_data")){

                            //图片
                            String[] ineed = str.split("\"|\":");
                            for (String str2 : ineed){
                                if (str2.trim().contains(".jpg")){
                                    Log.d("MainActivity233", "run: "+ str2.trim() + "  " + str2);
                                    single = str2.trim();
                                    break;
                                }
                            }

                            //标题&&内容
                            String[] ineed1 = str.split("\",");
                            for (String str2 : ineed1){
                                if (str2.trim().contains("\"title\":")){
                                    Log.d("MainActivity233", "run: "+ str2.trim() + "  " + str2);
                                    title = str2.trim().split("\"title\": \"")[1];
                                }
                                if (str2.trim().contains("\"summary\":")){
                                    Log.d("MainActivity233", "run: "+ str2.trim() + "  " + str2);
                                    context = str2.trim().split("\"summary\": \"")[1];
                                }
                            }
                        }
                    }
                }

                //默认情况
                else{
                    //获取所有img标签数据
                    images = document.getElementsByTag("img");
                    //筛选有可能有图片的标签
                    ImageUri = images.select("[data-src$=jpeg],[data-src$=png],[src$=jpeg],[src$=png]");
                    //筛选到时
                    if (ImageUri.size() != 0){
                        single = ImageUri.first().attr("abs:src");
                        Log.d("Accept", "In run: not 0");
                    }
                    //筛选不到时
                    else{
                        single = null;
                        Log.d("Accept", "In run: 0");
                    }
                    Log.d("Accept", "run: normal");
                }

                //主线程更新UI
                //图片获取失败时
                if (single == null){
                    Message message = new Message();
                    message.what = NO_IMAGE;
                    handler.sendMessage(message);
                }
                //获取成功时
                else{
                    mImageUri = Uri.parse(single);
                    Message message = new Message();
                    message.what = SET_IMAGE;
                    handler.sendMessage(message);
                }
            }

            catch (IOException e)
            {
                e.printStackTrace();
            }
            //链接超时
            finally {
                Message message = new Message();
                message.what = NO_IMAGE;
                handler.sendMessage(message);
            }
        }
    });

    private  Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what){
                case SET_IMAGE:
                    Glide.with(getApplicationContext()).load(mImageUri).error(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_close_black_24dp)).into(appCompatImageView);
                    Title.setText(title);
                    Context.setText(context);
                    progressBar.setVisibility(View.GONE);
                    hasFinishImage = true;
                    break;
                case NO_IMAGE:
                    appCompatImageView.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_close_black_24dp));
                    Title.setText(title);
                    Context.setText(context);
                    progressBar.setVisibility(View.GONE);
                    hasFinishImage = true;
                    break;
            }
        }
    };


    @Override
    public void onBackPressed() {
        confirmBack();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accept_edit_collectionitem);


        if(LitePal.where("PackageName = ?", "全部收藏").find(Category.class).size() == 0){
            Category category = new Category("全部收藏", tools.DrawableToByteArray(ContextCompat.getDrawable(this, R.drawable.ic_folder_black_24dp)), "全部收藏", "全部收藏");
            category.save();
        }

        //初始化各种控件
        collectionItem = new CollectionItem();
        cardView = (CardView)findViewById(R.id.accept_collectionitem_cardview);
        Context = (TextInputEditText)findViewById(R.id.accept_edit_context);
        Title = (TextInputEditText)findViewById(R.id.accept_edit_title);
        appCompatImageView = (AppCompatImageView)findViewById(R.id.accept_collectionitem_image);
        progressBar = (ContentLoadingProgressBar) findViewById(R.id.accept_collectionitem_progressbar);

        //根据主题改变cardview背景
        switch (BaseActivity.sTheme){
            case R.style.white_transStat:
                cardView.setCardBackgroundColor(ContextCompat.getColor(this, R.color.cardview_light_background));
                break;
            case R.style.Dark:
                cardView.setCardBackgroundColor(ContextCompat.getColor(this, R.color.cardview_dark2));
                break;
        }

        //初始化spinner列表
        categoryName.clear();
        for (Category category : LitePal.findAll(Category.class)){
            categoryName.add(category.getTitle());
        }

        //获取intent信息
        intent = getIntent();


        //初始化Spinner
        spinner = (AppCompatSpinner)findViewById(R.id.accept_edit_spinner);

        //来自应用内
        if (intent.getAction().equals("FROM_IN")){
            long collectionItemID = intent.getLongExtra("CollectionItemID", -1);
            collectionItem = LitePal.find(CollectionItem.class, collectionItemID);
            uri = collectionItem.getmUri();
            Context.setText(collectionItem.getContext());
            Title.setText(collectionItem.getTitle());

            hasFinishImage = true;
            if (collectionItem.getImage() != null){
                Glide.with(getApplicationContext()).load(collectionItem.getImage()).into(appCompatImageView);
                progressBar.setVisibility(View.GONE);
            }

            //初始化cache
            cache = new cacheData(collectionItem.getTitle(), collectionItem.getContext());

            PackageName = collectionItem.getParentCategory();
            SpinnerIndex = intent.getIntExtra("SpinnerIndex", -1);
        }

        //来自剪贴板
        else if (intent.getAction().equals("FROM_PASTE")){
            hasFinishImage = false;
            if (intent.getStringExtra("Paste") != null){
                uri = intent.getStringExtra("Paste");
                title = "";
                thread.start();
            }
        }

        //外部应用数据
        else{
            hasFinishImage = false;
            if (intent.getStringExtra(Intent.EXTRA_TEXT) != null){
                data = intent.getStringExtra(Intent.EXTRA_TEXT);
                uri = data.substring(data.indexOf("http"));
                title = data.substring(0, data.indexOf("http"));
                thread.start();
            }
            else{
                Toast.makeText(this, "不支持的分享类型", Toast.LENGTH_SHORT).show();
                finish();
            }
        }


        spinner.setAdapter(new ArrayAdapter<String>(this, R.layout.spinner_item, categoryName));

        spinner.setSelection(SpinnerIndex);


        //设置spinner监听
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                //根据选中的Name查找类别中对应的PackageName
                ArrayAdapter<String> adapter = (ArrayAdapter<String>) parent.getAdapter();
                String SelectedName = adapter.getItem(position);
                for (Category category : LitePal.where("Title = ?", SelectedName)
                        .find(Category.class)) {
                    PackageName = category.getPackageName();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //初始化Toolbar
        toolbar = (Toolbar)findViewById(R.id.toolbar_accept_edit);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle("编辑");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.accept_collectionitem_edit_toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.accept_collectionitem_save:
                saveCollectionItem();
                break;
            case android.R.id.home:
                confirmBack();
                break;
        }
        return true;
    }

    public void saveCollectionItem(){

        if (hasFinishImage && !thread.isAlive()){
            collectionItem.setmUri(uri);
            collectionItem.setContext(Context.getText().toString());
            collectionItem.setTitle(Title.getText().toString());
            collectionItem.setParentCategory(PackageName);
            collectionItem.setImage(tools.DrawableToByteArray(appCompatImageView.getDrawable()));
            if (!collectionItem.getTitle().isEmpty()){
                if (intent.getAction().equals("FROM_IN")){
                    collectionItem.update(collectionItem.getId());
                }
                else{
                    collectionItem.save();
                }
                Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
                finish();
            }
            else{
                Toast.makeText(this, "保存失败，标题不能为空", Toast.LENGTH_SHORT).show();
            }
        }
        else{
            Toast.makeText(this, "保存失败，图片未加载完成", Toast.LENGTH_SHORT).show();
        }
    }

    //确认是否返回
    public void confirmBack(){
        if (!(cache.getContext().equals(Context.getText().toString()) && cache.getTitle().equals(Title.getText().toString()))){
            AlertDialog.Builder dialog = new AlertDialog.Builder(AcceptCollectionitemAndEditActivity.this);
            dialog.setMessage("内容已经更改，是否保存？");
            dialog.setCancelable(true);
            dialog.setPositiveButton("保存", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    saveCollectionItem();
                }
            });
            dialog.setNegativeButton("不保存", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            dialog.show();
        }
        else {
            finish();
        }
    }
}
