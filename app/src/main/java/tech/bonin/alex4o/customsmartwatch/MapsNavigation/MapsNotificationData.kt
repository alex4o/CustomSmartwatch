package tech.bonin.alex4o.customsmartwatch.MapsNavigation

import android.app.Notification
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import org.jetbrains.anko.image
import org.jetbrains.anko.imageBitmap
import java.text.SimpleDateFormat
import java.util.*
import kotlin.properties.Delegates

/**
 * Created by alex4o on 1/26/18.

 id's are inconsistent

 */

/*
01-26 16:55:41.085 13144-13144/tech.bonin.alex4o.customsmartwatch I/System.out: 2131034928(lockscreen_notification_icon) class android.widget.ImageView
01-26 16:55:41.085 13144-13144/tech.bonin.alex4o.customsmartwatch I/System.out: 2131034929(lockscreen_directions) class android.widget.TextView
01-26 16:55:41.085 13144-13144/tech.bonin.alex4o.customsmartwatch I/System.out: 2131034930(lockscreen_eta) class android.widget.TextView
01-26 16:55:41.086 13144-13144/tech.bonin.alex4o.customsmartwatch I/System.out: 2131034931(lockscreen_oneliner) class android.widget.TextView
01-26 16:55:41.086 13144-13144/tech.bonin.alex4o.customsmartwatch I/System.out: 2131034918(nav_notification_icon) class android.widget.ImageView
01-26 16:55:41.086 13144-13144/tech.bonin.alex4o.customsmartwatch I/System.out: 2131034919(nav_title) class android.widget.TextView
01-26 16:55:41.087 13144-13144/tech.bonin.alex4o.customsmartwatch I/System.out: 2131034920(nav_description) class android.widget.TextView
01-26 16:55:41.087 13144-13144/tech.bonin.alex4o.customsmartwatch I/System.out: 2131034921(nav_time) class android.widget.TextView
01-26 16:55:41.087 13144-13144/tech.bonin.alex4o.customsmartwatch I/System.out: 2131034922(dismiss_nav) class android.widget.Button
01-26 16:55:41.088 13144-13144/tech.bonin.alex4o.customsmartwatch I/System.out: 2131034924(heads_up_notification_icon) class android.widget.ImageView
01-26 16:55:41.088 13144-13144/tech.bonin.alex4o.customsmartwatch I/System.out: 2131034925(heads_up_distance) class android.widget.TextView
01-26 16:55:41.088 13144-13144/tech.bonin.alex4o.customsmartwatch I/System.out: 2131034926(heads_up_location) class android.widget.TextView
 */
fun String.ascii(): String {
    return this.replace(Regex("[^\\x00-\\x7F]"), "")
}



@JsonPropertyOrder("distance", "currentStreet", "nextStreet", "travelTime", "travelDistance", "eta" )
class MapsNotificationData(notification: Notification, views: HashMap<String, View>) {
    private val resultFormat = SimpleDateFormat("HH:mm")
    private val clockFormat = SimpleDateFormat("HH:mm:ss")
    private val inputFormat = SimpleDateFormat("hh:mm a")

    @JsonIgnore
    val drawable: Drawable = (views["nav_notification_icon"] as ImageView).drawable

    @JsonIgnore
    val image: Bitmap = (drawable as BitmapDrawable).bitmap
    val distance: String = (views["heads_up_distance"] as TextView).text.toString().ascii()

    var currentStreet: String by Delegates.notNull()
    var nextStreet: String by Delegates.notNull()
    var travelTime: String by Delegates.notNull()
    var totalDistance: String by Delegates.notNull()
    var eta: String by Delegates.notNull()

    init {
        val (current_streem, next_street) = (views["nav_description"] as TextView).text.toString().split("toward").map { it.trim() }
        val (travel_time, total_distance, eta) = (views["nav_time"] as TextView).text.toString().split("·").map { it.ascii().trim() }

        this.totalDistance = total_distance
        this.travelTime = travel_time
        this.eta = resultFormat.format(inputFormat.parse(eta.dropLast(3).trim()))
        this.currentStreet = current_streem
        this.nextStreet = next_street


    }

    fun draw(): Screen {
        val clock = clockFormat.format(Calendar.getInstance().time)
        return screen {
            line(distance, 0, 0)
            line(totalDistance, 14 - totalDistance.length, 0)
            center(currentStreet)
            center(nextStreet)
            line(eta, 0 ,3)
            line(travelTime, 14 - travelTime.length, 3)

            center(clock,  4*9)
        }
    }
}

class Screen(val charHeight: Int = 8, val charWidth: Int = 6, val lineWidth: Int = 14,  val charPadding: Int = 1) {
    val list = ArrayList<Triple<Int, Int, String>>()
    var x: Int = 0
    var y: Int = 0

    fun text(text: String, x: Int = 0): Unit {
        list.add(Triple(x, y, text))
    }

    fun text(text: String, x: Int, y: Int): Unit {
        this.y = y
        list.add(Triple(x,y, text))
    }

    fun line(text: String, x: Int, y: Int) {
        list.add(Triple(x * charWidth, y * charHeight, text))
        this.y = y * charHeight
    }

    fun line(text: String, x: Int = 0) {
        y += charHeight
        list.add(Triple(x * charWidth, y, text))
    }

    fun char(x: Int, y: Int): Pair<Int, Int> {
        return Pair(x * charWidth, y * charHeight);
    }

    fun pixel(coord: Int): Int{
        return coord
    }


    fun center(text: String) {
        y += charHeight
        text(text, (7 - text.length/2)*6, y)
    }

    fun center(text: String, y: Int) {
        text(text, (7 - text.length/2)*6, y)
    }

    fun render(): Array<Any> {
        return list.flatMap { it.toList() }.toTypedArray()
    }
}



fun screen(init: Screen.() -> Unit): Screen {
    val screen = Screen()
    screen.init()
    return screen
}
