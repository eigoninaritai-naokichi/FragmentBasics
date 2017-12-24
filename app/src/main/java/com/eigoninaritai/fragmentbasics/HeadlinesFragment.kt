package com.eigoninaritai.fragmentbasics

import android.content.Context
import android.os.Bundle
import android.support.v4.app.ListFragment
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast

/**
 * 記事のタイトル表示をするフラグメント。
 */
class HeadlinesFragment : ListFragment() {
    companion object {
        /**
         * このフラグメントのインスタンスを初期化して返す。
         *
         * @return 初期化されたこのフラグメント。
         */
        fun newInstance(): ListFragment {
            return HeadlinesFragment()
        }
    }

    /**
     * 記事が選択された際のコールバックを含むインターフェース。
     * このフラグメントを使用するアクティビティは、このインターフェースを実装しなければならない。
     */
    interface OnHeadlineSelectedListener {
        /**
         * 記事が選択された際に呼び出される。
         *
         * @param position 選択された記事の位置
         */
        fun onArticleSelected(position: Int)
    }

    /**
     * 記事が選択された際のコールバック。
     * 遅延して初期化されるため、onAttach以降に使用しなければならない。
     */
    lateinit private var callback: OnHeadlineSelectedListener

    /**
     * フラグメントを作成する。
     *
     * @param savedInstanceState 特定の状況下でフラグメント廃棄直前に保存された情報。
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        // ListViewにレイアウトを設定
        listAdapter = ArrayAdapter(activity, android.R.layout.simple_list_item_activated_1, Ipsum.Headlines)
    }

    /**
     * フラグメント開始時に呼び出される。
     *
     * この時点でレイアウトの描画が終わっている。
     */
    override fun onStart() {
        super.onStart()

        // レイアウトがツーペインだった場合、ListViewの選択モードをシングルモードに設定
        if (resources.getBoolean(R.bool.is_two_panes)) {
            listView.choiceMode = ListView.CHOICE_MODE_SINGLE
            listView.clearChoices()
        }
    }

    /**
     * アクティビティに追加されると時に呼び出される。
     *
     * @param context このフラグメントを追加するアクティビティ。
     */
    override fun onAttach(context: Context?) {
        super.onAttach(context)

        // 記事が選択された際のコールバックの実装をアクティビティから取得
        // この実装をアクティビティが実装していない場合、エラー
        try {
            callback = context as OnHeadlineSelectedListener
        }
        catch(classCastProblem: ClassCastException) {
            throw ClassCastException("${context.toString()}はOnHeadlineSelectedListenerを実装しなければなりません。")
        }
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
        inflater.inflate(R.menu.head_line_menu, menu)
        val searchView = menu.findItem(R.id.action_search).actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String): Boolean { return true }

            /**
             * 検索文字が決定された時に呼び出される。
             *
             * @param query 決定された時の文字列。
             */
            override fun onQueryTextSubmit(query: String): Boolean {
                Toast.makeText(activity, query, Toast.LENGTH_SHORT).show()
                menu.findItem(R.id.action_search).collapseActionView()
                return true
            }
        })
        super.onCreateOptionsMenu(menu, inflater)
    }

    /**
     * メニューの項目が選択された時に呼ばれる。
     *
     * @param item 選択された項目。
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_sort -> {
                Toast.makeText(activity, "並び替えボタンクリック", Toast.LENGTH_SHORT).show()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    /**
     * ListViewのアイテムが選択された時に呼ばれる。
     *
     * @param l 選択されたアイテムを内包するListView。
     * @param v 選択したアイテム。
     * @param position 選択されたアイテムの位置。
     * @param id 選択されたアイテムの行ID。
     */
    override fun onListItemClick(l: ListView?, v: View?, position: Int, id: Long) {
        // 記事が選択された際のコールバックを呼び出し、選択したListViewのアイテムを選択状態にする(ツーペインの時のみ選択状態になる)
        callback.onArticleSelected(position)
        updateSelectedItemPosition(position)
    }

    /**
     * リストで選択されたアイテムの位置を更新する。
     *
     & @param position 選択されたアイテムの位置。
     */
    fun updateSelectedItemPosition(position: Int) {
        listView.setItemChecked(position, true)
    }
}
