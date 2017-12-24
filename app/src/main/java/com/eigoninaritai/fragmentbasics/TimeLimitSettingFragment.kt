package com.eigoninaritai.fragmentbasics

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.format.DateFormat
import android.view.View
import android.widget.Button
import java.text.SimpleDateFormat
import java.util.*

/**
 * タスクの期限を設定するアクティビティ。
 */
abstract class TimeLimitSettingFragment : Fragment() {
    companion object {
        /**
         * アラームの識別子を引数として示す識別子。。
         */
        val ARG_ALARM_ID = "com.eigoninaritai.frowtask.TimeLimitSettingFragment"
    }

    /**
     * DatePickerDialogFragmentを呼び出すボタンの識別子。
     * 継承したクラスのonCreateView時に初期化しなければならない。
     */
    protected var datePickerButtonId: Int = 0

    /**
     * TimePickerDialogFragmentを呼び出すボタンの識別子。
     * 継承したクラスのonCreateView時に初期化しなければならない。
     */
    protected var timePickerButtonId: Int = 0

    /**
     * 日付、時刻のボタンに設定されている値に基づくカレンダーインスタンス。
     *
     * 取得した場合、日付、時刻のボタンに設定された値でカレンダーインスタンスを作成し、返す。
     * 設定した場合、渡されたカレンダーインスタンスから日付、時刻のボタンに値を設定する。
     */
    var selectedDateTime: Calendar
        get() {
            val datePickerSpinner = activity?.findViewById<Button>(datePickerButtonId)
            val dateString = datePickerSpinner?.text
            val timePickerSpinner = activity?.findViewById<Button>(timePickerButtonId)
            val timeString = timePickerSpinner?.text
            if (
                !dateString.isNullOrEmpty() &&
                !timeString.isNullOrEmpty()
            ) {
                val selectedDateTimeString = "$dateString $timeString"
                val formatPattern = "yyyy/MM/dd HH:mm"
                val selectedDateTimeTemp = Calendar.getInstance()
                selectedDateTimeTemp.time = SimpleDateFormat(formatPattern, Locale.JAPAN).parse(selectedDateTimeString)
                return selectedDateTimeTemp
            }
            else {
                throw RuntimeException("選択された日時の取得に失敗しました。")
            }
        }
        set (value) {
            val year = value.get(Calendar.YEAR)
            val month = value.get(Calendar.MONTH)
            val dayOfMonth = value.get(Calendar.DAY_OF_MONTH)
            updateDate(year, month, dayOfMonth)
            val hourOfDay = value.get(Calendar.HOUR_OF_DAY)
            val minute = value.get(Calendar.MINUTE)
            updateTime(hourOfDay, minute)
        }

    /**
     * アクティビティが作成された時に呼ばれる。
     *
     * @param savedInstanceState 特定の状況下でアクティビティ廃棄直前に保存された情報。
     */
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // 現在日時を日付、時刻のボタンに設定
        val datePickerButton = activity?.findViewById<Button>(datePickerButtonId)
        datePickerButton?.setOnClickListener { showDatePickerDialogFragment() }
        val timePickerButton = activity?.findViewById<Button>(timePickerButtonId)
        timePickerButton?.setOnClickListener { showTimePickerDialogFragment() }

