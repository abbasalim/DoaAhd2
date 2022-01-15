package ir.esfandune.doaahd

import android.graphics.Typeface
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.wear.widget.WearableLinearLayoutManager
import androidx.wear.widget.WearableRecyclerView
import ir.esfandune.database.*
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern

class Act_Main : AppCompatActivity() {
    var mp: MediaPlayer? = null
    var main_ls: WearableRecyclerView? = null
    var doa_itm: List<Doa>? = null
    var db: DBAdapter? = null
    var fab_play: ImageView? = null
    var font: Typeface? = null
    var adapter: Rc_doaAdapter? = null

    /////////////
    var totalTime: TextView? = null
    var currentTime: TextView? = null
    var musicPrgbar: SeekBar? = null

    ///
    private val mHandler = Handler()
    private var utils: Utilities? = null
    private val mUpdateTimeTask: Runnable = object : Runnable {
        override fun run() {
            //  long totalDuration = mp.getDuration();
            if (mp!!.isPlaying) gotoLine()
            mHandler.postDelayed(this, 250)
        }
    }

    fun miliSecondFormat(millis: Int): String {
        return if (TimeUnit.MILLISECONDS.toHours(millis.toLong()) > 1) String.format(
            "%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis.toLong()),
            TimeUnit.MILLISECONDS.toMinutes(millis.toLong()) % TimeUnit.HOURS.toMinutes(
                1
            ),
            TimeUnit.MILLISECONDS.toSeconds(millis.toLong()) % TimeUnit.MINUTES.toSeconds(
                1
            )
        ) else String.format(
            "%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(millis.toLong()),
            TimeUnit.MILLISECONDS.toSeconds(millis.toLong()) % TimeUnit.MINUTES.toSeconds(
                1
            )
        )
    }

