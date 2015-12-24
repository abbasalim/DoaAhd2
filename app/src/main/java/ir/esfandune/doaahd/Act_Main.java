package ir.esfandune.doaahd;

import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ir.esfandune.database.DBAdapter;
import ir.esfandune.database.Doa;
import ir.esfandune.database.DoaAdapter;
import ir.esfandune.database.List_itm;
import ir.esfandune.database.List_itm_Adapter;


public class Act_Main extends AppCompatActivity {
    List<Doa> doa_itm;
    DBAdapter db;
    static MediaPlayer mp;
    static ListView main_ls;
    FloatingActionButton fab;
    static int val = 0;
    static Timer t;
    static TimerTask task;
    //////////////
    private DrawerLayout mDrawerLayout;
    Toolbar toolbar;
    Typeface font;
    /////////////
    private Handler mHandler = new Handler();
    private Utilities utils;
    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            //  long totalDuration = mp.getDuration();
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
                Pattern regex = Pattern.compile("(?s)<"+utils.milliSecondsToTimer(currentDuration).toString()+">\\s(.*)</"+utils.milliSecondsToTimer(currentDuration).toString()+">");
                Matcher regexMatcher = regex.matcher(text);
                while (regexMatcher.find()) {
                    for (int i = 1; i <= regexMatcher.groupCount(); i++) {
                        String ls_pos =regexMatcher.group(i).replaceAll("\\n", "");
                        int  a= Integer.valueOf(ls_pos.toString().trim());
                        //Log.i(DBAdapter.TAG	,i+ "<><><<><><<"+ls_pos+"<<><<><><");
                        main_ls.setSelection(a+1);
                    }
                }
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), "" + e.toString(), Toast.LENGTH_LONG).show();
            }
            mHandler.postDelayed(this, 100);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act__main);

        main_ls = (ListView) findViewById(R.id.main_list);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        font = Typeface.createFromAsset(getAssets(), "BROYA_rg.TTF");
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        db = new DBAdapter(getBaseContext());
        fab =(FloatingActionButton) findViewById(R.id.fab_play);
        utils = new Utilities();
        mp = new MediaPlayer();
        //
        main_ls.setFastScrollEnabled(true);
        main_ls.setVerticalScrollbarPosition(View.SCROLLBAR_POSITION_LEFT);
        main_ls.setAlpha(0);
        LayoutInflater inflater = getLayoutInflater();
        View m_headerView = inflater.inflate(R.layout.lst_hdr_ftr, main_ls, false);
        m_headerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //do something
            }
        });
        main_ls.addHeaderView(m_headerView);
        main_ls.addFooterView(m_headerView);
        //
        db.open();
        doa_itm = db.getAllItem();
        if (doa_itm.size() == 0) {
            try {
                String destPath = "/data/data/" + getPackageName()+ "/databases";
                CopyDB(getAssets().open("db_doaahd"),new FileOutputStream(destPath + "/db_doaahd"));
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
        main_ls.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (fab.isHidden()) {
                    toolbar.animate().alpha(1f).setInterpolator(new AccelerateInterpolator(2)).start();
                    valueAnim(0, toolbar.getHeight());
                    fab.show(true);

                } else {
                    toolbar.animate().alpha(0).setInterpolator(new DecelerateInterpolator(2)).start();
                    valueAnim(toolbar.getHeight(), 0);
                    fab.hide(true);
                }

            }

        });
        NavigationList();
        TextView toolbarTitle = (TextView) findViewById(R.id.toolbarTitle);
        toolbarTitle.setTypeface(font);

            toolbar.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                        toolbar.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    } else {
                        toolbar.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                   // valueAnim(0, toolbar.getHeight());
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.MATCH_PARENT);
                    params.topMargin =  toolbar.getHeight();
                    main_ls.setLayoutParams(params);
                    main_ls.animate().alpha(1).setStartDelay(500).setDuration(300).start();
                }
            });


    }
    public void refreshDisplay() {
        ArrayAdapter<Doa> adapter = new DoaAdapter(Act_Main.this, doa_itm);
        main_ls.setAdapter(adapter);

    }
    public void NavigationList(){
        ListView lst = (ListView) findViewById(R.id.list_drawer);
        LayoutInflater inflater = getLayoutInflater();
        //header
        View m_headerView = inflater.inflate(R.layout.mk_header, lst, false);
        m_headerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //do something
            }
        });
        lst.addHeaderView(m_headerView);
        //footer
        m_headerView = inflater.inflate(R.layout.mk_footer, lst, false);
        m_headerView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //do something
            }
        });
        final SharedPreferences sh = Act_Main.this.getSharedPreferences("setting", 0);

        final TextView mk_ftr_txt = (TextView) m_headerView.findViewById(R.id.mk_ftr_txt);
        Typeface broya = Typeface.createFromAsset(Act_Main.this.getAssets(), "BROYA_rg.TTF");
        mk_ftr_txt.setTypeface(broya);
        mk_ftr_txt.setText(sh.getInt("count", 0)+"");

        ImageView mk_ftr_plus =  (ImageView) m_headerView.findViewById(R.id.mk_ftr_plus);
        mk_ftr_plus.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                int size = sh.getInt("count", 0)+1;
                mk_ftr_txt.setText(size+"");

                SharedPreferences.Editor sh_et = sh.edit();
                sh_et.putInt("count", size);
                sh_et.commit();

            }
        });
        ImageView mk_ftr_mi =  (ImageView) m_headerView.findViewById(R.id.mk_ftr_mi);
        mk_ftr_mi.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                int size = sh.getInt("count", 0)-1;
                if (sh.getInt("count", 0) > 0) {
                    mk_ftr_txt.setText(size+"");
                    SharedPreferences.Editor sh_et = sh.edit();
                    sh_et.putInt("count", size);
                    sh_et.commit();
                }
            }
        });

        //list view
        lst.addFooterView(m_headerView);
        lst.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onListItemClick(position);

            }
                });



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
        n_d.settitle("درباره سازنده");
        n_d.setimg("mk_ico_abt");
        doa_itm.add(n_d);

        ArrayAdapter<List_itm> adapter = new List_itm_Adapter(Act_Main.this, doa_itm);
        lst.setAdapter(adapter);

    }
    public void onListItemClick(int number) {
        SharedPreferences sh = getBaseContext().getSharedPreferences("setting", 0);
        SharedPreferences.Editor sh_et = sh.edit();
        switch (number) {
            case 1:
                //mTitle = getString(R.string.title_section1);
                if (sh.getInt("font_size", 24) == 24) {
                    sh_et.putInt("font_size", 30);
                }else {
                    sh_et.putInt("font_size", 24);
                }

                break;
            case 2:
                if (sh.getString("font_type", "Otaha.TTF").equalsIgnoreCase("Otaha.TTF")) {
                    Log.i("asd", "if");
                    sh_et.putString("font_type", "a-JannatLT-Regular.ttf");
                }else {
                    Log.i("asd", "else");
                    sh_et.putString("font_type","Otaha.TTF");
                }

                break;
            case 3:

                alertAbt();
                break;
        }
        sh_et.commit();

        mDrawerLayout.closeDrawers();
        refreshDisplay();

    }
    public void CopyDB(InputStream inputStream, OutputStream outputStream)throws IOException {
        // ---copy 1K bytes at a time---
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, length);
        }
        inputStream.close();
        outputStream.close();
    }
    public void onFabClick( View v){
        final FloatingActionButton fab = ((FloatingActionButton) v);
        if(mp.isPlaying()){
            fab.setImageResource(R.drawable.ic_action_play);
            mp.pause();
            t.cancel();
            task.cancel();
        } else {
            fab.setProgress(fab.getProgress(),true);
            updateProgressBar();
            mp.start();
            final  int total = 605000;///time music be milisaniye 605000
            fab.setImageResource(R.drawable.ic_action_pause);
            t = new Timer();
            task = new TimerTask() {
                @Override
                public void run() {
                    Act_Main.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (val>=total/10){
                                t.cancel();
                                val=0;
                                fab.setImageResource(R.drawable.ic_action_play);
                                fab.setProgress(100, false);
                            }
                            else val++;
                            int darsad = (mp.getCurrentPosition() * 100) / total;
                            fab.setProgress(darsad, false);
                            //Log.i("Now", "val=" + val + " | darsad=" + darsad);
                        }
                    });
                }
            };
            t.scheduleAtFixedRate(task, 0, 10);
        }
    }
    public void onMenuClick(View v){
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
                }catch (Exception e){
                    Toast.makeText(Act_Main.this,"خطای 131272:\n از نصب بودن نرم افزاری جهت ارسال ایمیل برروی دستگاهتان مطمئن شوید",Toast.LENGTH_LONG).show();
                }


            }
        });
        new AlertDialog.Builder(Act_Main.this).setView(view).show();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
          /*  mp.pause();
            t.cancel();
            task.cancel();
            mHandler.removeCallbacks(mUpdateTimeTask);*/
       System.exit(0);
    }
    ///
    private void valueAnim(int first,int last){
        final RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.MATCH_PARENT);
        ValueAnimator va = ValueAnimator.ofInt(first,last);
        va.setDuration(300);
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                Integer value = (Integer) animation.getAnimatedValue();
                params.topMargin =  value;
                main_ls.setLayoutParams(params);
            }
        });
        va.start();
    }
}
