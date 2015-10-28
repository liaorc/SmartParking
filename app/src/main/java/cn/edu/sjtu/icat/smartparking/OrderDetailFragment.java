package cn.edu.sjtu.icat.smartparking;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Date;

/**
 * Created by Ruochen on 2015/10/21.
 */
public class OrderDetailFragment extends Fragment {
    private static final String TAG = "order_detail";
    public static final String EXTRA_ORDER = "order";
    private Order mOrder;


    public static OrderDetailFragment newInstance(Bundle arg) {
        OrderDetailFragment fragment = new OrderDetailFragment();
        fragment.setArguments(arg);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_order_detail, null);

        mOrder = getArguments().getParcelable(EXTRA_ORDER);

        Log.d(TAG, "QR: " + mOrder.getQRCode());
        Log.d(TAG, "name: " + mOrder.getParkInfo().getName());

        TextView parkName = (TextView)v.findViewById(R.id.order_detail_parkNameTextView);
        parkName.setText(mOrder.getParkInfo().getName());

        TextView submitTime = (TextView)v.findViewById(R.id.order_detail_submitTimeTextView);
        submitTime.setText(MiscUtils.getTimeDescription(mOrder.getSubmitTime(), new Date()));


        ImageView qrcodeImageView = (ImageView)v.findViewById(R.id.order_detail_qrcode);
        try {
            Bitmap bitmap = MiscUtils.str2Bitmap(mOrder.getQRCode(), 1000, 1000);
            qrcodeImageView.setImageBitmap(bitmap);
            //qrcodeImageView.setScaleType(ImageView.ScaleType.FIT_XY);
        } catch (Exception e) {
            e.printStackTrace();
        }


        return v;
    }
}
