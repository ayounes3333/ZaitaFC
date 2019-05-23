package com.zaita.aliyounes.zaitafc.helpers

import android.util.Pair

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

import java.util.Calendar.DATE
import java.util.Calendar.MONTH
import java.util.Calendar.YEAR

object DateTimeUtils {

    private val dbDateFormat = SimpleDateFormat("dd-MM-yyyy'T'HH:mm:ss a", Locale.getDefault())
    private val fullMessageDateFormat = SimpleDateFormat("MMM dd HH:mm", Locale.getDefault())
    private val messageDateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    private val dayDateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())

    // get formatted db string from current date
    val currentDbDateString: String
        get() = dbDateFormat.format(Date())

    // get date object for today without time (at 12 AM)
    // set the calendar to start of today
    val todayDate: Date
        get() {
            val c = Calendar.getInstance()
            c.set(Calendar.HOUR_OF_DAY, 0)
            c.set(Calendar.MINUTE, 0)
            c.set(Calendar.SECOND, 0)
            c.set(Calendar.MILLISECOND, 0)
            return c.time
        }

    // get formatted date time for message time
    fun getMessageDateTime(dbString: String): String {
        try {
            val date = dbDateFormat.parse(dbString)
            return if (date.after(todayDate)) {
                messageDateFormat.format(date)
            } else {
                fullMessageDateFormat.format(date)
            }
        } catch (e: ParseException) {
            e.printStackTrace()
            return ""
        }

    }

    // get date object from formatted db string
    fun fromDbDateString(dbString: String): Date? {
        try {
            return dbDateFormat.parse(dbString)
        } catch (e: ParseException) {
            e.printStackTrace()
            return null
        }

    }

    // get date object from formatted db string
    fun fromDayDateString(dayString: String): Date? {
        try {
            return dbDateFormat.parse(dayString)
        } catch (e: ParseException) {
            e.printStackTrace()
            return null
        }

    }

    // get formatted db string from date object
    fun toDbDateString(date: Date): String {
        return dbDateFormat.format(date)
    }

    // get formatted db string from date object
    fun toDayDateString(date: Date): String {
        return dbDateFormat.format(date)
    }

    // get day date string from formatted db string
    fun getDayStringFromDbString(dbString: String): String {
        try {
            return dayDateFormat.format(dbDateFormat.parse(dbString))
        } catch (e: ParseException) {
            e.printStackTrace()
            return ""
        }

    }

    fun getDiffYears(first: Date, last: Date): Int {
        val a = getCalendar(first)
        val b = getCalendar(last)
        var diff = b.get(YEAR) - a.get(YEAR)
        if (a.get(MONTH) > b.get(MONTH) || a.get(MONTH) == b.get(MONTH) && a.get(DATE) > b.get(DATE)) {
            diff--
        }
        return diff
    }

    private fun getCalendar(date: Date): Calendar {
        val cal = Calendar.getInstance(Locale.US)
        cal.time = date
        return cal
    }

    // check if two dates and durations intersect
    fun isDatesIntersect(reservationDate: Date, reservationDurationHrs: Int, otherDate: Date, otherDurationHrs: Int): Boolean {
        val reservationDateLimit: Date
        val otherDateLimit: Date
        val cal = Calendar.getInstance() // creates calendar
        cal.time = reservationDate // sets calendar time/date
        cal.add(Calendar.HOUR_OF_DAY, reservationDurationHrs) // add hours
        reservationDateLimit = cal.time // returns new date object, hours in the future
        cal.time = otherDate // sets calendar time/date
        cal.add(Calendar.HOUR_OF_DAY, otherDurationHrs) // add hours
        otherDateLimit = cal.time // returns new date object, hours in the future

        return reservationDate.after(otherDate) && reservationDate.before(otherDateLimit) || reservationDateLimit.after(otherDate) && reservationDateLimit.before(otherDateLimit)
    }

    // check if a date and duration intersect with at least one date of the list
    fun isDateIntersectWithOneOfDates(reservationDate: Date, reservationDurationHrs: Int, otherDates: List<Pair<Date, Int>>): Boolean {
        for (otherDate in otherDates) {
            if (isDatesIntersect(reservationDate, reservationDurationHrs, otherDate.first, otherDate.second))
                return true
        }
        return false
    }
}
