package tech.bonin.alex4o.customsmartwatch

import android.bluetooth.BluetoothDevice
import android.content.Context
import android.service.notification.StatusBarNotification
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

/**
 * Created by alex4o on 1/22/18.
 */
class DeviceAdapter(private val mContext: Context, list: List<BluetoothDevice>) : ArrayAdapter<BluetoothDevice>(mContext, 0, list) {
    private var btdevices : List<BluetoothDevice>

    init {
        btdevices = list
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup) : View {


        var view : ViewHolder
        if(convertView == null){
            view = ViewHolder(LayoutInflater.from(mContext).inflate(android.R.layout.simple_list_item_1, parent, false))
        }else{
            view = ViewHolder(convertView)
        }

        val device = btdevices.get(position)

        view.textView.text = device.name + "\n" + device.address

        return view.textView;
    }

    class ViewHolder(rootView: View) {

        var textView: TextView

        init {
            textView = rootView.findViewById(android.R.id.text1) as TextView
        }


    }
}

class NotificationAdapter(private val mContext: Context, list: List<StatusBarNotification>) : ArrayAdapter<StatusBarNotification>(mContext, 0, list) {
    private var notifications : List<StatusBarNotification>

    init {
        notifications = list
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup) : View {


        var view : ViewHolder
        if(convertView == null){
            view = ViewHolder(LayoutInflater.from(mContext).inflate(android.R.layout.simple_list_item_1, parent, false))
        }else{
            view = ViewHolder(convertView)
        }

        val sbn = notifications.get(position)
        val notification = sbn.notification
        if(notification.extras != null){
            for(key in notification.extras.keySet()){
                println(notification.extras.get(key))
            }
            view.textView.text = (notification.tickerText.toString())
        }else{
            view.textView.text = notification.tickerText.toString()
        }


        return view.textView;
    }

    class ViewHolder(rootView: View) {

        var textView: TextView

        init {
            textView = rootView.findViewById(android.R.id.text1) as TextView
        }


    }
}