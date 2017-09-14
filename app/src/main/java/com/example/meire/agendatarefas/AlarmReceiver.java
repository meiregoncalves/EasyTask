package com.example.meire.agendatarefas;

import android.app.Application;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.example.meire.agendatarefas.dao.tasksDAO;

public class AlarmReceiver extends BroadcastReceiver {

    private NotificationManager mNotificationManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        tasksDAO tasksDAO = new tasksDAO(context);
        mNotificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        int idTask = intent.getIntExtra("IDTASK",0);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
        mBuilder.setContentTitle(context.getString(R.string.dont_forget));
        mBuilder.setContentText(tasksDAO.getBy(idTask).getTitle());
        mBuilder.setTicker(context.getString(R.string.msg_you_have_task));
        mBuilder.setSmallIcon(R.mipmap.ic_launcher);
        mBuilder.setAutoCancel(true);
        mBuilder.setSound(defaultSoundUri);
        mBuilder.setVibrate(new long[] { 1000, 1000});

        Intent newintent = new Intent(context, SplashScreenActivity.class);
        newintent.putExtra("IDTASK", idTask);

        try {
            mBuilder.setContentIntent(PendingIntent.getActivity(context, 0, newintent, PendingIntent.FLAG_UPDATE_CURRENT));
            mNotificationManager.notify(100, mBuilder.build());
        }
        catch (Exception ex) {

        }
    }
}
