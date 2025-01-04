package com.example.compicomida

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.compicomida.views.fragments.PantryFragment

class Notifications: BroadcastReceiver() {

    companion object {
        const val PROGRAMADA_NO_CADUCADO_NOTIFICATION_ID = 2
        const val PROGRAMADA_CADUCADO_NOTIFICATION_ID = 3
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("PR11", "Entro en el onReceive de ProgramadaNotification")

        if(intent!!.getBooleanExtra("expirated",false) ){
            createNotificacionForProductsExpired(context!!)
        }else{
            createNotificacionForProductsNotExpired(context!!)
        }
    }

    private fun createNotificacionForProductsExpired(context: Context) {
        val intent = Intent(context, PantryFragment::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val notificacion = NotificationCompat.Builder(context,CompiComidaApp.CANAL_SIMPLE)
            .setSmallIcon(R.drawable.baseline_food_bank_24)
            .setContentTitle(context.getString(R.string.product_notificacion_title))
            .setContentText(context.getString(R.string.product_notificacion_body_expirated))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(PROGRAMADA_CADUCADO_NOTIFICATION_ID,notificacion)
    }

    private fun createNotificacionForProductsNotExpired(context: Context) {
        val intent = Intent(context, PantryFragment::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val notificacion = NotificationCompat.Builder(context,CompiComidaApp.CANAL_SIMPLE)
            .setSmallIcon(R.drawable.baseline_food_bank_24)
            .setContentTitle(context.getString(R.string.product_notificacion_title))
            .setContentText(context.getString(R.string.product_notificacion_body_not_expirated))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(PROGRAMADA_NO_CADUCADO_NOTIFICATION_ID,notificacion)
    }
}