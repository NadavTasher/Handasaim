package nadav.tasher.handasaim.tools.graphics;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;

import nadav.tasher.handasaim.activities.Main;
import nadav.tasher.handasaim.tools.TunnelHub;
import nadav.tasher.handasaim.values.Values;
import nadav.tasher.lightool.communication.Tunnel;
import nadav.tasher.lightool.graphics.views.DragNavigation;
import nadav.tasher.lightool.info.Device;

public class MessageBar extends LinearLayout {

    private ArrayList<String> messages;
    private ArrayList<TextView> messageViews;
    private int currentIndex = 0;
    private boolean alive = true;
    private DragNavigation dn;
    private Thread animate;
    private Activity a;

    public MessageBar(Activity context, ArrayList<String> messages, DragNavigation drag) {
        super(context);
        this.a = context;
        this.messages = messages;
        this.messageViews = new ArrayList<>();
        this.dn=drag;
        init();
    }

    private void init() {
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER);
        setBackground(Main.generateCoaster(getContext(), Values.messageColor));
        setPadding(10,10,10,10);
        setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Device.screenY(getContext())/10));

        for (int a = 0; a < messages.size(); a++) {
            TextView t=getTextView(messages.get(a), Main.getTextColor(getContext()),true);
            if(dn!=null){
                final int finalA = a;
                t.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ScrollView sv=new ScrollView(getContext());
                        sv.addView(getTextView(messages.get(finalA),Main.getTextColor(getContext()),false));
                        dn.setContent(sv);
                        dn.open(false);
                    }
                });

            }
            messageViews.add(t);
        }
        if (messageViews.size() > 1) {
            for (int b = 0; b < messageViews.size(); b++) {
                addView(messageViews.get(b));
                messageViews.get(b).setVisibility(View.GONE);
            }
            make();
        } else {
            if (messageViews.size() == 1) {
                addView(messageViews.get(0));
            } else {
                setVisibility(View.GONE);
            }
        }
    }

    private void make() {
        animate = new Thread(new Runnable() {

            void disappear() {
                ObjectAnimator disappear = ObjectAnimator.ofFloat(messageViews.get(currentIndex), View.ALPHA, 1, 0);
                disappear.setDuration(1000);
                disappear.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        messageViews.get(currentIndex).setVisibility(View.GONE);
                        next();
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {
                    }
                });
                disappear.start();
            }

            void go() {
                messageViews.get(currentIndex).setVisibility(View.VISIBLE);
                messageViews.get(currentIndex).setAlpha(0);
                appear();
            }

            void next() {
                if (currentIndex < messageViews.size() - 1) {
                    currentIndex++;
                } else {
                    currentIndex = 0;
                }
                go();
            }

            void appear() {
                ObjectAnimator appear = ObjectAnimator.ofFloat(messageViews.get(currentIndex), View.ALPHA, 0, 1);
                appear.setDuration(1000);
                appear.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        pause();
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {
                    }
                });
                appear.start();
            }

            void pause() {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        disappear();
                    }
                }, 1000);
            }

            @Override
            public void run() {
                    a.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            go();
                        }
                    });

            }
        });
    }

    public void start() {
        if(animate!=null)
        animate.start();
    }

    public void stop() {
        alive = false;
    }

    private TextView getTextView(String t, int textColor,boolean singleLine) {
        final TextView v = new TextView(getContext());
        v.setTextColor(textColor);
        v.setTextSize((float) (Main.getFontSize(getContext())/1.5));
        v.setText(t);
        v.setGravity(Gravity.CENTER);
        v.setTypeface(Main.getTypeface(getContext()));
        v.setSingleLine(singleLine);
        v.setEllipsize(TextUtils.TruncateAt.END);
        v.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        TunnelHub.textColorChangeTunnle.addReceiver(new Tunnel.OnTunnel<Integer>() {
            @Override
            public void onReceive(Integer response) {
                v.setTextColor(response);
            }
        });
        return v;
    }
}