        // 保存された情報がある場合、保存された情報を日付、時刻のボタンに設定
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
            val savedDateTime = Calendar.getInstance()
            savedDateTime.set(Calendar.YEAR, year)
            savedDateTime.set(Calendar.MONTH, month)
            savedDateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            savedDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay)
            savedDateTime.set(Calendar.MINUTE, minute)
            selectedDateTime = savedDateTime
        }
        else {
            selectedDateTime = Calendar.getInstance()
        }
    }

    /**
     * DatePickerをダイアログで表示するフラグメントを表示する。
     */
    private fun showDatePickerDialogFragment() {
        val year = selectedDateTime.get(Calendar.YEAR)
        val month = selectedDateTime.get(Calendar.MONTH)
        val dayOfMonth = selectedDateTime.get(Calendar.DAY_OF_MONTH)
        val datePickerFragment = DatePickerDialogFragment.newInstance(year, month, dayOfMonth)
        datePickerFragment.show(activity?.supportFragmentManager, DatePickerDialogFragment::class.simpleName)
    }

    /**
     * 日付ボタンを更新する。
     *
     * 渡された値で日付をボタンに表示する。
     *
     * @param year 指定された年。
     * @param month 指定された月。
     * @param dayOfMonth 指定された日。
     */
    fun updateDate(year: Int, month: Int, dayOfMonth: Int) {
        val pickedDate = Calendar.getInstance()
        pickedDate.set(Calendar.YEAR, year)
        pickedDate.set(Calendar.MONTH, month)
        pickedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        val datePickerButton = activity?.findViewById<Button>(datePickerButtonId)
        datePickerButton?.text = DateFormat.format("yyyy/MM/dd", pickedDate)
    }

    /**
     * TimePickerをダイアログで表示するフラグメントを表示する。
     */
    private fun showTimePickerDialogFragment() {
        val hourOfDay = selectedDateTime.get(Calendar.HOUR_OF_DAY)
        val minute = selectedDateTime.get(Calendar.MINUTE)
        val timePickerFragment = TimePickerDialogFragment.newInstance(hourOfDay, minute)
        timePickerFragment.show(activity?.supportFragmentManager, TimePickerDialogFragment::class.simpleName)
    }

    /**
     * 時間ボタンを更新する。
     *
     * 渡された値で時間をボタンに表示する。
     *
     * @param hourOfDay 指定された時間。
     * @param minute 指定された分。
     */
    fun updateTime(hourOfDay: Int, minute: Int) {
        val pickedTime = Calendar.getInstance()
        pickedTime.set(Calendar.HOUR_OF_DAY, hourOfDay)
        pickedTime.set(Calendar.MINUTE, minute)
        val timePickerButton = activity?.findViewById<Button>(timePickerButtonId)
        timePickerButton?.text = DateFormat.format("kk:mm", pickedTime)
    }

    /**
     * アラームを設定するクリックイベントを取得する。
     *
     * @return アラームを設定するクリックイベント。
     */
    protected fun getSetTimeLimitClickListener() = View.OnClickListener { setTimeLimit(getAlarmId()) }

    /**
     * アラームを設定する。
     *
     * 設定された時刻に基づいてアラームを設定する。
     *
     * @param alarmId アラームに使用する識別子。
     * 以前に同じ識別子でアラームが設定されていた場合、以前に設定されていたアラームは取り消される。
     */
    protected fun setTimeLimit(alarmId: Int) {
        val alarmPendingIntent = getAlarmPendingIntent(alarmId)
        val alarmManager = activity?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, selectedDateTime.timeInMillis, alarmPendingIntent)
        }
        else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, selectedDateTime.timeInMillis, alarmPendingIntent)
        }
    }

    /**
     * アラームを取り消すクリックイベントを取得する。
     *
     * @return アラームを取り消すクリックイベント。
     */
    protected fun getCancelTimeLimitClickListener() = View.OnClickListener { cancelTimeLimit(getAlarmId()) }

    /**
     * アラームを取り消す。
     *
     * @param alarmId アラームに使用する識別子。
     */
    protected fun cancelTimeLimit(alarmId: Int) {
        val alarmPendingIntent = getAlarmPendingIntent(alarmId)
        val alarmManager = activity?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(alarmPendingIntent)
    }

    /**
     * アラームを設定するためのPendingIntentを取得する。
     *
     * @param requestCode PendingIntentに渡すリクエストコード。
     * @return アラームを設定するためのPendingIntent。
     */
    private fun getAlarmPendingIntent(requestCode: Int): PendingIntent {
        val intent = getAlarmIntent(requestCode)
        return PendingIntent.getBroadcast(activity, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    /**
     * アラームに使用する識別子を取得する。
     *
     * 継承したクラスでアラームに使用する識別子として設定したい値を返す実装をする。
     *
     * @return アラームに使用する識別子。
     */
    abstract fun getAlarmId(): Int

    /**
     * アラームに使用するIntentを取得する。
     *
     * 継承したクラスでアラームに使用するIntentを返す実装をする。
     *
     * @param alarmId アラームに使用する識別子。
     */
    abstract fun getAlarmIntent(alarmId: Int): Intent

    /**
     * アクティビティ廃棄時に呼ばれる。
     *
     * 廃棄直前に保持したい情報がある場合にoutStateに情報を登録する。
     * 次回アクティビティ作成時にここで登録した情報が渡される。
     *
     * @param outState 保持したい情報がある場合にこのインスタンスに情報を登録する。
     */
    override fun onSaveInstanceState(outState: Bundle) {
        // 日付、時刻のボタンに設定されている値を保存
        val year = selectedDateTime.get(Calendar.YEAR)
        val month = selectedDateTime.get(Calendar.MONTH)
        val dayOfMonth = selectedDateTime.get(Calendar.DAY_OF_MONTH)
        val hourOfDay = selectedDateTime.get(Calendar.HOUR_OF_DAY)
        val minute = selectedDateTime.get(Calendar.MINUTE)
        outState.putInt(DatePickerDialogFragment.YEAR, year)
        outState.putInt(DatePickerDialogFragment.MONTH, month)
        outState.putInt(DatePickerDialogFragment.DAY_OF_MONTH, dayOfMonth)
        outState.putInt(TimePickerDialogFragment.HOUR_OF_DAY, hourOfDay)
        outState.putInt(TimePickerDialogFragment.MINUTE, minute)
        super.onSaveInstanceState(outState)
    }
}
