package sterbenj.com.sharecollection;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public ActionMode actionMode;
    private List<Category> categoryList = new ArrayList<>();
    public Set<Integer> positionSet = new HashSet<>();
    CollectionsAdapter collectionsAdapter;
    public final static int NAME = 0;
    public static MainActivity instance;
    private FloatingActionMenu fab;
    private FloatingActionButton fabFromApp;
    private FloatingActionButton fabFromCustom;
    private DrawerLayout drawer;

    public static final String TAG = "MainActivity";

    @Override
    public void onResume(){
        Log.d(TAG, "onResume: ");
        super.onResume();
        categoryList.clear();
        List<Category> newList = LitePal.order("id asc").find(Category.class);
        categoryList.addAll(newList);
        collectionsAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        instance = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(LitePal.where("PackageName = ?", "全部收藏").find(Category.class).size() == 0){
            Category category = new Category("全部收藏", tools.DrawableToByteArray(ContextCompat.getDrawable(this, R.drawable.ic_launcher_foreground)), "全部收藏", "全部收藏");
            category.save();
        }

        /*
        初始化recyclerView和其adapter
         */
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.recycler_list);
        collectionsAdapter = new CollectionsAdapter(categoryList);
        collectionsAdapter.setMyClickListener(new CollectionsAdapter.MyClickListener(){
            //长按进入多选编辑模式
            @Override
            public void longClickListener(View v, int position){
                if (actionMode == null  && position != 0) {
                    actionMode = startSupportActionMode(new MyCallback());
                    drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                    fab.hideMenu(true);
                    positionSet.add(position);
                    actionMode.setTitle(positionSet.size() + " 已选择项目");
                    collectionsAdapter.notifyItemChanged(position);
                }
            }

            //短按进入笔记编辑，多选模式则选择目标
            @Override
            public void ClickListener(View v, int position, Context mContext, Category category1, int NAME, CheckBox checkBox){
                if (actionMode != null){
                    if (positionSet.contains(position) && position != 0) {
                        // 如果包含，则撤销选择
                        positionSet.remove(position);
                    }
                    else if (!positionSet.contains(position) && position != 0){
                        // 如果不包含，则添加
                        positionSet.add(position);
                    }
                    actionMode.setTitle(positionSet.size() + " 已选择项目");
                        // 更新列表界面
                    collectionsAdapter.notifyItemChanged(position);
                }
                else{
                    Intent intent = new Intent(mContext, CollectionItemListActivity.class);
                    intent.putExtra("Category", category1);
                    intent.putExtra("SpinnerIndex", position);
                    intent.setAction("From CategoryList");
                    mContext.startActivity(intent);
                }
            }
        });

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(collectionsAdapter);

        //下滑隐藏fab，上滑出现fab
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0){
                    fab.hideMenu(true);
                }
                if(dy < 0 && actionMode == null){
                    fab.showMenu(true);
                }
            }
        });
        /*
        END
        初始化recyclerView和其adapter
         */

        //初始化toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //初始化fab
        fab = (FloatingActionMenu) findViewById(R.id.fab);
        fabFromApp = (FloatingActionButton) findViewById(R.id.fab_fromapp);
        fabFromCustom = (FloatingActionButton) findViewById(R.id.fab_fromcustom);
        fab.setClosedOnTouchOutside(false);

        //从应用列表新建
        fabFromApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ApplistActivity.class);
                startActivityForResult(intent, NAME);
            }
        });

        //自定义新建类
        fabFromCustom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CategoryEditActivity.class);
                intent.setAction("From Custom");
                startActivity(intent);
            }
        });


        drawer= (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id){
            case R.id.action_exit:
                finish();
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_setting) {
            startActivity(new Intent(this, SettingActivity.class));
            // Handle the camera action
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    /*
    多选编辑模式
     */
    private class MyCallback implements ActionMode.Callback{
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            if (actionMode == null){
                actionMode = mode;
                MenuInflater inflater = mode.getMenuInflater();
                inflater.inflate(R.menu.main_delete, menu);
                return true;
            }
            return false;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()){
                case R.id.main_actionMode_delete:
                    return confirmDeleteAll();
                case R.id.main_actionMode_SelectAll:
                    if (positionSet.size() == categoryList.size() - 1){
                        for (int i = 1; i < collectionsAdapter.getItemCount(); i++){
                            positionSet.remove(i);
                        }
                    }
                    else{
                        for (int i = 1; i < collectionsAdapter.getItemCount(); i++){
                            if (!positionSet.contains(i)){
                                positionSet.add(i);
                            }
                        }
                    }
                    actionMode.setTitle(positionSet.size() + " 已选择项目");
                    collectionsAdapter.notifyDataSetChanged();
                    return true;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            actionMode = null;
            positionSet.clear();
            fab.showMenu(true);
            categoryList.clear();
            List<Category> newList = LitePal.order("id asc").find(Category.class);
            categoryList.addAll(newList);
            collectionsAdapter.notifyDataSetChanged();
        }
        /*
        END
        多选编辑模式
         */


        /*
        多选模式删除确认
         */
        public boolean confirmDeleteAll(){


            /*
            确认是否删除
             */
            AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
            dialog.setTitle("");
            dialog.setMessage("确认删除" + positionSet.size() + "个项目吗？");
            dialog.setCancelable(false);

            //确认
            dialog.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    for (int position : positionSet){
                        LitePal.delete(Category.class, collectionsAdapter.getCategoryList().get(position).getId());
                    }
                    actionMode.finish();
                }
            });

            //取消
            dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });

            dialog.show();
            /*
            确认是否删除
             */


            return true;
        }
        /*
        END
        多选模式删除确认
         */


    }
}