package com.eigoninaritai.fragmentbasics

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import java.util.*

/**
 * フラグメントの基礎アプリのメインアクティビティ。
 */
class MainActivity : AppCompatActivity(), HeadlinesFragment.OnHeadlineSelectedListener, DatePickerDialogFragment.OnDateSetListener, TimePickerDialogFragment.OnTimeSetListener {
    companion object {
        /**
         * 呼び出したアクティビティから結果を受け取るためのリクエストコード。
         */
        val REQUEST_ARTICLE_POSITION: Int = 1

        /**
         * Bundleで使用する最後にフォーカスが当たっていたフラグメントの識別子。。
         */
        private val ARG_LAST_FOCUS: String = "com.eigoninaritai.fragmentbasics.LAST_FOCUS"
    }

    /**
     * ツーペイン表示かどうか。
     */
    private var isTwoPanes: Boolean = false

    /**
     * アクティビティを作成する。
     *
     * @param savedInstanceState 特定の状況下でアクティビティ廃棄直前に保存された情報。
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.action_toolbar))

        isTwoPanes = resources.getBoolean(R.bool.is_two_panes)
        if (savedInstanceState == null) {
            // 通知から起動された場合、通知から取得した情報で記事を表示する
            if (
                intent.hasExtra(ReportingElapsedTaskBroadcastReceiver.ARG_TASK) &&
                intent.hasExtra(ArticleFragment.ARG_POSITION)
            ) {
                val currentArticlePosition = intent.getIntExtra(ArticleFragment.ARG_POSITION, ArticleFragment.NOT_SELECTED_ARTICLE)
                if (isTwoPanes) {
                    updateArticleView(currentArticlePosition)
                }
                else {
                    startArticleActivity(currentArticlePosition)
                }
            }
        }
        else {
            // 保存された情報があるかつワンペインの場合、最後にフォーカスがあったフラグメントを表示する
            if (!isTwoPanes) {
                if (
                    savedInstanceState.containsKey(ArticleFragment.ARG_POSITION) &&
                    savedInstanceState.containsKey(ARG_LAST_FOCUS)
                ) {
                    val lastFocus = savedInstanceState.getInt(ARG_LAST_FOCUS)
                    when (lastFocus) {
                        Focus.ARTICLE.value -> {
                            val currentArticlePosition = savedInstanceState.getInt(ArticleFragment.ARG_POSITION)
                            startArticleActivity(currentArticlePosition, savedInstanceState)
                        }
                    }
                }
            }
        }
    }

    /**
     * 記事が選択された際に呼び出される。
     *
     * @param position 選択された記事の位置
     */
    override fun onArticleSelected(position: Int) {
        if (isTwoPanes) {
            updateArticleView(position)
        }
        else {
            startArticleActivity(position)
        }
    }

    /**
     * このアクティビティで呼び出したアクティビティからこのアクティビティに戻ってきた時に呼び出される。
     *
     * @param requestCode このアクティビティから呼び出したアクティビティに渡したリクエストコード。
     * @param resultCode 呼び出したアクティビティの終了時の状態。
     * @param data 呼び出したアクティビティ終了時に格納した情報。
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // ツーペインだった場合、記事表示アクティビティで表示していた情報を設定
        if (
            requestCode == REQUEST_ARTICLE_POSITION &&
            resultCode == RESULT_OK &&
            isTwoPanes &&
            data != null
        ) {
            val year = data.getIntExtra(DatePickerDialogFragment.YEAR, 0)
            val month = data.getIntExtra(DatePickerDialogFragment.MONTH, 0)
            val dayOfMonth = data.getIntExtra(DatePickerDialogFragment.DAY_OF_MONTH, 0)
            val hourOfDay = data.getIntExtra(TimePickerDialogFragment.HOUR_OF_DAY, 0)
            val minute = data.getIntExtra(TimePickerDialogFragment.MINUTE, 0)
            val savedDateTime = Calendar.getInstance()
            savedDateTime.set(Calendar.YEAR, year)
            savedDateTime.set(Calendar.MONTH, month)
            savedDateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            savedDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay)
            savedDateTime.set(Calendar.MINUTE, minute)
            val articleFragment = supportFragmentManager.findFragmentById(R.id.article_fragment) as ArticleFragment
            articleFragment.selectedDateTime = savedDateTime
            val currentArticlePosition = data.getIntExtra(ArticleFragment.ARG_POSITION, ArticleFragment.NOT_SELECTED_ARTICLE)
            updateArticleView(currentArticlePosition)
        }
    }

    /**
     * 現在表示中の記事を更新する。
     *
     * @param position 現在選択されている記事の位置。
     */
    private fun updateArticleView(position: Int) {
        val headlinesFragment = supportFragmentManager.findFragmentById(R.id.headlines_fragment) as HeadlinesFragment
        headlinesFragment.updateSelectedItemPosition(position)
        val articleFragment = supportFragmentManager.findFragmentById(R.id.article_fragment) as ArticleFragment
        articleFragment.updateArticleView(position)
    }

