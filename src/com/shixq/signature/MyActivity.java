package com.shixq.signature;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MyActivity extends Activity {
    private Button copyBtn;
    private TextView errorTv;
    private Button getBtn;
    private EditText pkgNameEt;
    private TextView resultTv;

    private void errout(String paramString) {
        this.errorTv.append(paramString + "\n");
    }

    private Signature[] getRawSignature(Context paramContext, String paramString) {
        if ((paramString == null) || (paramString.length() == 0)) {
            errout("getSignature, packageName is null");
            return null;
        }
        PackageManager localPackageManager = paramContext.getPackageManager();
        PackageInfo localPackageInfo;
        try {
            localPackageInfo = localPackageManager.getPackageInfo(paramString, PackageManager.GET_SIGNATURES);
            if (localPackageInfo == null) {
                errout("info is null, packageName = " + paramString);
                return null;
            }
        } catch (PackageManager.NameNotFoundException localNameNotFoundException) {
            errout("NameNotFoundException");
            return null;
        }
        return localPackageInfo.signatures;
    }

    private void getSign(String packageName) {
        Signature[] arrayOfSignature = getRawSignature(this, packageName);
        if ((arrayOfSignature == null) || (arrayOfSignature.length == 0)) {
            errout("signs is null");
            return;
        }
        stdout(MD5.getMessageDigest(arrayOfSignature[0].toByteArray()));
    }

    private void showDialog() {
        AlertDialog.Builder localBuilder = new AlertDialog.Builder(this);
        localBuilder.setCancelable(false);
        localBuilder.setTitle(R.string.alert_title).setMessage(R.string.alert_msg);
        localBuilder.setPositiveButton(R.string.ok, null);
        localBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                MyActivity.this.finish();
            }
        });
        localBuilder.show();
    }

    private void stdout(String paramString) {
        this.resultTv.append(paramString + "\n");
        this.copyBtn.setVisibility(View.VISIBLE);
    }

    public void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        setContentView(R.layout.main);
        this.pkgNameEt = ((EditText) findViewById(R.id.pkg_name_et));
        this.resultTv = ((TextView) findViewById(R.id.result_tv));
        this.errorTv = ((TextView) findViewById(R.id.error_tv));
        this.getBtn = ((Button) findViewById(R.id.get_sign_btn));
        this.getBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View paramView) {
                MyActivity.this.resultTv.setText("");
                MyActivity.this.errorTv.setText("");
                MyActivity.this.copyBtn.setVisibility(View.GONE);
                String packageName = MyActivity.this.pkgNameEt.getText().toString();
                if ((packageName != null) && (packageName.length() > 0))
                    MyActivity.this.getSign(packageName);
            }
        });
        this.copyBtn = ((Button) findViewById(R.id.copy_btn));
        this.copyBtn.setVisibility(View.GONE);
        this.copyBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View paramView) {
                ((ClipboardManager) MyActivity.this.getSystemService(Context.CLIPBOARD_SERVICE)).setPrimaryClip(ClipData.newPlainText(null, MyActivity.this.resultTv.getText().toString().trim()));
                Toast.makeText(MyActivity.this, R.string.copy_done, Toast.LENGTH_SHORT).show();
            }
        });
        showDialog();
    }
}
