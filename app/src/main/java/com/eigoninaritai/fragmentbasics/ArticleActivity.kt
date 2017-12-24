package com.eigoninaritai.fragmentbasics

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import java.util.*

class ArticleActivity : AppCompatActivity(), DatePickerDialogFragment.OnDateSetListener, TimePickerDialogFragment.OnTimeSetListener {
    /**
     * アクティビティを作成する。
     *
     * 作成時、メインアクティビティから送られてきた記事の位置を取得し、表示する記事を設定する。
     * ワンペインからツーペインに切り替わった状態でこのアクティビティが作成された場合、このアクティビティを終了する
     *
     * @param savedInstanceState 特定の状況下でアクティビティ廃棄直後に保存された情報。
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_article)
        setSupportActionBar(findViewById<Toolbar?>(R.id.action_toolbar))

        // メインアクティビティから送られてきた記事の位置を取得し、表示する記事を設定
        if (savedInstanceState == null) {
            val currentPosition = intent.getIntExtra(ArticleFragment.ARG_POSITION, ArticleFragment.NOT_SELECTED_ARTICLE)
            val articleFragment = supportFragmentManager.findFragmentById(R.id.article_fragment) as ArticleFragment
            articleFragment.updateArticleView(currentPosition)
        }
    }

    /**
     * アクティビティ開始時に呼び出される。
     *
     * この時点でレイアウトの描画が終わっている。
     */
    override fun onStart() {
        super.onStart()

        // ツーペインの場合、このアクティビティを終了し、現在表示中の記事の位置と日時をメインアクティビティに返す
        // ワンペインの場合、日時の情報がある場合、日時を設定
        if (resources.getBoolean(R.bool.is_two_panes)) {
            val articleFragment = supportFragmentManager.findFragmentById(R.id.article_fragment) as ArticleFragment
            val year = articleFragment.selectedDateTime.get(Calendar.YEAR)
            val month = articleFragment.selectedDateTime.get(Calendar.MONTH)
            val dayOfMonth = articleFragment.selectedDateTime.get(Calendar.DAY_OF_MONTH)
            val hourOfDay = articleFragment.selectedDateTime.get(Calendar.HOUR_OF_DAY)
            val minute = articleFragment.selectedDateTime.get(Calendar.MINUTE)
            intent.putExtra(DatePickerDialogFragment.YEAR, year)
            intent.putExtra(DatePickerDialogFragment.MONTH, month)
            intent.putExtra(DatePickerDialogFragment.DAY_OF_MONTH, dayOfMonth)
            intent.putExtra(TimePickerDialogFragment.HOUR_OF_DAY, hourOfDay)
            intent.putExtra(TimePickerDialogFragment.MINUTE, minute)
            intent.putExtra(ArticleFragment.ARG_POSITION, articleFragment.currentPosition)
            setResult(RESULT_OK, intent)
            finish()
            return
        }
        else {
            if (
                intent.hasExtra(DatePickerDialogFragment.YEAR) &&
                intent.hasExtra(DatePickerDialogFragment.MONTH) &&
                intent.hasExtra(DatePickerDialogFragment.DAY_OF_MONTH) &&
                intent.hasExtra(TimePickerDialogFragment.HOUR_OF_DAY) &&
                intent.hasExtra(TimePickerDialogFragment.MINUTE)
            ) {
                val year = intent.getIntExtra(DatePickerDialogFragment.YEAR, 0)
                val month = intent.getIntExtra(DatePickerDialogFragment.MONTH, 0)
                val dayOfMonth = intent.getIntExtra(DatePickerDialogFragment.DAY_OF_MONTH, 0)
                val hourOfDay = intent.getIntExtra(TimePickerDialogFragment.HOUR_OF_DAY, 0)
                val minute = intent.getIntExtra(TimePickerDialogFragment.MINUTE, 0)
                val savedDateTime = Calendar.getInstance()
                savedDateTime.set(Calendar.YEAR, year)
                savedDateTime.set(Calendar.MONTH, month)
                savedDateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                savedDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay)
                savedDateTime.set(Calendar.MINUTE, minute)
                val articleFragment = supportFragmentManager.findFragmentById(R.id.article_fragment) as ArticleFragment
                articleFragment.selectedDateTime = savedDateTime
            }
        }
    }

    /**
     * ユーザーが日付を選択した時に呼び出される。
     *
     * @param year ユーザーが選択した年。
     * @param month ユーザーが選択した月。
     * @param dayOfMonth ユーザーが選択した日。
     */
    override fun onDateSet(year: Int, month: Int, dayOfMonth: Int) {
        val articleFragment = supportFragmentManager.findFragmentById(R.id.article_fragment) as ArticleFragment
        articleFragment.updateDate(year, month, dayOfMonth)
    }

    /**
     * ユーザーが時間を選択した時に呼び出される。
     *
     * @param hourOfDay ユーザーが選択した時間。
     * @param minute ユーザーが選択した分。
     */
    override fun onTimeSet(hourOfDay: Int, minute: Int) {
        val articleFragment = supportFragmentManager.findFragmentById(R.id.article_fragment) as ArticleFragment
        articleFragment.updateTime(hourOfDay, minute)
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
        val mainLayout = findViewById<View>(android.R.id.content)
        inputMethodManager?.hideSoftInputFromWindow(mainLayout.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        mainLayout.requestFocus()
        return false
    }
}
