package com.eigoninaritai.fragmentbasics

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.TextView

/**
 * 記事の内容を表示するフラグメント。
 */
class ArticleFragment : TimeLimitSettingFragment() {
    companion object {
        /**
         * Bundleで使用する記事の位置の識別子。
         */
        val ARG_POSITION: String = "com.eigoninaritai.fragmentbasics.POSITION"

        /**
         * 記事が未選択の時の値。
         */
        val NOT_SELECTED_ARTICLE: Int = -1

        /**
         * このフラグメントのインスタンスを初期化して返す。
         *
         * @param position 表示する記事の位置。
         * @return 初期化されたこのフラグメント。
         */
        fun newInstance(position: Int): ArticleFragment {
            val fragment = ArticleFragment()
            val args = Bundle()
            args.putInt(ARG_POSITION, position)
            fragment.arguments = args
            return fragment
        }
    }

    /**
     * 現在表示中の記事の位置。
     */
    var currentPosition: Int = ArticleFragment.NOT_SELECTED_ARTICLE

    /**
     * フラグメントを作成する。
     *
     * @param savedInstanceState 特定の状況下でフラグメント廃棄直前に保存された情報。
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    /**
     * フラグメントのレイアウトを作成する。
     *
     * @param inflater フラグメントを作成するためのインフレーター。
     * @param container フラグメントのレイアウトが設定される親View。
     * @param savedInstanceState 特定の状況下でフラグメント廃棄直前に保存された情報。
     * @return 作成したフラグメントのレイアウト。
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // 日付、時刻のボタンの識別子を設定
        datePickerButtonId = R.id.date_picker
        timePickerButtonId = R.id.time_picker

        val argPosition = arguments?.getInt(ARG_POSITION)
        if (savedInstanceState != null) currentPosition = savedInstanceState.getInt(ARG_POSITION)
        else if (argPosition != null) currentPosition = argPosition

        // フラグメントのレイアウトリソースを親Viewに設定
        return inflater.inflate(R.layout.fragment_article, container, false)
    }

    /**
     * フラグメント開始時に呼び出される。
     *
     * この時点でレイアウトの描画が終わっている。
     */
    override fun onStart() {
        super.onStart()

        // 期限を設定するボタンにイベントを設定
        val timeLimitSettingButton = activity?.findViewById<Button>(R.id.time_limit_setting_button)
        timeLimitSettingButton?.setOnClickListener(getSetTimeLimitClickListener())
        val timeLimitCancelButton = activity?.findViewById<Button>(R.id.time_limit_cancel_button)
        timeLimitCancelButton?.setOnClickListener(getCancelTimeLimitClickListener())

        // 記事の位置が保持されている場合、記事を表示
        if (currentPosition != NOT_SELECTED_ARTICLE) updateArticleView(currentPosition)
    }

    /**
     * メニューの設定をする。
     *
     * この処理はアクティビティから呼ばれる。
     * アクティビティでメニューが設定されていた場合、アクティビティのメニューの後にこの処理で設定したメニューが表示される。
     *
     * @param menu 配置したいメニューをこのインスタンスに設定する。
     * @param inflater メニューを作成するためのインフレーター。
     */
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.article_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    /**
     * メニューの項目が選択された時に呼ばれる。
     *
     * @param item 選択された項目。
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_alarm -> {
                setTimeLimit(getAlarmId())
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    /**
     * 表示する記事を更新する。
     *
     * @param position 表示する記事の位置。
     */
    fun updateArticleView(position: Int) {
        val article = activity?.findViewById<TextView>(R.id.article)
        article?.text = Ipsum.Articles[position]
        currentPosition = position
        view?.requestFocus()
    }

    /**
     * アラームに使用する識別子を取得する。
     *
     * 継承したクラスでアラームに使用する識別子として設定したい値を返す実装をする。
     *
     * @return アラームに使用する識別子。
     */
    override fun getAlarmId(): Int {
        return currentPosition
    }

    /**
     * アラームに使用するIntentを取得する。
     *
     * 継承したクラスでアラームに使用するIntentを返す実装をする。
     *
     * @param alarmId アラームに使用する識別子。
     */
    override fun getAlarmIntent(alarmId: Int): Intent {
        val intent = Intent(activity, ReportingElapsedTaskBroadcastReceiver::class.java)
        intent.putExtra(TimeLimitSettingFragment.ARG_ALARM_ID, alarmId)
        intent.putExtra(ARG_POSITION, currentPosition)
        intent.putExtra(ReportingElapsedTaskBroadcastReceiver.ARG_TASK, "テスト通知$alarmId")
        return intent
    }

    /**
     * フラグメント廃棄時に呼ばれる。
     *
     * 廃棄直前に保持したい情報がある場合にoutStateに情報を登録する。
     * 次回フラグメント作成時にここで登録した情報が渡される。
     *
     * @param outState 保持したい情報がある場合にこのインスタンスに情報を登録する。
     */
    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(ArticleFragment.ARG_POSITION, currentPosition)
        super.onSaveInstanceState(outState)
    }
}
