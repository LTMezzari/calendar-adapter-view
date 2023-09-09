package mezzari.torres.lucas.calendar_adapter_view.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import mezzari.torres.lucas.calendar.CalendarAdapterView
import mezzari.torres.lucas.calendar.adapter.month.MonthAdapter
import org.joda.time.DateTime

/**
 * @author Lucas T. Mezzari
 * @since 08/09/2023
 */
class CustomMonthAdapter(
    private val context: Context,
    private val startDateTime: DateTime,
    private val endDateTime: DateTime
) : MonthAdapter<CustomMonthAdapter.CustomViewHolder>() {

    override fun getNextPage(current: DateTime): DateTime? {
        if (current
                .withDayOfMonth(1)
                .withTimeAtStartOfDay()
                .isEqual(
                    endDateTime
                        .withDayOfMonth(1)
                        .withTimeAtStartOfDay()
                )
        ) {
            return null
        }
        return super.getNextPage(current)
    }

    override fun getPreviousPage(current: DateTime): DateTime? {
        if (current
                .withDayOfMonth(1)
                .withTimeAtStartOfDay()
                .isEqual(
                    startDateTime
                        .withDayOfMonth(1)
                        .withTimeAtStartOfDay()
                )
        ) {
            return null
        }
        return super.getPreviousPage(current)
    }

    override fun onBindDayViewHolder(
        day: DateTime,
        date: DateTime,
        holder: CustomViewHolder
    ) {
        (holder.view as? TextView)?.text = day.dayOfMonth.toString()
    }

    override fun onCreateViewHolder(
        viewType: Int,
        container: ViewGroup?
    ): CustomViewHolder {
        return CustomViewHolder(TextView(context))
    }

    class CustomViewHolder(itemView: View) : CalendarAdapterView.ViewHolder(itemView)
}