package ir.esfandune.doaahd;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ir.esfandune.database.DBAdapter;
import ir.esfandune.database.Doa;
import ir.esfandune.database.List_itm;
import ir.esfandune.database.List_itm_Adapter;
import ir.esfandune.database.Rc_doaAdapter;


public class Act_Main extends AppCompatActivity {
    public static int PlayingItemPost, PerPlayingItmPost;
    static MediaPlayer mp;
    static RecyclerView main_ls;
    static int val = 0;
    static Timer t;
    static TimerTask task;
    List<Doa> doa_itm;
    DBAdapter db;
    FloatingActionButton fab;
    Toolbar toolbar;
    Typeface font;
    Rc_doaAdapter adapter;
    SharedPreferences shpSettinh;
    ListView list_drawer;
    /////////////
    TextView totalTime, currentTime;
    RelativeLayout rl_musicBar;
    SeekBar musicPrgbar;
    //////////////
    FrameLayout ll_fontSize;
    SeekBar seekbar;
    ///

    private DrawerLayout mDrawerLayout;
    ///
    private Handler mHandler = new Handler();
    private Utilities utils;
    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            //  long totalDuration = mp.getDuration();
            if (mp.isPlaying())
                gotoLine();
            mHandler.postDelayed(this, 1000);
        }
    };

    public static String miliSecondFormat(int millis) {
        if (TimeUnit.MILLISECONDS.toHours(millis) > 1)
            return String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                    TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
                    TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1));
        else
            return String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(millis),
                    TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1));
    }

    private void gotoLine() {
        long currentDuration = mp.getCurrentPosition();
        AssetManager assetManager = getAssets();
        InputStream input;
        try {
            input = assetManager.open("sub.txt");
            int size = input.available();
            byte[] buffer = new byte[size];
            input.read(buffer);
            input.close();
            String text = new String(buffer);
            //در هر دفعه مگرده ببینه ثانیه الان صوت درحال پخش با کدوم عدد در تکست فایله برابری مکنه اسکرول مکنه به اونجا
            String nowPlayingTime = utils.milliSecondsToTimer(currentDuration).toString();
            Pattern regex = Pattern.compile("(?s)<" + nowPlayingTime + ">\\s(.*)</" + nowPlayingTime + ">");
            Matcher regexMatcher = regex.matcher(text);
            //اگر جایی که هستیم ثانیه اش با هیچ یک از موقعیت های متن برابر نیست به وسیله کد زیر میگرده تا ایتم قبلیش رو پیدا کنه و اسکرول به اونجا بشه
            while (!regexMatcher.find()) {
                currentDuration = currentDuration - 1000;
                nowPlayingTime = utils.milliSecondsToTimer(currentDuration).toString();
                regex = Pattern.compile("(?s)<" + nowPlayingTime + ">\\s(.*)</" + nowPlayingTime + ">");
                regexMatcher = regex.matcher(text);
            }
//                while (regexMatcher.find()) {
            for (int i = 1; i <= regexMatcher.groupCount(); i++) {
                String ls_pos = regexMatcher.group(i).replaceAll("\\n", "");
                int a = Integer.valueOf(ls_pos.toString().trim());
                PerPlayingItmPost = PlayingItemPost;//چون هر 500 ثانیه بروز مشه این دوتا ممکنه یکی هم بشه
                PlayingItemPost = a;
                ((LinearLayoutManager) main_ls.getLayoutManager()).scrollToPositionWithOffset(a, 20);
                main_ls.getAdapter().notifyItemChanged(PerPlayingItmPost);
                main_ls.getAdapter().notifyItemChanged(PlayingItemPost);

            }
//                }
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), "" + e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        shpSettinh = getSharedPreferences("setting", MODE_PRIVATE);
        setTheme(shpSettinh.getInt("theme", R.style.AppBaseTheme));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act__main);

        if (shpSettinh.getBoolean("keepOnScreen", true))
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        else
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        CoordinatorLayout activity_main = (CoordinatorLayout) findViewById(R.id.activity_main);
        boolean day = shpSettinh.getInt("theme", R.style.AppBaseTheme) == R.style.AppBaseTheme;
        activity_main.setBackgroundColor(day ? getResources().getColor(R.color.bg_d) : getResources().getColor(R.color.bg_n));
        ((ImageView) findViewById(R.id.shamse)).setColorFilter(day ? Color.BLACK : Color.WHITE);


        main_ls = (RecyclerView) findViewById(R.id.main_list);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        font = Typeface.createFromAsset(getAssets(), "IRANSansMobile(FaNum).ttf");
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        db = new DBAdapter(getBaseContext());
        fab = (FloatingActionButton) findViewById(R.id.fab_play);
        ll_fontSize = (FrameLayout) findViewById(R.id.ll_fontSize);
        seekbar = (SeekBar) findViewById(R.id.font_size);
        utils = new Utilities();
        mp = new MediaPlayer();
        //
        initMusicBar();

        main_ls.setVerticalScrollbarPosition(View.SCROLLBAR_POSITION_LEFT);
        db.open();
        doa_itm = db.getAllItem();
        if (doa_itm.size() == 0) {
            try {
                String destPath = "/data/data/" + getPackageName() + "/databases";
                CopyDB(getAssets().open("db_doaahd"), new FileOutputStream(destPath + "/db_doaahd"));
                Log.i("esfandune", "database copy shod");
                doa_itm = db.getAllItem();
                refreshDisplay();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }// end if
        else {
            refreshDisplay();
        }
        try {
            AssetFileDescriptor afd = getAssets().openFd("AudioFile.mp3");
            mp.setDataSource(afd.getFileDescriptor());
            mp.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        NavigationList();
        TextView toolbarTitle = (TextView) findViewById(R.id.toolbarTitle);
        toolbarTitle.setTypeface(font);


        RotateAnimation rotateAnimation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setRepeatCount(Animation.INFINITE);
        rotateAnimation.setInterpolator(new LinearInterpolator());
        rotateAnimation.setDuration(40000);
        findViewById(R.id.shamse).startAnimation(rotateAnimation);
    }

    private void initMusicBar() {
        musicPrgbar = (SeekBar) findViewById(R.id.musicPrgbar);
        totalTime = (TextView) findViewById(R.id.totalTime);
        currentTime = (TextView) findViewById(R.id.currentTime);
        totalTime.setTypeface(font);
        currentTime.setTypeface(font);
        rl_musicBar = (RelativeLayout) findViewById(R.id.rl_musicBar);
        rl_musicBar.setVisibility(View.GONE);
        musicPrgbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mp.seekTo(progress);
                    updateProgressBar();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    public void refreshDisplay() {

        if (adapter == null) {
            adapter = new Rc_doaAdapter(Act_Main.this, doa_itm);
            main_ls.setLayoutManager(new LinearLayoutManager(this));
            main_ls.setAdapter(adapter);
        } else {
            int lastFirstVisiblePosition = ((LinearLayoutManager) main_ls.getLayoutManager()).findFirstVisibleItemPosition();
            adapter = new Rc_doaAdapter(Act_Main.this, doa_itm);
            main_ls.setAdapter(adapter);
            main_ls.getLayoutManager().scrollToPosition(lastFirstVisiblePosition);
        }
        RecyclerView.ItemAnimator animator = main_ls.getItemAnimator();
        if (animator instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
        }
    }

    public void NavigationList() {
        list_drawer = (ListView) findViewById(R.id.list_drawer);
        LayoutInflater inflater = getLayoutInflater();
        //header
        View m_headerView = inflater.inflate(R.layout.mk_header, list_drawer, false);
        m_headerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //do something
            }
        });
        list_drawer.addHeaderView(m_headerView);
        //footer
        m_headerView = inflater.inflate(R.layout.mk_footer, list_drawer, false);
        m_headerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //do something
            }
        });
        final SharedPreferences sh = Act_Main.this.getSharedPreferences("setting", 0);

        final TextView mk_ftr_txt = (TextView) m_headerView.findViewById(R.id.mk_ftr_txt);
        Typeface broya = Typeface.createFromAsset(Act_Main.this.getAssets(), "BROYA_rg.TTF");
        mk_ftr_txt.setTypeface(broya);
        mk_ftr_txt.setText(sh.getInt("count", 0) + "");

        ImageView mk_ftr_plus = (ImageView) m_headerView.findViewById(R.id.mk_ftr_plus);
        mk_ftr_plus.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                int size = sh.getInt("count", 0) + 1;
                mk_ftr_txt.setText(size + "");

                SharedPreferences.Editor sh_et = sh.edit();
                sh_et.putInt("count", size);
                sh_et.commit();

            }
        });
        ImageView mk_ftr_mi = (ImageView) m_headerView.findViewById(R.id.mk_ftr_mi);
        mk_ftr_mi.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                int size = sh.getInt("count", 0) - 1;
                if (sh.getInt("count", 0) > 0) {
                    mk_ftr_txt.setText(size + "");
                    SharedPreferences.Editor sh_et = sh.edit();
                    sh_et.putInt("count", size);
                    sh_et.commit();
                }
            }
        });

        //list view
        list_drawer.addFooterView(m_headerView);
        list_drawer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onListItemClick(position);

            }
        });


        rfrshNavListAdapter();

    }

    private void rfrshNavListAdapter() {
        List<List_itm> doa_itm = new ArrayList<List_itm>();

        List_itm n_d = new List_itm();
        n_d.settitle("تغییر اندازه قلم");
        n_d.setimg("mk_ico_font");
        doa_itm.add(n_d);

        n_d = new List_itm();
        n_d.settitle("تغییر قلم");
        n_d.setimg("mk_ico_font");
        doa_itm.add(n_d);

        n_d = new List_itm();
        if (shpSettinh.getInt("theme", R.style.AppBaseTheme) == R.style.AppBaseTheme)
            n_d.settitle("حالت شب");
        else
            n_d.settitle("حالت روز");
        n_d.setimg("mk_ico_font");
        doa_itm.add(n_d);

        n_d = new List_itm();
        String scStatus = " (" + (shpSettinh.getBoolean("keepOnScreen", true) ? "فعال است" : "غیرفعال است") + ")";
        n_d.settitle("روشن ماندن صفحه" + scStatus);
        n_d.setimg("mk_ico_font");
        doa_itm.add(n_d);

        n_d = new List_itm();
        n_d.settitle("درباره سازنده");
        n_d.setimg("mk_ico_abt");
        doa_itm.add(n_d);

        ArrayAdapter<List_itm> adapter = new List_itm_Adapter(Act_Main.this, doa_itm);
        list_drawer.setAdapter(adapter);
    }

    public void onListItemClick(int number) {
        SharedPreferences sh = getBaseContext().getSharedPreferences("setting", 0);
        final SharedPreferences.Editor sh_et = sh.edit();
        switch (number) {
            case 1:
                ConfigFontSizeLL();
                mDrawerLayout.closeDrawers();
                break;
            case 2:
                if (sh.getString("font_type", "IRANSansMobile(FaNum).ttf").equalsIgnoreCase("a-JannatLT-Regular.ttf")) {
                    sh_et.putString("font_type", "IRANSansMobile(FaNum).ttf").commit();
                } else {
                    sh_et.putString("font_type", "a-JannatLT-Regular.ttf").commit();
                }
                refreshDisplay();
                break;
            case 3:
                String Mode;
                final int style;
                if (sh.getInt("theme", R.style.AppBaseTheme) == R.style.AppBaseTheme) {
                    style = R.style.AppBaseTheme_night;
                    Mode = "شب";
                } else {
                    style = R.style.AppBaseTheme;
                    Mode = "روز";
                }
                new AlertDialog.Builder(this).setPositiveButton("اجرای مجدد", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        sh_et.putInt("theme", style).commit();
                        Intent mStartActivity = new Intent(Act_Main.this, Act_Main.class);
                        int mPendingIntentId = 123456;
                        PendingIntent mPendingIntent = PendingIntent.getActivity(Act_Main.this, mPendingIntentId, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
                        AlarmManager mgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
                        System.exit(0);
                    }
                }).setNegativeButton("فعلا بیخیال", null).setTitle("نکته").setMessage("برای تغییر حالت به " + Mode + " نیاز به اجرای مجدد برنامه است.").show();

                break;
            case 4:
                if (shpSettinh.getBoolean("keepOnScreen", true)) {
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                    shpSettinh.edit().putBoolean("keepOnScreen", false).commit();
                }else {
                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                    shpSettinh.edit().putBoolean("keepOnScreen", true).commit();
                }

                rfrshNavListAdapter();
                break;
            case 5:
                alertAbt();
                break;
        }


    }

    public void CopyDB(InputStream inputStream, OutputStream outputStream) throws IOException {
        // ---copy 1K bytes at a time---
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, length);
        }
        inputStream.close();
        outputStream.close();
    }

    public void onFabClick(View v) {
        final FloatingActionButton fab = ((FloatingActionButton) v);
        if (mp.isPlaying()) {
            fab.setImageResource(R.drawable.ic_action_play);
            mp.pause();
            t.cancel();
            task.cancel();
        } else {
            fab.setProgress(fab.getProgress(), true);
            mp.start();
            final int total = 605000;///time music be milisaniye 605000
            fab.setImageResource(R.drawable.ic_action_pause);
            musicPrgbar.setMax(total);
            updateProgressBar();
            t = new Timer();
            task = new TimerTask() {
                @Override
                public void run() {
                    Act_Main.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//                            if (val>=total/10){
                            if (mp.getCurrentPosition() >= total) {
                                val = 0;
                                fab.setImageResource(R.drawable.ic_action_play);
                                fab.setProgress(100, false);
                                fab.hideProgress();
                                rl_musicBar.animate().alpha(0).setDuration(300).setListener(new Animator.AnimatorListener() {
                                    @Override
                                    public void onAnimationStart(Animator animator) {

                                    }

                                    @Override
                                    public void onAnimationEnd(Animator animator) {
                                        rl_musicBar.setVisibility(View.GONE);
                                    }

                                    @Override
                                    public void onAnimationCancel(Animator animator) {

                                    }

                                    @Override
                                    public void onAnimationRepeat(Animator animator) {

                                    }
                                }).start();
                                t.cancel();
                            } else {
                                if (rl_musicBar.getVisibility() == View.GONE) {
                                    rl_musicBar.setVisibility(View.VISIBLE);
                                    rl_musicBar.animate().alpha(1).setDuration(300).setListener(null).start();
                                }
                                val++;
                                int darsad = (mp.getCurrentPosition() * 100) / total;
                                fab.setProgress(darsad, false);
                            }

                            musicPrgbar.setProgress(mp.getCurrentPosition());
                            totalTime.setText(miliSecondFormat(total));
                            currentTime.setText(miliSecondFormat(mp.getCurrentPosition()));
                            //Log.i("Now", "val=" + val + " | darsad=" + darsad);
                        }
                    });
                }
            };
            t.scheduleAtFixedRate(task, 0, 10);
        }
    }

    public void onMenuClick(View v) {
        mDrawerLayout.openDrawer(Gravity.RIGHT);
    }

    public void updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 100);
    }

    public void alertAbt() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.activity_about, null, false);

        TextView abt_txt = (TextView) view.findViewById(R.id.abt_txt);
        abt_txt.setTypeface(font);
        abt_txt = (TextView) view.findViewById(R.id.abt_title);
        abt_txt.setTypeface(font);
        ImageView img_abt = (ImageView) view.findViewById(R.id.abt_abt);
        img_abt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                try {
                    Intent intent = new Intent(Intent.ACTION_SENDTO);
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_SUBJECT, "");
                    intent.putExtra(Intent.EXTRA_TEXT, "");
                    intent.setData(Uri.parse("mailto:abbasali88m@yahoo.com"));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(Act_Main.this, "خطای 131272:\n از نصب بودن نرم افزاری جهت ارسال ایمیل برروی دستگاهتان مطمئن شوید", Toast.LENGTH_LONG).show();
                }


            }
        });
        new AlertDialog.Builder(Act_Main.this).setView(view).show();
    }

    private void ConfigFontSizeLL() {
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser)
                    shpSettinh.edit().putInt("font_size", progress).apply();
                refreshDisplay();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        seekbar.setProgress(shpSettinh.getInt("font_size", 18));
        if (ll_fontSize.getVisibility() == View.GONE) {
            ll_fontSize.setVisibility(View.VISIBLE);
            seekbar.animate().alpha(1).setDuration(300).setListener(null).start();
        } else hideLL_fontSize();
    }

    private void hideLL_fontSize() {
        seekbar.animate().alpha(0).setDuration(300).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                ll_fontSize.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        }).start();
    }

    @Override
    public void onBackPressed() {

        if (ll_fontSize.getVisibility() != View.GONE)
            hideLL_fontSize();
          /*  mp.pause();
            t.cancel();
            task.cancel();
            mHandler.removeCallbacks(mUpdateTimeTask);*/
        else {
            super.onBackPressed();
            System.exit(0);
        }
    }


    ///
    private void valueAnim(int first, int last) {
        final RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        ValueAnimator va = ValueAnimator.ofInt(first, last);
        va.setDuration(300);
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                Integer value = (Integer) animation.getAnimatedValue();
                params.topMargin = value;
                main_ls.setLayoutParams(params);
            }
        });
        va.start();
    }
}