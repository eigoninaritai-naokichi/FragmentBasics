package com.eigoninaritai.fragmentbasics

import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.text.format.DateFormat
import android.widget.TimePicker

/**
 * TimePickerをダイアログで表示するフラグメント。
 */
class TimePickerDialogFragment : DialogFragment(), TimePickerDialog.OnTimeSetListener {
    companion object {
        /**
         * ユーザーが選択した時間を取得、保持するためのBundleで使用する識別子。
         */
        val HOUR_OF_DAY = "com.eigoninaritai.fragmentbasics.DatePickerDialogFragment.HOUR_OF_DAY"

        /**
         * ユーザーが選択した分を取得、保持するためのBundleで使用する識別子。
         */
        val MINUTE = "com.eigoninaritai.fragmentbasics.DatePickerDialogFragment.MINUTE"

        /**
         * このフラグメントのインスタンスを初期化して返す。
         *
         * @param hourOfDay ユーザーが選択した時間。
         * @param minute ユーザーが選択した分。
         * @return 初期化されたこのフラグメント。
         */
        fun newInstance(hourOfDay: Int, minute: Int): TimePickerDialogFragment {
            val fragment = TimePickerDialogFragment()
            val args = Bundle()
            args.putInt(HOUR_OF_DAY, hourOfDay)
            args.putInt(MINUTE, minute)
            fragment.arguments = args
            return fragment
        }
    }

    /**
     * ユーザーが時間を選択した際のコールバックを含むインターフェース。
     */
    interface OnTimeSetListener {
        /**
         * ユーザーが時間を選択した時に呼び出される。
         *
         * @param hourOfDay ユーザーが選択した時間。
         * @param minute ユーザーが選択した分。
         */
        fun onTimeSet(hourOfDay: Int, minute: Int)
    }

    /**
     * ユーザーが時間を選択した際のコールバック。
     * 遅延して初期化されるため、onAttach以降に使用しなければならない。
     */
    lateinit private var callback: OnTimeSetListener

    /**
     * ダイアログを作成する。
     *
     * 保存された時間の情報がある場合、その値を使用してTimePickerを表示する。
     * ない場合はインスタンス作成時に渡された値を使用してTimePickerを表示する。
     *
     * @param savedInstanceState 特定の状況下で廃棄直前にフラグメントに保存された情報。
     */
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val hourOfDay: Int
        val minute: Int
        if (savedInstanceState != null) {
            hourOfDay = savedInstanceState.getInt(HOUR_OF_DAY)
            minute = savedInstanceState.getInt(MINUTE)
        }
        else {
            hourOfDay = arguments?.getInt(HOUR_OF_DAY) ?: throw RuntimeException("選択された時間の取得に失敗しました。")
            minute = arguments?.getInt(MINUTE) ?: throw RuntimeException("選択された時間の取得に失敗しました。")
        }
        return TimePickerDialog(activity, this, hourOfDay, minute, DateFormat.is24HourFormat(activity))
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
            callback = context as OnTimeSetListener
        }
        catch(classCastProblem: ClassCastException) {
            throw ClassCastException("${context.toString()}はOnTimeSetListenerを実装しなければなりません。")
        }
    }

    /**
     * ユーザーが時間を選択した時に呼び出される。
     *
     * @param view 現在表示中のTimePickerのインスタンス。
     * @param hourOfDay ユーザーが選択した時間。
     * @param minute ユーザーが選択した分。
     */
    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        callback.onTimeSet(hourOfDay, minute)
    }
}
