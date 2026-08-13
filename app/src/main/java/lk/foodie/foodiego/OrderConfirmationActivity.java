package lk.foodie.foodiego;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.appcompat.app.AppCompatActivity;
import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGImageView;
import java.io.InputStream;

public class OrderConfirmationActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_confirmation);

        // Load the SVG image
        SVGImageView svgImageView = findViewById(R.id.svg_image);
//        try {
//            InputStream inputStream = getResources().openRawResource(R.raw.food_svgrepo_com);
//            SVG svg = SVG.getFromInputStream(inputStream);
//            svgImageView.setSVG(svg);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        Animation animation1 = AnimationUtils.loadAnimation(OrderConfirmationActivity.this, R.anim.order_animation);
        // Create and start an animation
//        AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
//        alphaAnimation.setDuration(500);
//        alphaAnimation.setRepeatMode(AlphaAnimation.REVERSE);
//        alphaAnimation.setRepeatCount(AlphaAnimation.INFINITE);
        svgImageView.startAnimation(animation1);
        new Handler().postDelayed(()->{
            Intent intent = new Intent(OrderConfirmationActivity.this, MainActivity.class);
            startActivity(intent);
        },3000);
    }
}
