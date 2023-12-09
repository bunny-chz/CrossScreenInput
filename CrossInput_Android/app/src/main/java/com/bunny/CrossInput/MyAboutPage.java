package com.bunny.CrossInput;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;

/**
 * Project:  文件快传
 * Comments: 关于界面类
 * JDK version used: <JDK1.8>
 * Author： Bunny     Github: https://github.com/bunny-chz/
 * Create Date：2022-10-13
 * Version: 1.0
 */

public class MyAboutPage extends AppCompatActivity {
    private static final String SHARE_TEXT = "跨屏输入是一款支持在安卓和Windows两个平台之间，传输文字文本的打字辅助软件。\n\n" +
            "下载时请看准安卓版本选择对应的下载！\n" +
            "蓝奏云下载：https://zss233.lanzout.com/b00r968bc\n" +
            "密码:2333\n";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View aboutPage = new AboutPage(this)
                .isRTL(false)
                .setImage(R.mipmap.app_icon)
                .setDescription(this.getResources().getString(R.string.APP_description))
                .addItem(new Element().setTitle("Version 1.0.0"))
                .addItem(getExplain())
                .addItem(getShare())
                .addItem(getDonationForAliPay())
                .addItem(getDonationForWechat())
                .addGroup("联系开发者(Bunny)")
                .addGitHub("bunny-chz","GitHub(bunny-chz)")
                .create();
        setContentView(aboutPage);
    }
    Element getExplain() {
        Element explainElement = new Element();
        explainElement.setTitle("免责声明");
        explainElement.setAutoApplyIconTint(true);
        explainElement.setIconTint(mehdi.sakout.aboutpage.R.color.about_item_icon_color);
        explainElement.setIconNightTint(android.R.color.white);
        explainElement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MyAboutPage.this);
                builder.setCancelable(true);
                builder.setTitle("免责声明");
                builder.setMessage("本软件只用于日常的学习交流，请勿它用。\n\n" +
                        "如造成不良影响和损失，开发者不承担任何责任。\n\n" +
                        "！！软件有时运行不稳定或者网络原因，会出现问题，请务必审核和确认电脑端接收的文本内容！！\n");
                builder.setPositiveButton("确定", null);
                builder.create().show();
            }
        });
        return explainElement;
    }
    Element getShare() {
        Element shareElement = new Element();
        shareElement.setTitle("分享");
        shareElement.setAutoApplyIconTint(true);
        shareElement.setIconTint(mehdi.sakout.aboutpage.R.color.about_item_icon_color);
        shareElement.setIconNightTint(android.R.color.white);
        shareElement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT, SHARE_TEXT);
                intent.setType("text/plain");
                startActivity(intent);
            }
        });
        return shareElement;
    }
    Element getDonationForAliPay() {
        Element donationElement = new Element();
        donationElement.setTitle("打赏开发者（支付宝）");
        donationElement.setAutoApplyIconTint(true);
        donationElement.setIconTint(mehdi.sakout.aboutpage.R.color.about_item_icon_color);
        donationElement.setIconNightTint(android.R.color.white);
        donationElement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                donationForAliPay();
            }
        });
        return donationElement;
    }
    Element getDonationForWechat() {
        Element donationElement = new Element();
        donationElement.setTitle("打赏开发者（微信）");
        donationElement.setAutoApplyIconTint(true);
        donationElement.setIconTint(mehdi.sakout.aboutpage.R.color.about_item_icon_color);
        donationElement.setIconNightTint(android.R.color.white);
        donationElement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                donationForWecaht();
            }
        });
        return donationElement;
    }

    public void donationForAliPay() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MyAboutPage.this);
        final AlertDialog dialog = builder.create();
        LayoutInflater factory = LayoutInflater.from(MyAboutPage.this);
        final View view = factory.inflate(R.layout.img_alipay,null);
        dialog.setView(view);
        dialog.setCancelable(true);
        dialog.show();
    }

    public void donationForWecaht() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MyAboutPage.this);
        final AlertDialog dialog = builder.create();
        LayoutInflater factory = LayoutInflater.from(MyAboutPage.this);
        final View view = factory.inflate(R.layout.img_wechatpay,null);
        dialog.setView(view);
        dialog.setCancelable(true);
        dialog.show();
    }
}