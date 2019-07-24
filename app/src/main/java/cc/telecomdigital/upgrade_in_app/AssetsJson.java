package cc.telecomdigital.upgrade_in_app;

import android.content.Context;

import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.InputStream;

import cc.telecomdigital.upgrade_in_app.checkversion.bean.UpdateBean;

public class AssetsJson {

    public static UpdateBean parseAsset(Context context, String assetJson) {
        UpdateBean bean = null;
        try {
            InputStream inputStream = context.getAssets().open(assetJson);
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            String json = new String(buffer, "UTF-8");

            return new GsonBuilder().setLenient().create().fromJson(json, UpdateBean.class);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return bean;
    }
}
