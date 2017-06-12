package work.pengzhe.com.objectanim;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.Random;

public class MainActivity extends AppCompatActivity {


    Button btn;
    private PointF startPoint;
    private PointF endPoint;
    private Drawable[] drawables;
    private Interpolator[] interpolators;
    private LinearLayout rootView;
    int width;
    int height;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        WindowManager wm = this.getWindowManager();
        rootView = (LinearLayout) findViewById(R.id.ll);
        width = wm.getDefaultDisplay().getWidth();
        height = wm.getDefaultDisplay().getHeight();
        startPoint = new PointF(width / 2 - 50, height);
        endPoint = new PointF(width / 2 - 50, 0);
        init();


//        TypeEvaluator<PointF> typeEvaluator = new TypeEvaluator<PointF>() {
//            @Override
//            public PointF evaluate(float fraction, PointF startValue, PointF endValue) {
//                return new PointF(width / 2 - 50, height - fraction * height);
//            }
//        };


        btn = (Button) findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFlower();
            }
        });
    }

    private void init() {
        drawables = new Drawable[8];
        drawables[0] = getResources().getDrawable(R.mipmap.flower);
        drawables[1] = getResources().getDrawable(R.mipmap.flower_1);
        drawables[2] = getResources().getDrawable(R.mipmap.flower_2);
        drawables[3] = getResources().getDrawable(R.mipmap.flower_3);
        drawables[4] = getResources().getDrawable(R.mipmap.flower_4);
        drawables[5] = getResources().getDrawable(R.mipmap.flower_5);
        drawables[6] = getResources().getDrawable(R.mipmap.heart_1);
        drawables[7] = getResources().getDrawable(R.mipmap.heart_2);


        interpolators = new Interpolator[4];
        interpolators[0] = new AccelerateDecelerateInterpolator();
        interpolators[1] = new AccelerateInterpolator();
        interpolators[2] = new DecelerateInterpolator();
        interpolators[3] = new LinearInterpolator();
    }

    /**
     * 添加花朵
     */
    private void addFlower() {
        ImageView flower = new ImageView(this);
        flower.setLayoutParams(new ViewGroup.LayoutParams(100, 100));
        flower.setBackground(drawables[new Random().nextInt(drawables.length)]);
        rootView.addView(flower);
        startAnin(flower);
    }


    /**
     * 开启动画
     *
     * @param flower
     */
    private void startAnin(final ImageView flower) {
        final AnimatorSet animatorSet = new AnimatorSet();

        ObjectAnimator alphaAnim = ObjectAnimator.ofFloat(flower, "alpha", 0, 1);
        alphaAnim.setDuration(200);

        final ValueAnimator animator = ValueAnimator.ofObject(new MyTypeEvaluator(getPoint(0), getPoint(1)), startPoint, endPoint);
        animator.setDuration(4000);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                PointF pointF = (PointF) animation.getAnimatedValue();
                flower.setX(pointF.x);
                flower.setY(pointF.y);
            }
        });
        //  animator.start();
        animatorSet.play(alphaAnim).before(animator);
        animatorSet.start();
    }

    private PointF getPoint(int i) {
        PointF pointF = new PointF();
        pointF.x = new Random().nextFloat() * width;
        if (i == 0) {
            pointF.y = new Random().nextFloat() * 0.5f * height;
        } else {
            pointF.y = (new Random().nextFloat() * 0.5f * height) + 0.5f * height;
        }
        return pointF;
    }


    class MyTypeEvaluator implements TypeEvaluator<PointF> {

        private PointF pointF1, pointF2;

        public MyTypeEvaluator(PointF pointF1, PointF pointF2) {
            this.pointF1 = pointF1;
            this.pointF2 = pointF2;
        }

        @Override
        public PointF evaluate(float fraction, PointF startValue, PointF endValue) {
            //三阶贝塞尔曲线
            //B(t) = P0 * (1-t)^3 + 3 * P1 * (1-t)^2 + 3 * P2 * t^2 * (1-t) + P3 * t^3  ，其中 0 <= t <= 1
            float timeLeft = 1.0f - fraction;
            PointF pointF = new PointF();//结果
            pointF.x = timeLeft * timeLeft * timeLeft * (startValue.x)
                    + 3 * timeLeft * timeLeft * fraction * (pointF1.x)
                    + 3 * timeLeft * fraction * fraction * (pointF2.x)
                    + fraction * fraction * fraction * (endValue.x);

            pointF.y = timeLeft * timeLeft * timeLeft * (startValue.y)
                    + 3 * timeLeft * timeLeft * fraction * (pointF1.y)
                    + 3 * timeLeft * fraction * fraction * (pointF2.y)
                    + fraction * fraction * fraction * (endValue.y);
            return pointF;

        }
    }


}
