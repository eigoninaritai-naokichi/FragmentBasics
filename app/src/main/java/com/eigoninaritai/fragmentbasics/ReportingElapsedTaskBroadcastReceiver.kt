package com.eigoninaritai.fragmentbasics

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.support.v4.app.NotificationCompat
import android.support.v4.app.TaskStackBuilder

/**
 * 期限を経過したタスクをユーザーに通知するブロードキャストレシーバー。
 *
 * タスク編集フラグメントで期限が設定された場合にこのブロードキャストが設定される。
 * タスクの期限が過ぎるとタスク、もしくは直近の子タスクの内容と期限を報告する。
 */
class ReportingElapsedTaskBroadcastReceiver : BroadcastReceiver() {
    companion object {
        /**
         * 期限を経過したタスクをユーザーに通知するブロードキャストレシーバーのチャンネルID。
         */
        val REPORT_ELAPSED_TASK_CHANNEL: String = "com.eigoninaritai.frowtask.ReportingElapsedTaskBroadcastReceiver"

        /**
         * タスクテーブルを引数として示す識別子。
         */
        val ARG_TASK: String = "com.eigoninaritai.frowtask.ARG_TASK"
    }

    /**
     * タスクの期限が経過した直後に呼び出される。
     *
     * この受け取り処理が呼び出されると経過したタスクの内容と期限をユーザーに通知する。
     *
     * @param context このブロードキャストレシーバーを呼び出したContextクラス。
     * @param intent このブロードキャストレシーバーに渡されたIntent。
     * タスク、もしくは子タスクテーブルが内包されている。
     */
    override fun onReceive(context: Context, intent: Intent) {
        // タスク編集アクティビティに渡す値を取得
        val task = intent.getStringExtra(ARG_TASK)
        val currentArticlePosition = intent.getIntExtra(ArticleFragment.ARG_POSITION, ArticleFragment.NOT_SELECTED_ARTICLE)

        // タスク編集アクティビティに渡すIntentを作成し、フロータスクアクティビティへのナビゲーションを設定
        val taskIntent = Intent(context, MainActivity::class.java)
        taskIntent.putExtra(ARG_TASK, task)
        taskIntent.putExtra(ArticleFragment.ARG_POSITION, currentArticlePosition)
        val taskStackBuilder = TaskStackBuilder.create(context)
        taskStackBuilder.addNextIntent(taskIntent)

        // 以前に同じタスクIDで通知が設定されていた場合、情報を上書きする
        val taskPendingIntent = taskStackBuilder.getPendingIntent(currentArticlePosition, PendingIntent.FLAG_UPDATE_CURRENT)
        val notification = NotificationCompat.Builder(context, REPORT_ELAPSED_TASK_CHANNEL)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setTicker(task)
            .setContentTitle(task)
            .setContentText("2017/12/09 20:00")
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setVibrate(longArrayOf(1000, 1000))
            .setContentIntent(taskPendingIntent)
            .build()

        // 通知を発行
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(currentArticlePosition, notification)
    }
}
