package com.bunny.CrossInput;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bunny.CrossInput.cyynf.dialog_library.CustomProgress;
import com.bunny.CrossInput.flatdesign.sshadkany.RectButton;
import com.bunny.CrossInput.flatdesign.sshadkany.shapes.RoundRectView;
import com.bunny.CrossInput.network.HttpInterface;
import com.bunny.CrossInput.network.RetrofitCreator;
import com.bunny.CrossInput.utils.SaveData;
import com.king.zxing.CameraScan;
import com.king.zxing.CaptureActivity;

import java.net.HttpURLConnection;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "Cross Screen Input LOG";
    public static final int REQUEST_CODE_SCAN = 0X01;
    private String baseUrl = "http://0.0.0.0:52011";
    private EditText etText;
    private TextView serverIP,explain;
    private RectButton del,clear,up,down,left,right;
    private RoundRectView textBox;
    SaveData saveData = new SaveData(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }
    private void initView(){
        etText = findViewById(R.id.etText);
        serverIP = findViewById(R.id.serverIP);
        explain = findViewById(R.id.explain);
        RectButton scanBtn = findViewById(R.id.scanBtn);
        RectButton moreBtn = findViewById(R.id.moreBtn);
        del = findViewById(R.id.del);
        clear = findViewById(R.id.clear);
        textBox = findViewById(R.id.textBox);
        up = findViewById(R.id.up);
        down = findViewById(R.id.down);
        left = findViewById(R.id.left);
        right = findViewById(R.id.right);
        del.setVisibility(View.INVISIBLE);
        clear.setVisibility(View.INVISIBLE);
        textBox.setVisibility(View.INVISIBLE);
        up.setVisibility(View.INVISIBLE);
        down.setVisibility(View.INVISIBLE);
        left.setVisibility(View.INVISIBLE);
        right.setVisibility(View.INVISIBLE);
        del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                delText();
            }
        });
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                etText.setText("");
            }
        });
        up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                up();
            }
        });
        down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                down();
            }
        });
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                left();
            }
        });
        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                right();
            }
        });
        scanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(MainActivity.this, CaptureActivity.class),REQUEST_CODE_SCAN,null);
            }
        });
        moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(MainActivity.this,moreBtn);
                popupMenu.getMenuInflater().inflate(R.menu.client_menu,popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        if (menuItem.getItemId() == R.id.menu_editHostPort) {
                            editBaseUrl();
                        }
                        if (menuItem.getItemId() == R.id.menu_about) {
                            Intent i = new Intent(MainActivity.this, MyAboutPage.class);
                            startActivity(i);
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });
        etText.addTextChangedListener(new TextWatcher() {
            private String mBeforeText;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                mBeforeText = s.toString();
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String currentText = s.toString();
                if (before < count) {
                    String addedText = currentText.substring(start, start + count - before);
                    Log.d(TAG, "Text added: " + addedText);
                    addText(addedText);
                } else if (before > count) {
                    if (count > 0){
                        delText();
                    }
                }
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        etText.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                InputConnection ic = etText.onCreateInputConnection(new EditorInfo());
                if (ic != null) {
                    if (keyCode == KeyEvent.KEYCODE_DEL) {
                        ic.deleteSurroundingText(1, 0);
                        delText();
                        return true;
                    }else if (keyCode == KeyEvent.KEYCODE_ENTER) {
                        enter();
                        return true;
                    }
                }
            }
            return false;
        });
        explain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScaleAnimation animation = new ScaleAnimation(0.75f, 1.0f, 0.75f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                animation.setDuration(100);
                animation.setFillAfter(true);
                explain.startAnimation(animation);
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setCancelable(true);
                builder.setTitle("免责声明");
                builder.setMessage("本软件只用于日常的学习交流，请勿它用。\n\n" +
                        "如造成不良影响和损失，开发者不承担任何责任。\n\n" +
                        "！！软件有时运行不稳定或者网络原因，会出现问题，请务必审核和确认电脑端接收的文本内容！！\n");
                builder.setPositiveButton("确定", null);
                builder.create().show();
            }
        });
    }
    private void delText(){
        HttpInterface httpInterface = RetrofitCreator.getInstance(baseUrl).getRetrofit().create(HttpInterface.class);
        Call<ResponseBody> task = httpInterface.delText();
        task.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                int code = response.code();
                Log.d(TAG,"Response_code ---> " + code);
                if (code == HttpURLConnection.HTTP_OK){

                        Log.d(TAG,"post_response_body ---> " + response.body());

                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(TAG,"Get Fail ---> " + t.getStackTrace());
                Toast.makeText(MainActivity.this, "连接异常，电脑端未打开或检查网络连接", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void addText(String text){
        String postUrl = "/add?text=" + text;
        HttpInterface httpInterface = RetrofitCreator.getInstance(baseUrl).getRetrofit().create(HttpInterface.class);
        Call<ResponseBody> task = httpInterface.addText(postUrl);
        task.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                int code = response.code();
                Log.d(TAG,"Response_code ---> " + code);
                if (code == HttpURLConnection.HTTP_OK){
                    try {
                        Log.d(TAG,"post_response_body ---> " + response.body());
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(TAG,"Fail ---> " + t.getStackTrace());
                Toast.makeText(MainActivity.this, "连接异常，电脑端未打开或检查网络连接", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void testLink(String baseUrl){
        CustomProgress dialog;
        String IP =  baseUrl.replace("http://","").replace(":" + saveData.loadString("IPPort"),"");
        dialog = CustomProgress.show(this,   IP + "\n连接电脑中...", true, null);
        HttpInterface httpInterface = RetrofitCreator.getInstance(baseUrl).getRetrofit().create(HttpInterface.class);
        Call<ResponseBody> task = httpInterface.testLink();
        task.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                int code = response.code();
                Log.d(TAG,"Response_code ---> " + code);
                if (code == HttpURLConnection.HTTP_OK){
                    Log.d(TAG,"post_response_body ---> " + response.body());
                    del.setVisibility(View.VISIBLE);
                    clear.setVisibility(View.VISIBLE);
                    textBox.setVisibility(View.VISIBLE);
                    up.setVisibility(View.VISIBLE);
                    down.setVisibility(View.VISIBLE);
                    left.setVisibility(View.VISIBLE);
                    right.setVisibility(View.VISIBLE);
                    serverIP.setText("已连接电脑端 IP：" + IP + "  端口：" + saveData.loadString("IPPort"));
                    serverIP.setBackgroundColor(getResources().getColor(R.color.isLink));
                    dialog.dismiss();
                    Toast.makeText(MainActivity.this, "连接电脑成功！", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(TAG,"Get Fail ---> " + t.getStackTrace());
                dialog.dismiss();
                Toast.makeText(MainActivity.this, "连接失败，请检查网络连接设置或电脑端未打开", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void testLinkQr(String baseUrl){
        CustomProgress dialog;
        String address =  baseUrl.replace("http://","");
        String addressStr[] = address.split(":");
        dialog = CustomProgress.show(this,   addressStr[0] + "\n连接电脑中...", true, null);
        HttpInterface httpInterface = RetrofitCreator.getInstance(baseUrl).getRetrofit().create(HttpInterface.class);
        Call<ResponseBody> task = httpInterface.testLink();
        task.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                int code = response.code();
                Log.d(TAG,"Response_code ---> " + code);
                if (code == HttpURLConnection.HTTP_OK){
                    Log.d(TAG,"post_response_body ---> " + response.body());
                    del.setVisibility(View.VISIBLE);
                    clear.setVisibility(View.VISIBLE);
                    textBox.setVisibility(View.VISIBLE);
                    up.setVisibility(View.VISIBLE);
                    down.setVisibility(View.VISIBLE);
                    left.setVisibility(View.VISIBLE);
                    right.setVisibility(View.VISIBLE);
                    serverIP.setText("已连接电脑端 IP：" + addressStr[0] + "  端口：" + addressStr[1]);
                    serverIP.setBackgroundColor(getResources().getColor(R.color.isLink));
                    dialog.dismiss();
                    Toast.makeText(MainActivity.this, "连接电脑成功！", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(TAG,"Get Fail ---> " + t.getStackTrace());
                dialog.dismiss();
                Toast.makeText(MainActivity.this, "连接失败，请检查网络连接设置或电脑端未打开", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void up(){
        HttpInterface httpInterface = RetrofitCreator.getInstance(baseUrl).getRetrofit().create(HttpInterface.class);
        Call<ResponseBody> task = httpInterface.up();
        task.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                int code = response.code();
                Log.d(TAG,"Response_code ---> " + code);
                if (code == HttpURLConnection.HTTP_OK){

                    Log.d(TAG,"post_response_body ---> " + response.body());

                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(TAG,"Get Fail ---> " + t.getStackTrace());
                Toast.makeText(MainActivity.this, "连接异常，电脑端未打开或检查网络连接", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void down(){
        HttpInterface httpInterface = RetrofitCreator.getInstance(baseUrl).getRetrofit().create(HttpInterface.class);
        Call<ResponseBody> task = httpInterface.down();
        task.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                int code = response.code();
                Log.d(TAG,"Response_code ---> " + code);
                if (code == HttpURLConnection.HTTP_OK){

                    Log.d(TAG,"post_response_body ---> " + response.body());

                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(TAG,"Get Fail ---> " + t.getStackTrace());
                Toast.makeText(MainActivity.this, "连接异常，电脑端未打开或检查网络连接", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void left(){
        HttpInterface httpInterface = RetrofitCreator.getInstance(baseUrl).getRetrofit().create(HttpInterface.class);
        Call<ResponseBody> task = httpInterface.left();
        task.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                int code = response.code();
                Log.d(TAG,"Response_code ---> " + code);
                if (code == HttpURLConnection.HTTP_OK){

                    Log.d(TAG,"post_response_body ---> " + response.body());

                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(TAG,"Get Fail ---> " + t.getStackTrace());
                Toast.makeText(MainActivity.this, "连接异常，电脑端未打开或检查网络连接", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void right(){
        HttpInterface httpInterface = RetrofitCreator.getInstance(baseUrl).getRetrofit().create(HttpInterface.class);
        Call<ResponseBody> task = httpInterface.right();
        task.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                int code = response.code();
                Log.d(TAG,"Response_code ---> " + code);
                if (code == HttpURLConnection.HTTP_OK){

                    Log.d(TAG,"post_response_body ---> " + response.body());

                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(TAG,"Get Fail ---> " + t.getStackTrace());
                Toast.makeText(MainActivity.this, "连接异常，电脑端未打开或检查网络连接", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void enter(){
        HttpInterface httpInterface = RetrofitCreator.getInstance(baseUrl).getRetrofit().create(HttpInterface.class);
        Call<ResponseBody> task = httpInterface.enter();
        task.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                int code = response.code();
                Log.d(TAG,"Response_code ---> " + code);
                if (code == HttpURLConnection.HTTP_OK){

                    Log.d(TAG,"post_response_body ---> " + response.body());

                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(TAG,"Get Fail ---> " + t.getStackTrace());
                Toast.makeText(MainActivity.this, "连接异常，电脑端未打开或检查网络连接", Toast.LENGTH_SHORT).show();
            }
        });
    }
    /**
     * 自定义编辑主机端口的对话框
     */
    public void editBaseUrl() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        final AlertDialog dialog = builder.create();
        LayoutInflater factory = LayoutInflater.from(MainActivity.this);
        final View view = factory.inflate(R.layout.client_edit_host_port,null);
        dialog.setTitle("设置电脑端参数");
        dialog.setView(view);
        dialog.setCancelable(true);
        dialog.show();
        Button confirm = view.findViewById(R.id.confirm);
        Button cancel = view.findViewById(R.id.cancel);
        Button clear = view.findViewById(R.id.clear);
        EditText IPOne = view.findViewById(R.id.IPOne);
        EditText IPTwo = view.findViewById(R.id.IPTwo);
        EditText IPThree = view.findViewById(R.id.IPThree);
        EditText IPFour = view.findViewById(R.id.IPFour);
        EditText port = view.findViewById(R.id.port);
        if(saveData.loadString("IPPort") == null) {
            saveData.saveString("52011","IPPort");
        }
        if(saveData.loadString("IPOne") != null) {
            IPOne.setText(saveData.loadString("IPOne"));
        }
        if(saveData.loadString("IPTwo") != null) {
            IPTwo.setText(saveData.loadString("IPTwo"));
        }
        if(saveData.loadString("IPThree") != null) {
            IPThree.setText(saveData.loadString("IPThree"));
        }
        if(saveData.loadString("IPFour") != null) {
            IPFour.setText(saveData.loadString("IPFour"));
        }
        if(saveData.loadString("IPPort") != null) {
            port.setText(saveData.loadString("IPPort"));
        }
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScaleAnimation animation = new ScaleAnimation(0.75f, 1.0f, 0.75f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                animation.setDuration(100);
                animation.setFillAfter(true);
                confirm.startAnimation(animation);
                saveData.saveString(null,"PcIP");
                if(!TextUtils.isEmpty(IPOne.getText().toString()) && !TextUtils.isEmpty(IPTwo.getText().toString())
                && !TextUtils.isEmpty(IPThree.getText().toString()) && !TextUtils.isEmpty(IPFour.getText().toString()) && !TextUtils.isEmpty(port.getText().toString())){
                    if (isIPV4(Integer.parseInt(IPOne.getText().toString())) && isIPV4(Integer.parseInt(IPTwo.getText().toString()))
                            && isIPV4(Integer.parseInt(IPThree.getText().toString())) && isIPV4(Integer.parseInt(IPFour.getText().toString()))){
                        saveData.saveString(IPOne.getText().toString(),"IPOne");
                        saveData.saveString(IPTwo.getText().toString(),"IPTwo");
                        saveData.saveString(IPThree.getText().toString(),"IPThree");
                        saveData.saveString(IPFour.getText().toString(),"IPFour");
                        saveData.saveString(port.getText().toString(),"IPPort");
                        baseUrl = "http://" + IPOne.getText().toString() + "." + IPTwo.getText().toString()
                                + "." + IPThree.getText().toString() + "." + IPFour.getText().toString() + ":" + port.getText().toString();;
                        testLink(baseUrl);
                        dialog.dismiss();
                    }else{
                        Toast.makeText(MainActivity.this, "请填写合法的IPv4地址，每个数字输入范围在0~255", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "请填写好所有输入框！", Toast.LENGTH_SHORT).show();
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ScaleAnimation animation = new ScaleAnimation(0.75f, 1.0f, 0.75f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                animation.setDuration(100);
                animation.setFillAfter(true);
                cancel.startAnimation(animation);
                dialog.dismiss();
            }
        });
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ScaleAnimation animation = new ScaleAnimation(0.75f, 1.0f, 0.75f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                animation.setDuration(100);
                animation.setFillAfter(true);
                clear.startAnimation(animation);
                IPOne.setText("");
                IPTwo.setText("");
                IPThree.setText("");
                IPFour.setText("");
                port.setText("");
            }
        });
    }

    private  boolean isIPV4(int IP){
        return IP >= 0 && IP <= 255;
    }
    /**
     * 安卓onActivityResult，用于Activity界面跳转的数据传递
     *
     * @param requestCode 区分何种操作的数字
     * @param resultCode 操作结果数字
     * @param data 数据
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //ZXingLite扫码得到结果后的操
        if (resultCode == RESULT_OK && data != null && requestCode == REQUEST_CODE_SCAN) {
            String result = CameraScan.parseScanResult(data);
            Log.d(TAG,"result ----> " + result);
            if (result.contains("BunnyInput")){
                baseUrl = result.replace("BunnyInput?","");
                testLinkQr(baseUrl);
            }else
                Toast.makeText(MainActivity.this, "请扫描跨屏输入电脑端生成的二维码！", Toast.LENGTH_SHORT).show();
            }
    }
    /**
     再按一次退出主界面操作
     **/
    long exitTime = 0;
    @Override
    public void onBackPressed() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(this, "再按一次退出应用", Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
            return;
        }
        finish();
    }
}