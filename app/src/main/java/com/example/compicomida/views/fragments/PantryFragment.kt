package com.example.compicomida.views.fragments

import android.Manifest
import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.SearchView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.compicomida.CompiComidaApp
import com.example.compicomida.Notifications
import com.example.compicomida.R
import com.example.compicomida.model.localDb.entities.PantryItem
import com.example.compicomida.viewmodels.pantry.PantryViewModel
import com.example.compicomida.viewmodels.pantry.factory.PantryViewModelFactory
import com.example.compicomida.views.activities.pantry.AddPantryItemActivity
import com.example.compicomida.views.activities.pantry.EditPantryItemActivity
import com.example.compicomida.views.adapters.PantryAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.time.Duration
import java.time.LocalDateTime
import java.util.Calendar

/**
 * PantryFragment:
 * - Shows a list with all the products from your pantry.
 */
class PantryFragment : Fragment(), SearchView.OnQueryTextListener {

    // View Model
    private lateinit var pantryModel: PantryViewModel

    // Recycler Lists
    private lateinit var recyclerPantry: RecyclerView

    // Adapter Lists
    private lateinit var pantryAdapter: PantryAdapter

    // Activity launcher
    private lateinit var activityLauncher: ActivityResultLauncher<Intent>

    //Launcher para solicitar permiso de notificaciones
    private lateinit var requestNotificationLauncher: ActivityResultLauncher<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_pantry, container, false)

        // Creating the Recycler Views
        recyclerPantry = root.findViewById(R.id.recyclerPantry)
        recyclerPantry.layoutManager = GridLayoutManager(root.context, 2)

        // Initialise the view model
        pantryModel = ViewModelProvider(
            requireActivity(),
            PantryViewModelFactory(CompiComidaApp.appModule.pantryRepo)
        )[PantryViewModel::class.java]


        activityLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) pantryModel.refreshPantryList()
            }

        initialisePantryRecyclerView()

        //API 33+ conceder permisos explícitamente
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            requestNotificationLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { allowed ->
                if (!allowed)
                    Toast.makeText(root.context,getString(R.string.notifications_alert_denied), Toast.LENGTH_LONG).show()
                else
                    Toast.makeText(root.context,getString(R.string.notifications_alert_allowed), Toast.LENGTH_LONG).show()
            }
            if (!hasPermissionPostApi33(root.context))
                requestNotificationsPermission(root.context)
        }

        checkExpirationDates(root.context)

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initFabNewPantryItem(view)
        initSearchView(view)
    }

    // Initialise the SearchView
    private fun initSearchView(view: View) {
        val pantrySearchView = view.findViewById<SearchView>(R.id.pantrySearchView)
        pantrySearchView.setOnQueryTextListener(this)
    }

    // Initialise the Fab Click Listener
    private fun initFabNewPantryItem(view: View) {
        val fabNewItem = view.findViewById<FloatingActionButton>(R.id.fabNewItemInPantry)
        fabNewItem.setOnClickListener {
            activityLauncher.launch(
                Intent(
                    context,
                    AddPantryItemActivity::class.java
                )
            )
        }
    }

    // Initialise the recycler view
    private fun initialisePantryRecyclerView() {
        pantryAdapter = PantryAdapter(listOf()) { pantryItemId ->
            pantryItemId?.let {

                activityLauncher.launch(
                    Intent(
                        context,
                        EditPantryItemActivity::class.java
                    ).apply {
                        putExtra("pantryId", it)
                    }
                )
            }
        }
        recyclerPantry.adapter = pantryAdapter

        // Observe pantry data
        pantryModel.pantryList.observe(viewLifecycleOwner) { pantryList ->
            pantryAdapter.updateList(pantryList)
        }

        pantryModel.refreshPantryList()
    }

    // ------- Functions that handle the search of pantry items

    override fun onQueryTextSubmit(query: String?): Boolean {

        query?.let {
            pantryModel.refreshPantryList(it)
            return true
        }

        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {

        newText?.let {
            pantryModel.refreshPantryList(it)
            return true
        }

        return false
    }

    private fun hasPermissionPostApi33(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun requestNotificationsPermission(context: Context) {
        if (!hasPermissionPostApi33(context))
        {
            //Solicitamos el permiso de notificaciones.
            requestNotificationLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    private fun checkExpirationDates(context: Context) {
        pantryModel.pantryList.observe(viewLifecycleOwner) { pantryList ->
            pantryList.forEach { pantryItem ->
                if(pantryItem.expirationDate.isAfter(LocalDateTime.now())){
                    notificateNotExpirateProducts(context,pantryItem)
                }else{
                    notificateExpirateProducts(context)
                }
            }
        }
    }

    private fun notificateExpirateProducts(context: Context){
        val intent = Intent(context, Notifications::class.java)

        intent.putExtra("expirated", true)

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            Notifications.PROGRAMADA_CADUCADO_NOTIFICATION_ID,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val  hourToNotificate = LocalDateTime.now().withHour(12).withMinute(0).withSecond(0).plusDays(1)

        val timeToNotificate = Duration.between(LocalDateTime.now(),hourToNotificate,).toMillis()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && alarmManager.canScheduleExactAlarms() || Build.VERSION.SDK_INT < Build.VERSION_CODES.S)
        {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, Calendar.getInstance().timeInMillis + timeToNotificate,pendingIntent)
        }
        else // 31 y sin permiso. Solicitamos
            startActivity(Intent(ACTION_REQUEST_SCHEDULE_EXACT_ALARM))
    }

    private fun notificateNotExpirateProducts(context: Context, pantryItem: PantryItem) {
        val intent = Intent(context, Notifications::class.java)

        intent.putExtra("expirated", false)

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            Notifications.PROGRAMADA_NO_CADUCADO_NOTIFICATION_ID,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val timeToNotificate = getTimeToNotificate(pantryItem)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && alarmManager.canScheduleExactAlarms() || Build.VERSION.SDK_INT < Build.VERSION_CODES.S)
        {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, Calendar.getInstance().timeInMillis + timeToNotificate,pendingIntent)
        }
        else // 31 y sin permiso. Solicitamos
            startActivity(Intent(ACTION_REQUEST_SCHEDULE_EXACT_ALARM))
    }

    private fun getTimeToNotificate(pantryItem: PantryItem): Long{

        var hourToNotificate = LocalDateTime.of(
            pantryItem.expirationDate.year,
            pantryItem.expirationDate.month,
            pantryItem.expirationDate.dayOfMonth,
            12,
            0
        ).minusDays(2)

        var timeToNotificate = Duration.between(LocalDateTime.now(),hourToNotificate,).toMillis()

        if(timeToNotificate <= 0){
            val now = LocalDateTime.now()
            if(now.hour < 12){
                hourToNotificate = now.withHour(12).withMinute(0).withSecond(0)
            }else{
                hourToNotificate = now.withHour(12).withMinute(0).withSecond(0).plusDays(1)
            }

            timeToNotificate = Duration.between(LocalDateTime.now(),hourToNotificate,).toMillis()
        }

        return timeToNotificate
    }

}