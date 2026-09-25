package com.glitchplays1.launcher;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class LauncherActivity extends Activity {
    private static final int[] CATCH_COLORS = {
            0xFF00E5FF, 0xFF7CFF3A, 0xFFFFE14A, 0xFFFF2EA6, 0xFFF4F4F4, 0xFFB14BFF
    };
    private static final String[] CATCH_GLYPHS = {"C", "\u25b2", "X", "T", "C", "H"};

    private View lockView;
    private View homeView;
    private TextView lockTime;
    private TextView lockDate;
    private TextView homeTime;
    private TextView homeDate;
    private GridLayout appGrid;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable tick = new Runnable() {
        @Override
        public void run() {
            updateClocks();
            handler.postDelayed(this, 1000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(Color.BLACK);
        getWindow().setNavigationBarColor(Color.BLACK);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        lockView = buildLock();
        homeView = buildHome();
        setContentView(lockView);
        loadApps();
    }

    @Override
    protected void onResume() {
        super.onResume();
        handler.removeCallbacks(tick);
        handler.post(tick);
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(tick);
    }

    @Override
    public void onBackPressed() {
        if (getContentView() == homeView) {
            setContentView(lockView);
        }
    }

    private View getContentView() {
        return ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
    }

    private void updateClocks() {
        Date now = new Date();
        String time = new SimpleDateFormat("h:mm", Locale.getDefault()).format(now);
        String date = new SimpleDateFormat("EEEE, MMMM d", Locale.getDefault()).format(now);
        String shortDate = new SimpleDateFormat("EEE, MMM d", Locale.getDefault()).format(now);
        if (lockTime != null) lockTime.setText(time);
        if (lockDate != null) lockDate.setText(date);
        if (homeTime != null) homeTime.setText(time);
        if (homeDate != null) homeDate.setText(shortDate);
    }

    private int dp(int value) {
        return Math.round(TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, value, getResources().getDisplayMetrics()));
    }

    private View buildLock() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.BLACK);
        root.setGravity(Gravity.CENTER_HORIZONTAL);
        root.setPadding(dp(24), dp(72), dp(24), dp(36));

        lockTime = new TextView(this);
        lockTime.setTextColor(Color.WHITE);
        lockTime.setTextSize(TypedValue.COMPLEX_UNIT_SP, 72);
        lockTime.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);
        lockTime.setGravity(Gravity.CENTER);
        root.addView(lockTime);

        lockDate = new TextView(this);
        lockDate.setTextColor(0xFFCCCCCC);
        lockDate.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        lockDate.setGravity(Gravity.CENTER);
        lockDate.setPadding(0, dp(4), 0, dp(28));
        root.addView(lockDate);

        LinearLayout stack = new LinearLayout(this);
        stack.setOrientation(LinearLayout.VERTICAL);
        stack.setGravity(Gravity.CENTER);
        for (int i = 0; i < CATCH_GLYPHS.length; i++) {
            TextView g = new TextView(this);
            g.setText(CATCH_GLYPHS[i]);
            g.setTextColor(CATCH_COLORS[i]);
            g.setTextSize(TypedValue.COMPLEX_UNIT_SP, 42);
            g.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);
            g.setGravity(Gravity.CENTER);
            g.setShadowLayer(18, 0, 0, CATCH_COLORS[i]);
            stack.addView(g);
        }
        root.addView(stack, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f));

        TextView hint = new TextView(this);
        hint.setText("Swipe up to unlock");
        hint.setTextColor(0xFFAAAAAA);
        hint.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        hint.setGravity(Gravity.CENTER);
        hint.setPadding(0, dp(16), 0, 0);
        root.addView(hint);

        root.setOnTouchListener(new SwipeUpListener() {
            @Override
            void onSwipeUp() {
                setContentView(homeView);
            }
        });
        return root;
    }

    private View buildHome() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.BLACK);
        root.setPadding(dp(16), dp(48), dp(16), dp(16));

        homeTime = new TextView(this);
        homeTime.setTextColor(Color.WHITE);
        homeTime.setTextSize(TypedValue.COMPLEX_UNIT_SP, 56);
        homeTime.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);
        homeTime.setGravity(Gravity.CENTER);
        root.addView(homeTime);

        homeDate = new TextView(this);
        homeDate.setTextColor(0xFFCCCCCC);
        homeDate.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        homeDate.setGravity(Gravity.CENTER);
        homeDate.setPadding(0, 0, 0, dp(12));
        root.addView(homeDate);

        ScrollView scroll = new ScrollView(this);
        appGrid = new GridLayout(this);
        appGrid.setColumnCount(4);
        appGrid.setUseDefaultMargins(true);
        scroll.addView(appGrid, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        root.addView(scroll, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f));
        return root;
    }

    private void loadApps() {
        PackageManager pm = getPackageManager();
        Intent query = new Intent(Intent.ACTION_MAIN, null);
        query.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> infos = pm.queryIntentActivities(query, 0);
        List<ResolveInfo> apps = new ArrayList<ResolveInfo>();
        String self = getPackageName();
        for (ResolveInfo info : infos) {
            if (info.activityInfo == null) continue;
            if (self.equals(info.activityInfo.packageName)) continue;
            apps.add(info);
        }
        Collections.sort(apps, new Comparator<ResolveInfo>() {
            @Override
            public int compare(ResolveInfo a, ResolveInfo b) {
                String left = String.valueOf(a.loadLabel(pm));
                String right = String.valueOf(b.loadLabel(pm));
                return left.compareToIgnoreCase(right);
            }
        });

        int cell = (getResources().getDisplayMetrics().widthPixels - dp(40)) / 4;
        for (final ResolveInfo info : apps) {
            LinearLayout cellView = new LinearLayout(this);
            cellView.setOrientation(LinearLayout.VERTICAL);
            cellView.setGravity(Gravity.CENTER_HORIZONTAL);
            cellView.setPadding(dp(6), dp(8), dp(6), dp(8));

            GradientDrawable box = new GradientDrawable();
            box.setColor(0xFF161616);
            box.setCornerRadius(dp(16));
            ImageView icon = new ImageView(this);
            Drawable d = info.loadIcon(pm);
            icon.setImageDrawable(d);
            icon.setBackground(box);
            icon.setPadding(dp(12), dp(12), dp(12), dp(12));
            LinearLayout.LayoutParams iconLp = new LinearLayout.LayoutParams(dp(64), dp(64));
            iconLp.gravity = Gravity.CENTER_HORIZONTAL;
            cellView.addView(icon, iconLp);

            TextView label = new TextView(this);
            label.setText(info.loadLabel(pm));
            label.setTextColor(0xFFDDDDDD);
            label.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
            label.setGravity(Gravity.CENTER);
            label.setMaxLines(2);
            label.setPadding(0, dp(6), 0, 0);
            cellView.addView(label);

            cellView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent launch = pm.getLaunchIntentForPackage(info.activityInfo.packageName);
                    if (launch != null) {
                        launch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(launch);
                    }
                }
            });

            GridLayout.LayoutParams lp = new GridLayout.LayoutParams();
            lp.width = cell;
            lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            appGrid.addView(cellView, lp);
        }
    }

    private abstract static class SwipeUpListener implements View.OnTouchListener {
        private float startY;
        abstract void onSwipeUp();
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                startY = event.getY();
                return true;
            }
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (startY - event.getY() > 120) onSwipeUp();
                return true;
            }
            return true;
        }
    }
}
