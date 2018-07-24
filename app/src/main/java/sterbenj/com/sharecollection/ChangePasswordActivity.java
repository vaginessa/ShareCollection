package sterbenj.com.sharecollection;

import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FetchUserInfoListener;
import cn.bmob.v3.listener.UpdateListener;

public class ChangePasswordActivity extends BaseActivity {

    TextInputLayout passwordLayout;
    TextInputEditText password;
    TextInputLayout passwordAgainLayout;
    TextInputEditText passwordAgain;

    String passwordStr;
    String passwordAgainStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        //密码
        passwordLayout = (TextInputLayout)findViewById(R.id.changepassword_password_layout);
        password = (TextInputEditText) passwordLayout.getEditText();
        //密码监听
        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                //监听重复输入是否相同
                passwordAgainStr = passwordAgain.getText().toString();
                if (!passwordAgainStr.isEmpty()){
                    if (!s.toString().equals(passwordAgainStr)){
                        passwordAgainLayout.setErrorEnabled(true);
                        passwordAgainLayout.setError("两次输入密码不同");
                    }
                    else{
                        passwordAgainLayout.setErrorEnabled(false);
                    }
                }
                else{
                    passwordAgainLayout.setErrorEnabled(false);
                }

                //监听长度
                if (!s.toString().isEmpty()){
                    if (s.toString().length() < 8 || s.toString().length() > 16){
                        passwordLayout.setErrorEnabled(true);
                        passwordLayout.setError("长度错误");
                    }
                    else{
                        passwordLayout.setErrorEnabled(false);
                    }
                }
                else{
                    passwordLayout.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //确认密码
        passwordAgainLayout = (TextInputLayout)findViewById(R.id.changepassword_passwordAgain_layout);
        passwordAgain = (TextInputEditText) passwordAgainLayout.getEditText();
        //确认密码监听
        passwordAgain.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                //监听重复输入是否相同
                passwordStr = password.getText().toString();
                if (!s.toString().isEmpty()){
                    if (!s.toString().equals(passwordStr)){
                        passwordAgainLayout.setErrorEnabled(true);
                        passwordAgainLayout.setError("两次输入密码不同");
                    }
                    else{
                        passwordAgainLayout.setErrorEnabled(false);
                    }
                }
                else{
                    passwordAgainLayout.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //确认修改密码
        AppCompatButton buttonChangePassword = (AppCompatButton)findViewById(R.id.changepassword_confimChangeButton);
        buttonChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInput()){
                    changePassword();
                }
                else{
                    Toast.makeText(getApplicationContext(), "修改密码失败，检测输入是否合法", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //返回
        AppCompatButton buttonBack = (AppCompatButton)findViewById(R.id.changepassword_back);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    //检测输入是否合法
    private Boolean checkInput(){
        passwordStr = password.getText().toString();
        passwordAgainStr = passwordAgain.getText().toString();
        if (passwordStr.isEmpty() || passwordAgainStr.isEmpty()
                || passwordStr.length() < 8 || passwordStr.length() > 16
                || !passwordAgainStr.equals(passwordStr)){
            return false;
        }
        else{
            return true;
        }
    }

    //修改密码
    private Boolean changePassword(){
        BmobUser newBmobUser = new BmobUser();
        newBmobUser.setPassword(passwordStr);
        BmobUser bmobUser = BmobUser.getCurrentUser(User.class);
        newBmobUser.update(bmobUser.getObjectId(), new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null){
                    Toast.makeText(getApplicationContext(), "修改密码成功", Toast.LENGTH_SHORT).show();

                    //本地缓存与服务器同步
                    BmobUser.fetchUserJsonInfo(new FetchUserInfoListener<String>() {
                        @Override
                        public void done(String s, BmobException e) {
                        }
                    });

                    finish();
                }
                else{
                    Toast.makeText(getApplicationContext(), "修改密码失败，请检查网络", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return false;
    }
}
