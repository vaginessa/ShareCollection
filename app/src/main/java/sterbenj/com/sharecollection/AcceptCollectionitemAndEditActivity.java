package sterbenj.com.sharecollection;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

/**
 * XJB Created by 野良人 on 2018/6/14.
 */
public class AcceptCollectionitemAndEditActivity extends BaseActivity {

    private TextInputEditText Title;
    private TextInputEditText Context;
    private CollectionItem collectionItem;
    private AppCompatSpinner spinner;
    private Toolbar toolbar;
    private String data;
    private String uri;
    private String PackageName;
    private int SpinnerIndex;
    private List<String> categoryName = new ArrayList<>();
    private Intent intent;

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        toolbar.setTitle("编辑");
        super.onPostCreate(savedInstanceState);
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
        Context = (TextInputEditText)findViewById(R.id.accept_edit_context);
        Title = (TextInputEditText)findViewById(R.id.accept_edit_title);

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
            collectionItem = (CollectionItem)intent.getSerializableExtra("CollectionItem");
            uri = collectionItem.getmUri();
            Context.setText(collectionItem.getContext());
            Title.setText(collectionItem.getTitle());
            PackageName = collectionItem.getParentCategory();
            SpinnerIndex = intent.getIntExtra("SpinnerIndex", -1);
        }

        //外部应用数据
        else{
            data = intent.getStringExtra(Intent.EXTRA_TEXT);
            uri = data.substring(data.indexOf("http"));
            Context.setText(data.substring(0, data.indexOf("http")));
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
                finish();
                break;
        }
        return true;
    }

    public void saveCollectionItem(){

        collectionItem.setmUri(uri);
        collectionItem.setContext(Context.getText().toString());
        collectionItem.setTitle(Title.getText().toString());
        collectionItem.setParentCategory(PackageName);
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
}
