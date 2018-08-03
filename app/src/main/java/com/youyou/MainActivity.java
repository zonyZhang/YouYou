package com.youyou;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.youyou.activity.BaseActivity;
import com.youyou.activity.MapActivity;
import com.youyou.constant.YouYouConstant.UserField;
import com.youyou.domain.UserItem;
import com.youyou.util.CommonUtil;
import com.youyou.util.YouYouPreference;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private Button saveBtn;

    /**
     * 名称,电话,描述
     */
    private EditText nameEt, telEt, desEt;

    private YouYouPreference mYouYouPreference;

    /**
     * 用户名/电话/描述
     */
    private String name, tel, des, uuid;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isSaveUserInfo()) {
            startMapActivity();
            return;
        }
        setContentView(R.layout.activity_main);
        init();
        initData();
    }

    /**
     * 初始化view及其他初始化
     *
     * @author zony
     * @time 18-6-19
     */
    private void init() {
        nameEt = findViewById(R.id.et_name);
        telEt = findViewById(R.id.et_tel);
        desEt = findViewById(R.id.et_des);
        saveBtn = findViewById(R.id.start_share);
        saveBtn.setOnClickListener(this);
    }

    /**
     * 初始化数据
     *
     * @author zony
     * @time 18-6-19
     */
    private void initData() {
        if (!isSaveUserInfo()) {
            return;
        }
        nameEt.setText(name);
        telEt.setText(tel);
        desEt.setText(des);
    }

    /**
     * 是否已经保存用户信息
     *
     * @author zony
     * @time 18-6-19
     */
    private boolean isSaveUserInfo() {
        mYouYouPreference = YouYouPreference.getInstance(this);
        name = (String) mYouYouPreference.getData(UserField.NAME, "");
        tel = (String) mYouYouPreference.getData(UserField.TEL, "");
        des = (String) mYouYouPreference.getData(UserField.DES, "");
        uuid = CommonUtil.getUUID(this);
        if (TextUtils.isEmpty(name)) {
            return false;
        }
        return true;
    }

    /**
     * 是否有效
     *
     * @author zony
     * @time 18-6-19
     */
    private boolean isValid() {
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, getText(R.string.main_edit_text_name_warn), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(tel)) {
            Toast.makeText(this, getText(R.string.main_edit_text_phone_null_warn), Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!CommonUtil.isPhoneNumberValid(tel)) {
            Toast.makeText(this, getText(R.string.main_edit_text_phone_error_warn), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    /**
     * 开始共享
     *
     * @author zony
     * @time 18-6-19
     */
    private void startShare() {
        name = nameEt.getText().toString().trim();
        tel = telEt.getText().toString().trim();
        des = desEt.getText().toString().trim();
        if (isValid()) {
            mYouYouPreference.putData(UserField.UUID, uuid);
            mYouYouPreference.putData(UserField.NAME, name);
            mYouYouPreference.putData(UserField.TEL, tel);
            mYouYouPreference.putData(UserField.DES, des);
            startMapActivity();
        }
    }

    /**
     * 启动MapActivity
     *
     * @author zony
     * @time 18-6-19
     */
    private void startMapActivity() {
        Toast.makeText(this, getText(R.string.main_text_start_shareing), Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, MapActivity.class);
        Bundle bundle = new Bundle();
        UserItem userItem = new UserItem();
        userItem.setUuid(uuid);
        userItem.setName(name);
        userItem.setTel(tel);
        userItem.setDes(des);
        bundle.putParcelable(UserField.USERITEM, userItem);
        intent.putExtras(bundle);
        CommonUtil.startActivity(this, intent, true);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.start_share:
                startShare();
                break;
            default:
                break;
        }
    }
}
