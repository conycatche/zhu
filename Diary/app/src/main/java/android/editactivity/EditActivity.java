package android.editactivity;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.mainactivity.KeyboardUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.mainactivity.diary;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.zamcenter.app.diary.R;
import org.litepal.crud.DataSupport;
import java.text.SimpleDateFormat;

public class EditActivity extends AppCompatActivity {

    //接收上个活动传入的日记内容
    private String diaryTitle;
    private String diaryContent;
    private String author;
    //接收上个活动传入的标志
    private int signal=0;

    //加载菜单
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.edit_toolbar,menu);
        return true;
    }

    //菜单项的点击事件
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //点击保存
            case R.id.save_button: {
                EditText editText=(EditText)findViewById(R.id.edit_content);
                EditText editTitle=(EditText)findViewById(R.id.edit_title);
                String title=editTitle.getText().toString();
                String content=editText.getText().toString();
                //获得用户名
                SharedPreferences pref = getSharedPreferences("com.zamcenter.app.diary_preferences", Context.MODE_PRIVATE);
                String author = pref.getString("account","");
                SimpleDateFormat sdf=new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
                String time=sdf.format(new java.util.Date());
                diary mDiary=new diary(title,content,time,author);
                //点击’新建‘后编辑的内容就存储
                if(signal==0) {
                    mDiary.save();
                    //防止连续点击’存储‘按钮连续存储一样的内容
                    signal=3;
                    //显示登录成功并跳转到主界面活动
                    Toast.makeText(EditActivity.this,"保存成功！",Toast.LENGTH_SHORT).show();
                    finish();
                }
                //更新原有内容的就只更新
                else{
                    //防止连续点击’存储‘按钮连续存储一样的内容
                    signal=3;
                    ContentValues values = new ContentValues();
                    values.put("time", mDiary.getTime().toString());
                    DataSupport.updateAll(diary.class,values,"content=?",diaryContent);
                    values.put("title", mDiary.getTitle().toString());
                    DataSupport.updateAll(diary.class,values,"content=?",diaryContent);
                    values.put("content", mDiary.getContent().toString());
                    DataSupport.updateAll(diary.class,values,"content=?",diaryContent);
                    //显示登录成功并跳转到主界面活动
                    Toast.makeText(EditActivity.this,"保存成功！",Toast.LENGTH_SHORT).show();
                    finish();
                }
                //隐藏光标并收起键盘
                editText.setCursorVisible(false);
                KeyboardUtils.hideKeyboard(this);
                break;
            }
            //点击返回
            case android.R.id.home:{
                //已经保存的直接返回
                if(signal==3){
                    finish();
                }
                //未保存的提示是否保存
                else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("");
                    builder.setMessage("保存此次修改吗？");
                    builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            EditText editText=(EditText)findViewById(R.id.edit_content);
                            EditText editTitle=(EditText)findViewById(R.id.edit_title);
                            String title=editTitle.getText().toString();
                            String content=editText.getText().toString();
                            //获得用户名
                            SharedPreferences pref = getSharedPreferences("com.zamcenter.app.diary_preferences", Context.MODE_PRIVATE);
                            String author = pref.getString("account","");
                            SimpleDateFormat sdf=new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
                            String time=sdf.format(new java.util.Date());
                            diary mDiary=new diary(title,content,time,author);
                            if(signal==0){
                                mDiary.save();
                            }else{
                                ContentValues values = new ContentValues();
                                values.put("time", mDiary.getTime().toString());
                                DataSupport.updateAll(diary.class,values,"content=?",diaryContent);
                                values.put("title", mDiary.getTitle().toString());
                                DataSupport.updateAll(diary.class,values,"title=?",diaryTitle);
                                values.put("content", mDiary.getContent().toString());
                                DataSupport.updateAll(diary.class,values,"content=?",diaryContent);
                            }
                            finish();
                        }
                    });
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
                    builder.create().show();
                }
                break;
            }
            default:
        }
        return true;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        //接收由MainActivity传来的日记信息
        Intent intent=getIntent();
        diaryTitle=intent.getStringExtra("diaryTitle");
        diaryContent=intent.getStringExtra("diaryContent");
        signal=intent.getIntExtra("signal",0);
        Toolbar toolbar=(Toolbar)findViewById(R.id.edit_toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back);
        }
        final EditText editText=(EditText)findViewById(R.id.edit_content);
        editText.setText(diaryContent);
        //光标放文本后面
        editText.setSelection(editText.getText().length());
        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setCursorVisible(true);
            }
        });
        final EditText editTitle=(EditText)findViewById(R.id.edit_title);
        editTitle.setText(diaryTitle);
        //光标放文本后面
        editTitle.setSelection(editTitle.getText().length());
        editTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTitle.setCursorVisible(true);
            }
        });
    }
}