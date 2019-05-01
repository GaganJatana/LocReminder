package chicmic.com.locreminder.alarm

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import chicmic.com.locreminder.R
import chicmic.com.locreminder.datamodel.Task

//import javax.swing.text.StyleConstants.setIcon



class AdapterRecycleView (private val myDataset: MutableList<Task>,context: Context) : RecyclerView.Adapter<AdapterRecycleView.ViewHolder>() {

    val mContext = context

    class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {


        val locname = itemView!!.findViewById<TextView>(R.id.name)
        val name = itemView!!.findViewById<TextView>(R.id.namelocation)
        val time = itemView!!.findViewById<TextView>(R.id.time)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val task: Task = myDataset[position]
        holder.name.text = task.name
        holder.locname.text = task.locationName
        Log.d("locname",task.locationName)
        holder.time.text = task.date+" "+task.time

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layout = LayoutInflater.from(parent.context).inflate(R.layout.task_row, parent, false)
        return ViewHolder(layout)
    }

    override fun getItemCount(): Int {
        return myDataset.size
    }
}
//    fun removeItem(position: Int) {
//        // Toast.makeText(mContext as DashBoard,position.toString(),Toast.LENGTH_LONG).show()
//        var contact = myDataset.get(position)
//        myDataset.removeAt(position);
//        alert(mContext, contact, position)
//
//        notifyItemRemoved(position);
//    }

//    fun call(position: Int)
//    {
//       mcallDelete.call( myDataset[position])
//        notifyDataSetChanged()
//    }
//
//    fun restoreItem(item: Contact, position: Int) {
//        myDataset.add(position,item)
//        notifyDataSetChanged()
//    }
//
//    fun alert(context:Context,contact: Contact,position: Int)
//    {
//        AlertDialog.Builder(context)
//                .setTitle("Delete entry")
//                .setMessage("Are you sure you want to delete this entry?")
//                .setPositiveButton(android.R.string.yes) { dialog, which ->
//                    mcallDelete.delete(contact,position)
//                }
//
//                .setNegativeButton(android.R.string.no){ dialog, which ->
//                        restoreItem(contact,position)
//                }
//                .setIcon(android.R.drawable.ic_dialog_alert)
//                .show()
//    }







