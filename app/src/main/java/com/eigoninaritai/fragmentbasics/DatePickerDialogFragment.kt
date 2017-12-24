package com.eigoninaritai.fragmentbasics

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.widget.DatePicker

/**
 * DatePickerをダイアログで表示するフラグメント。
 */
class DatePickerDialogFragment : DialogFragment(), DatePickerDialog.OnDateSetListener {
    companion object {
        /**
         * ユーザーが選択した年を取得、保持するためのBundleで使用する識別子。
         */
        val YEAR = "com.eigoninaritai.fragmentbasics.DatePickerDialogFragment.YEAR"

        /**
         * ユーザーが選択した月を取得、保持するためのBundleで使用する識別子。
         */
        val MONTH = "com.eigoninaritai.fragmentbasics.DatePickerDialogFragment.MONTH"

        /**
         * ユーザーが選択した日を取得、保持するためのBundleで使用する識別子。
         */
        val DAY_OF_MONTH = "com.eigoninaritai.fragmentbasics.DatePickerDialogFragment.DAY_OF_MONTH"

        /**
         * このフラグメントのインスタンスを初期化して返す。
         *
         * @param year ユーザーが選択した年。
         * @param month ユーザーが選択した月。
         * @param dayOfMonth ユーザーが選択した日。
         * @return 初期化されたこのフラグメント。
         */
        fun newInstance(year: Int, month: Int, dayOfMonth: Int): DatePickerDialogFragment {
            val fragment = DatePickerDialogFragment()
            val args = Bundle()
            args.putInt(YEAR, year)
            args.putInt(MONTH, month)
            args.putInt(DAY_OF_MONTH, dayOfMonth)
            fragment.arguments = args
            return fragment
        }
    }

    /**
     * ユーザーが日付を選択した際のコールバックを含むインターフェース。
     */
    interface OnDateSetListener {
        /**
         * ユーザーが日付を選択した時に呼び出される。
         *
         * @param year ユーザーが選択した年。
         * @param month ユーザーが選択した月。
         * @param dayOfMonth ユーザーが選択した日。
         */
        fun onDateSet(year: Int, month: Int, dayOfMonth: Int)
    }

    /**
     * ユーザーが日付を選択した際のコールバック。
     * 遅延して初期化されるため、onAttach以降に使用しなければならない。
     */
    lateinit private var callback: OnDateSetListener

    /**
     * ダイアログを作成する。
     *
     * 保存された日付の情報がある場合、その値を使用してDatePickerを表示する。
     * ない場合はインスタンス作成時に渡された値を使用してDatePickerを表示する。
     *
     * @param savedInstanceState 特定の状況下で廃棄直前にフラグメントに保存された情報。
     */
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val year: Int
        val month: Int
        val dayOfMonth: Int
        if (savedInstanceState != null) {
            year = savedInstanceState.getInt(YEAR)
            month = savedInstanceState.getInt(MONTH)
            dayOfMonth = savedInstanceState.getInt(DAY_OF_MONTH)
        }
        else {
            year = arguments?.getInt(YEAR) ?: throw RuntimeException("選択された日付の取得に失敗しました。")
            month = arguments?.getInt(MONTH) ?: throw RuntimeException("選択された日付の取得に失敗しました。")
            dayOfMonth = arguments?.getInt(DAY_OF_MONTH)  ?:throw RuntimeException("選択された日付の取得に失敗しました。")
        }
        return DatePickerDialog(activity, this, year, month, dayOfMonth)
    }

    /**
     * アクティビティに追加されると時に呼び出される。
     *
     * @param context このフラグメントを追加するアクティビティ。
     */
    override fun onAttach(context: Context?) {
        super.onAttach(context)

        // ユーザーが日付を選択した際のコールバックの実装をアクティビティから取得
        // この実装をアクティビティが実装していない場合、エラー
        try {
            callback = context as OnDateSetListener
        }
        catch(classCastProblem: ClassCastException) {
            throw ClassCastException("${context.toString()}はOnDateSetListenerを実装しなければなりません。")
        }
    }

    /**
     * ユーザーが日付を選択した時に呼び出される。
     *
     * @param view 現在表示中のDatePickerのインスタンス。
     * @param year ユーザーが選択した年。
     * @param month ユーザーが選択した月。
     * @param dayOfMonth ユーザーが選択した日。
     */
    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        callback.onDateSet(year, month, dayOfMonth)
    }

    /**
     * ダイアログ廃棄時に呼ばれる。
     *
     * 廃棄直前に保持したい情報がある場合にoutStateに情報を登録する。
     * 次回ダイアログ作成時にここで登録した情報が渡される。
     *
     * @param outState 保持したい情報がある場合にこのインスタンスに情報を登録する。
     */
    override fun onSaveInstanceState(outState: Bundle) {
        val datePickerDialog: DatePickerDialog = dialog as DatePickerDialog
        val datePicker = datePickerDialog.datePicker
        outState.putInt(YEAR, datePicker.year)
        outState.putInt(MONTH, datePicker.month)
        outState.putInt(DAY_OF_MONTH, datePicker.dayOfMonth)
        super.onSaveInstanceState(outState)
    }
}
