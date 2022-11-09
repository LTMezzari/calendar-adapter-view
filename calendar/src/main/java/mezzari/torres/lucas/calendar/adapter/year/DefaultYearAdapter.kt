package mezzari.torres.lucas.calendar.adapter.year

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.setPadding
import mezzari.torres.lucas.calendar.CalendarAdapterView
import org.joda.time.DateTime

/**
 * @author Lucas T. Mezzari
 * @since 09/11/22
 */
internal class DefaultYearAdapter(private val context: Context) :
    YearAdapter<DefaultYearAdapter.YearViewHolder>() {

    override fun onCreateViewHolder(viewType: Int, container: ViewGroup?): YearViewHolder {
        val view = TextView(context)
        view.setPadding(32)
        return YearViewHolder(view)
    }

    override fun onBindMonthViewHolder(
        month: DateTime, date: DateTime, holder: YearViewHolder
    ) {
        (holder.view as? TextView)?.apply {
            text = month.toString("MMM")
        }
    }

    class YearViewHolder(view: View) : CalendarAdapterView.ViewHolder(view)
}