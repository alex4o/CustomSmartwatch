package tech.bonin.alex4o.customsmartwatch

import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.service.notification.StatusBarNotification
import android.support.design.widget.Snackbar
import android.support.v4.widget.TextViewCompat
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewStub
import android.widget.*

import kotlinx.android.synthetic.main.activity_connected.*
import kotlinx.android.synthetic.main.content_connected.*
import kotlinx.coroutines.experimental.channels.Channel
import kotlinx.coroutines.experimental.channels.consumeEach
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.withContext
import org.jetbrains.anko.*
import tech.bonin.alex4o.customsmartwatch.MapsNavigation.MapsNotificationData
import android.R.attr.src
import android.text.method.ScrollingMovementMethod
import com.fasterxml.jackson.databind.ObjectMapper
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.msgpack.core.MessagePack
import org.msgpack.jackson.dataformat.MessagePackFactory
import java.nio.ByteBuffer
import java.nio.ByteOrder


class ConnectedActivity : AppCompatActivity() {
    var indecies = Notifications()
    val mapper = ObjectMapper(MessagePackFactory())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_connected)
        setSupportActionBar(toolbar)

        indecies = Notifications()

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }

                //var list = ArrayList<StatusBarNotification>()
        //this.notifications.adapter = NotificationAdapter(ctx, list)

        launch {
            while(Connected.socket?.isConnected!!){
                var str = StringBuilder()
                do{
                    val byte = Connected.istream?.read()!!
                    str.append(byte.toChar())
                }while(Connected.istream!!.available() > 0)



                runOnUiThread {
                    received.text = received.text.toString() + str.toString()
                    scroll.fullScroll(View.FOCUS_DOWN)
                }
            }

            runOnUiThread {
                finish()
            }
        }

//        launch {
//            while(Connected.socket?.isConnected!!){
//                var bytes: ArrayList<Byte> = ArrayList()
//                while(!Data.toSend.isEmpty){
//                }
//
//                runOnUiThread {
//                    toast("Some bytes send!")
//                }
//            }
//
//        }


        launch {
            Data.notifications.consumeEach { sbn ->
                println(sbn)
                val context = ctx.createPackageContext(sbn.getPackageName(), Context.CONTEXT_IGNORE_SECURITY)

                runOnUiThread {
                    //list.add(sbn)
                    //(this@ConnectedActivity.notifications.adapter as NotificationAdapter).notifyDataSetChanged()
//                    toast(sbn.packageName)
                    var table = HashMap<String, View>()
                    val views = arrayListOf(sbn.notification.contentView, sbn.notification.bigContentView, sbn.notification.headsUpContentView, sbn.notification.tickerView)

                    sbn.notification.extras.keySet().forEach { key ->
                        println("${key}: ${sbn.notification.extras[key]}");
                    }

                    views
                        .filter { it != null }
                        .map { it.infalte(context) }
                        .forEach { it.table(context, table) }

                    if (sbn.packageName == "com.google.android.apps.maps") {
                        val childCount = this@ConnectedActivity.notifications.childCount

                        val notificationData = MapsNotificationData(sbn.notification, table)
                        fun snd() {
                            val bytes = mapper.writeValueAsBytes(notificationData.draw().render())
//                            Data.toSend.send(bytes)
                            if(Connected.socket?.isConnected!!) {
                                Connected.ostream?.write(bytes.size.toByteArray(ByteOrder.LITTLE_ENDIAN))
                                Connected.ostream?.write(bytes)!!
                            }
                        }
                        launch { snd() }

                        val MapsNotification = UI {
                            linearLayout {
                                backgroundColor = Color.GREEN
                                imageView(notificationData.drawable)
                                onClick {
                                    snd()
                                }
                                verticalLayout {
                                    textView(notificationData.distance)
                                    textView(notificationData.currentStreet + " -> " + notificationData.nextStreet)
                                    textView(notificationData.totalDistance)
                                }
                            }
                        }
                        val (remove, index) = indecies.getIndex("maps", childCount)
                        if(remove) {
                            this@ConnectedActivity.notifications.removeViewAt(index)
                        }

                        this@ConnectedActivity.notifications.addView(MapsNotification.view, index)
                    }
                }
            }
        }
    }
}


class Notifications :  HashMap<String, Int>() {

    fun getIndex(key: String, value: Int): Pair<Boolean, Int> {
        return if(this.containsKey(key)){
            Pair(true, this[key]!!)
        }else{
            this[key] = value
            Pair(false, value)
        }
    }
}

fun Int.toByteArray(bo: ByteOrder = ByteOrder.BIG_ENDIAN): ByteArray {
    val buf = ByteBuffer.allocate(4)
    buf.order(bo)
    buf.putInt(this)
    buf.flip()

    return buf.array()
}



fun RemoteViews.infalte(context: Context): ViewGroup {
    var inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    val viewGroup = inflater.inflate(this.layoutId, null) as ViewGroup
    this.reapply(context, viewGroup)

    return  viewGroup
}

fun ViewGroup.table(context: Context, map: HashMap<String, View>){
    this.childrenSequence().forEach { child ->
        if(child is ViewStub){
            child.inflate()
        }

        if(child.id != -1) {

            println("${child.id}(${context.resources.getResourceEntryName(child.id)}) ${child.javaClass}")
            map.put(context.resources.getResourceEntryName(child.id), child)
        }

        if(child.tag != null) {
            println("${child.tag}: ${child.javaClass}")
            //this@ConnectedActivity.notifications.addView(child)
        }


        if(child is ViewGroup) {
            child.table(context, map)
        }

        if(child is TextView) {
            println(child.text)
        }

        if(child is ImageButton){
            val toast = Toast(context)
            val view = ImageView(context)
            view.setImageDrawable(child.drawable)
            toast.duration = 10000;
            toast.view = view
//            toast.show()
        }

        if(child is ImageView){
            val toast = Toast(context)
            toast.duration = 10000;
            val view = ImageView(context)
            view.setImageDrawable(child.drawable)
            toast.view = view
//            toast.show()
        }

    }
}