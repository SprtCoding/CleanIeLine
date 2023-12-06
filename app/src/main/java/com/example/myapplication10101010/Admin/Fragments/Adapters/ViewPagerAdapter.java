package com.example.myapplication10101010.Admin.Fragments.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.example.myapplication10101010.R;

public class ViewPagerAdapter extends PagerAdapter {
    Context context;
    int images[] = {
            R.drawable.your_order,
            R.drawable.we_collect,
            R.drawable.we_clean,
            R.drawable.we_dedliver
    };

    int heading[] = {
            R.string.your_order,
            R.string.we_collect,
            R.string.we_clean,
            R.string.we_deliver
    };
    int description[] = {
            R.string.your_order_desc,
            R.string.we_collect_desc,
            R.string.we_clean_desc,
            R.string.we_deliver_desc
    };

    public ViewPagerAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return heading.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (LinearLayout) object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = layoutInflater.inflate(R.layout.slider_layout, container, false);

        ImageView slideImg = (ImageView) v.findViewById(R.id.titleImg);
        TextView titleText = (TextView) v.findViewById(R.id.textTitle);
        TextView textDesc = (TextView) v.findViewById(R.id.textDescription);

        slideImg.setImageResource(images[position]);
        titleText.setText(heading[position]);
        textDesc.setText(description[position]);

        container.addView(v);

        return v;

    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((LinearLayout)object);
    }
}
