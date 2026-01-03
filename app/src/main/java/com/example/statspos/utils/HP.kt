package com.example.statspos.utils

import com.example.statspos.domain.models.main.LocalClients
import com.example.statspos.domain.models.utilities.users.Users
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

object HP {
    var localClient: LocalClients? = null

    var clientId:Int = 1
    val branchId:Int = 1
    val branchGroupId:Int = 0

    val user: Users = Users()


    fun getImageUrl(imageUrl: String ): String {
        return DB.HOST + clientId.toString() + "/images/" + imageUrl
    }

    fun getFormatedDate(localDate: LocalDate): String{
        val customFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        return localDate.format(customFormatter)
    }

    fun getFormatedTime(localTime: LocalTime): String{
        val customFormatter = DateTimeFormatter.ofPattern("h:m a")
        return localTime.format(customFormatter)
    }

    fun getZonedDate(localDate: LocalDate): String{
        val zonedDateTime = ZonedDateTime.of(
            localDate,
            LocalTime.now(),
            ZoneId.systemDefault()
        )

        val isoString = zonedDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        return isoString
    }

    fun getZonedTime(localTime: LocalTime): String{
        val zonedDateTime = ZonedDateTime.of(
            LocalDate.now(),
            localTime,
            ZoneId.systemDefault()
        )

        val isoString = zonedDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        return isoString
    }

    fun getZonedDateTime(localDate: LocalDate, localTime: LocalTime): String{
        val zonedDateTime = ZonedDateTime.of(
            localDate,
            localTime,
            ZoneId.systemDefault()
        )

        val isoString = zonedDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        return isoString
    }

}