    /**
     * 記事表示アクティビティを開始する。
     *
     * @param position 現在選択されている記事の位置。
     * アクティビティ再起動時にはArticleFragment.NOT_SELECTED_ARTICLEを渡すこと。
     * 渡されたことにより、ArticleActivityで作成されるArticleFragmentは破棄される前に表示していた記事を表示する。
     * @param savedInstanceState 特定の状況下でアクティビティ廃棄直前に保存された情報。
     */
    private fun startArticleActivity(position: Int, savedInstanceState: Bundle? = null) {
        val intent = Intent(this, ArticleActivity::class.java)
        intent.putExtra(ArticleFragment.ARG_POSITION, position)
        if (
            savedInstanceState != null &&
            savedInstanceState.containsKey(DatePickerDialogFragment.YEAR) &&
            savedInstanceState.containsKey(DatePickerDialogFragment.MONTH) &&
            savedInstanceState.containsKey(DatePickerDialogFragment.DAY_OF_MONTH) &&
            savedInstanceState.containsKey(TimePickerDialogFragment.HOUR_OF_DAY) &&
            savedInstanceState.containsKey(TimePickerDialogFragment.MINUTE)
        ) {
            val year = savedInstanceState.getInt(DatePickerDialogFragment.YEAR)
            val month = savedInstanceState.getInt(DatePickerDialogFragment.MONTH)
            val dayOfMonth = savedInstanceState.getInt(DatePickerDialogFragment.DAY_OF_MONTH)
            val hourOfDay = savedInstanceState.getInt(TimePickerDialogFragment.HOUR_OF_DAY)
            val minute = savedInstanceState.getInt(TimePickerDialogFragment.MINUTE)
            intent.putExtra(DatePickerDialogFragment.YEAR, year)
            intent.putExtra(DatePickerDialogFragment.MONTH, month)
            intent.putExtra(DatePickerDialogFragment.DAY_OF_MONTH, dayOfMonth)
            intent.putExtra(TimePickerDialogFragment.HOUR_OF_DAY, hourOfDay)
            intent.putExtra(TimePickerDialogFragment.MINUTE, minute)
        }
        startActivityForResult(intent, REQUEST_ARTICLE_POSITION)
    }

    /**
     * ユーザーが日付を選択した時に呼び出される。
     *
     * @param year ユーザーが選択した年。
     * @param month ユーザーが選択した月。
     * @param dayOfMonth ユーザーが選択した日。
     */
    override fun onDateSet(year: Int, month: Int, dayOfMonth: Int) {
        if (isTwoPanes) {
            val articleFragment = supportFragmentManager.findFragmentById(R.id.article_fragment) as ArticleFragment
            articleFragment.updateDate(year, month, dayOfMonth)
        }
    }

    /**
     * ユーザーが時間を選択した時に呼び出される。
     *
     * @param hourOfDay ユーザーが選択した時間。
     * @param minute ユーザーが選択した分。
     */
    override fun onTimeSet(hourOfDay: Int, minute: Int) {
        if (isTwoPanes) {
            val articleFragment = supportFragmentManager.findFragmentById(R.id.article_fragment) as ArticleFragment
            articleFragment.updateTime(hourOfDay, minute)
        }
    }

    /**
     * アクティビティ廃棄時に呼ばれる。
     *
     * 廃棄直前に保持したい情報がある場合にoutStateに情報を登録する。
     * 次回アクティビティ作成時にここで登録した情報が渡される。
     *
     * @param outState 保持したい情報がある場合にこのインスタンスに情報を登録する。
     */
    override fun onSaveInstanceState(outState: Bundle?) {
        // ツーペインの場合、現在表示している記事の位置を保存し、最後にフォーカスが当たっていたフラグメントの情報を保存
        // ワンペインの場合、保持している情報をクリア
        // ツーペインで値が保持された状態でワンペインになり、その状態でまたワンペインになった場合、以前保持したツーペインの情報を使ってしまうことになるためクリアしている
        if (isTwoPanes) {
            val articleFragment = supportFragmentManager.findFragmentById(R.id.article_fragment) as ArticleFragment
            outState?.putInt(ArticleFragment.ARG_POSITION, articleFragment.currentPosition)
            val year = articleFragment.selectedDateTime.get(Calendar.YEAR)
            val month = articleFragment.selectedDateTime.get(Calendar.MONTH)
            val dayOfMonth = articleFragment.selectedDateTime.get(Calendar.DAY_OF_MONTH)
            val hourOfDay = articleFragment.selectedDateTime.get(Calendar.HOUR_OF_DAY)
            val minute = articleFragment.selectedDateTime.get(Calendar.MINUTE)
            outState?.putInt(DatePickerDialogFragment.YEAR, year)
            outState?.putInt(DatePickerDialogFragment.MONTH, month)
            outState?.putInt(DatePickerDialogFragment.DAY_OF_MONTH, dayOfMonth)
            outState?.putInt(TimePickerDialogFragment.HOUR_OF_DAY, hourOfDay)
            outState?.putInt(TimePickerDialogFragment.MINUTE, minute)
            val currentView = articleFragment.view
            if (
                currentView != null &&
                currentView.hasFocus()
                ) {
                outState?.putInt(ARG_LAST_FOCUS, Focus.ARTICLE.value)
            }
            else {
                outState?.putInt(ARG_LAST_FOCUS, Focus.HEADLINES.value)
            }
        }
        else {
            outState?.clear()
        }
        super.onSaveInstanceState(outState)
    }

    /**
     * ユーザーが画面をタッチした時に呼ばれる。
     *
     * 背景をタップでキーボードが閉じる処理を追加している。
     *
     * @param event モーションイベント。
     */
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        val articleFragment = supportFragmentManager.findFragmentById(R.id.article_fragment) as ArticleFragment
        inputMethodManager?.hideSoftInputFromWindow(articleFragment.view?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        articleFragment.view?.requestFocus()
        return false
    }
}

/**
 * どのフラグメントにフォーカスが当たっているかを判別するための列挙型。
 *
 * @property value 各フォーカスの整数値。
 */
enum class Focus(val value: Int) {
    /**
     * 記事のタイトルを表示するフラグメント。
     */
    HEADLINES(0),

    /**
     * 記事の内容を表示するフラグメント。
     */
    ARTICLE(1)
}
