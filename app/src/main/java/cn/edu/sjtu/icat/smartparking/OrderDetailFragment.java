package cn.edu.sjtu.icat.smartparking;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Created by Ruochen on 2015/10/21.
 */
public class OrderDetailFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_order_detail, null);

        ImageView qrcodeImageView = (ImageView)v.findViewById(R.id.order_detail_qrcode);
        try {
            Bitmap bitmap = MiscUtils.str2Bitmap("12345dsfafsdfasfddsafdsafsafsdasaf6", 1000, 1000);
            qrcodeImageView.setImageBitmap(bitmap);
            //qrcodeImageView.setScaleType(ImageView.ScaleType.FIT_XY);
        } catch (Exception e) {
            //
        }


        return v;
    }
}
