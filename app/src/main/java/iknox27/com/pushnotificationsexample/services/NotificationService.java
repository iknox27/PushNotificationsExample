package iknox27.com.pushnotificationsexample.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;

import iknox27.com.pushnotificationsexample.FriendsActivity;
import iknox27.com.pushnotificationsexample.MainActivity;
import iknox27.com.pushnotificationsexample.R;

public class NotificationService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
         remoteMessage.getMessageId();
        ArrayList<String> value = new ArrayList<>();
        for (Map.Entry<String, String> entry : remoteMessage.getData().entrySet()) {
            String key = entry.getKey();
            value.add(entry.getValue());
            Log.d("sdsdsd", "key, " + key + " value " + value);
        }

         if(remoteMessage.getNotification() != null){
             sendNotification(remoteMessage.getNotification().getBody(),remoteMessage.getNotification().getTitle(),value );
         }
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.d("NEW_TOKEN",s);
    }

    private void sendNotification(String body,String title,ArrayList<String> typeOfNotification) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        int notificationId = 1;
        String channelName = "Channel Name";
        int importance = NotificationManager.IMPORTANCE_HIGH;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    getString(R.string.default_notification_channel_id), channelName, importance);
            notificationManager.createNotificationChannel(mChannel);
        }


        NotificationCompat.Builder mBuilder;
        Bitmap bmn = getBitmapFromURL(typeOfNotification.get(1));
         mBuilder = new NotificationCompat.Builder(this, getString(R.string.default_notification_channel_id))
                    .setSmallIcon(R.drawable.ic_stat_name)
                    .setColorized(true)
                    .setColor(getResources().getColor(R.color.colorAccent))
                    .setContentTitle(title)
                    .setLargeIcon(bmn)
                    .addAction(makeAction(typeOfNotification.get(0)))
                    .setContentText(body);


        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntent(intent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
                0,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
        mBuilder.setContentIntent(resultPendingIntent);

        notificationManager.notify(notificationId, mBuilder.build());
    }


    // weare going to use values to change my buttons and different intents
    private NotificationCompat.Action makeAction(String value) {
        Intent intent = null;
        String title  = "Launch";
        switch (value){
            case "addFriend" :
                intent  = new Intent(this, FriendsActivity.class);
                title = "Agregar Amigo";
                break;

                default: intent  =  new Intent(this, MainActivity.class);
                    title = "Ver";

        }
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Action action = new NotificationCompat.Action.Builder(R.mipmap.ic_launcher, title, pendingIntent).build();
        return action;
    }

    public  Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            Bitmap icon = BitmapFactory.decodeResource(getResources(),
                    R.drawable.ic_launcher_background);
            return icon;
        }
    }
}