    private fun gotoLine() {
        var currentDuration = mp!!.currentPosition.toLong()
        val assetManager = assets
        val input: InputStream
        try {
            input = assetManager.open("sub.txt")
            val size = input.available()
            val buffer = ByteArray(size)
            input.read(buffer)
            input.close()
            val text = String(buffer)
            //در هر دفعه مگرده ببینه ثانیه الان صوت درحال پخش با کدوم عدد در تکست فایله برابری مکنه اسکرول مکنه به اونجا
            var nowPlayingTime = utils!!.milliSecondsToTimer(currentDuration)
            var regex = Pattern.compile("(?s)<$nowPlayingTime>\\s(.*)</$nowPlayingTime>")
            var regexMatcher = regex.matcher(text)
            //اگر جایی که هستیم ثانیه اش با هیچ یک از موقعیت های متن برابر نیست به وسیله کد زیر میگرده تا ایتم قبلیش رو پیدا کنه و اسکرول به اونجا بشه
            while (!regexMatcher.find()) {
                currentDuration = currentDuration - 1000
                nowPlayingTime = utils!!.milliSecondsToTimer(currentDuration)
                regex = Pattern.compile("(?s)<$nowPlayingTime>\\s(.*)</$nowPlayingTime>")
                regexMatcher = regex.matcher(text)
            }
            //                while (regexMatcher.find()) {
            for (i in 1..regexMatcher.groupCount()) {
                val ls_pos = regexMatcher.group(i).replace("\\n".toRegex(), "")
                val a = Integer.valueOf(ls_pos.trim { it <= ' ' })
                if (PerPlayingItmPost != a) {
                    adapter!!.notifyItemChanged(PerPlayingItmPost)
                    adapter!!.notifyItemChanged(a)
                }
                PerPlayingItmPost =
                    PlayingItemPost //چون هر 500 ثانیه بروز مشه این دوتا ممکنه یکی هم بشه
                PlayingItemPost = a
                (main_ls!!.layoutManager as WearableLinearLayoutManager?)!!.scrollToPositionWithOffset(
                    a, 0
                )
            }
            //                }
        } catch (e: IOException) {
            Toast.makeText(applicationContext, "" + e.toString(), Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_act__main)
        main_ls = findViewById(R.id.main_list)
        font = Typeface.createFromAsset(assets, "IRANSansMobile(FaNum).ttf")
        db = DBAdapter(baseContext)
        fab_play = findViewById(R.id.fab_play)
        utils = Utilities()
        mp = MediaPlayer()
        //
        initMusicBar()
        main_ls?.verticalScrollbarPosition = View.SCROLLBAR_POSITION_LEFT
        db!!.open()
        doa_itm = db!!.allItem
        db!!.close()
        if (doa_itm?.size == 0) {
            try {
                copyDataBase()
                Log.i("esfandune", "database copy shod")
                db!!.open()
                doa_itm = db!!.allItem
                db!!.close()
                refreshDisplay()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } // end if
        else {
            refreshDisplay()
        }
        mp = MediaPlayer.create(applicationContext, R.raw.audiofile)
        mp?.setOnCompletionListener { pauseAudio() }

    }

    private fun initMusicBar() {
        musicPrgbar = findViewById(R.id.musicPrgbar)
        totalTime = findViewById(R.id.totalTime)
        currentTime = findViewById(R.id.currentTime)
        totalTime?.typeface = (font)
        currentTime?.typeface = (font)
        musicPrgbar?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    mp!!.seekTo(progress.toInt())
                    updateProgressBar()
                }
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {

            }

            override fun onStopTrackingTouch(p0: SeekBar?) {

            }

        })
    }

    private fun refreshDisplay() {
        if (adapter == null) {
            adapter = doa_itm?.let { Rc_doaAdapter(this@Act_Main, it) }
//            main_ls!!.layoutManager = WearableLinearLayoutManager(this)

            main_ls?.isEdgeItemsCenteringEnabled = false
            main_ls?.layoutManager =
                WearableLinearLayoutManager(this, CustomScrollingLayoutCallback())

            main_ls!!.adapter = adapter
        } else {
            val lastFirstVisiblePosition =
                (main_ls!!.layoutManager as WearableLinearLayoutManager?)!!.findFirstVisibleItemPosition()
            adapter = doa_itm?.let { Rc_doaAdapter(this@Act_Main, it) }
            main_ls!!.adapter = adapter
            main_ls!!.layoutManager!!.scrollToPosition(lastFirstVisiblePosition)
        }

    }

    class CustomScrollingLayoutCallback : WearableLinearLayoutManager.LayoutCallback() {
        override fun onLayoutFinished(child: View, parent: RecyclerView) {
            child.apply {
                scaleX = 1f
                scaleY = 1f
            }
        }
    }


    fun onFabClick(v: View) {
        if (mp!!.isPlaying)
            pauseAudio()
        else
            PlayAudio()
    }

    private fun pauseAudio() {
        fab_play?.setImageResource(R.drawable.ic_round_play_arrow_24)
        mp!!.pause()
        t!!.cancel()
        task!!.cancel()
    }

    private fun PlayAudio() {
        mp!!.start()
        val total = 605000 ///time music be milisaniye 605000
        fab_play?.setImageResource(R.drawable.ic_round_pause_24)
        musicPrgbar!!.max = total
        updateProgressBar()
        t = Timer()
        task = object : TimerTask() {
            override fun run() {
                runOnUiThread {
                    //                            if (val>=total/10){
                    if (mp!!.currentPosition >= total) {
                        `val` = 0
                        t!!.cancel()
                    } else {
                        `val`++
                        val darsad = mp!!.currentPosition * 100 / total
                    }
                    musicPrgbar!!.progress = mp!!.currentPosition
                    totalTime!!.text = miliSecondFormat(total)
                    currentTime!!.text = miliSecondFormat(mp!!.currentPosition)
                    //Log.i("Now", "val=" + val + " | darsad=" + darsad);
                }
            }
        }
        t!!.scheduleAtFixedRate(task, 0, 10)
    }

    fun updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 100)
    }

    /* override fun onBackPressed()
             super.onBackPressed()
             System.exit(0)
     }*/

    override fun onStop() {
        super.onStop()
        if (mp!!.isPlaying)
            pauseAudio()
    }

    companion object {
        @JvmField
        var PlayingItemPost = 0
        var PerPlayingItmPost = 0
        var `val` = 0
        var t: Timer? = null
        var task: TimerTask? = null
    }

    @Throws(IOException::class)
    private fun copyDataBase() {
        // Open your local db as the input stream
        val myInput = assets.open(DBAdapter.DATABASE_NAME)
        // Path to the just created empty db
        val outFileName = getDatabasePath(DBAdapter.DATABASE_NAME)
        // Open the empty db as the output stream
        val myOutput: OutputStream = FileOutputStream(outFileName)
        // transfer bytes from the inputfile to the outputfile
        val buffer = ByteArray(1024)
        var length: Int
        while (myInput.read(buffer).also { length = it } > 0) {
            myOutput.write(buffer, 0, length)
        }
        // Close the streams
        myOutput.flush()
        myOutput.close()
        myInput.close()
    }


}