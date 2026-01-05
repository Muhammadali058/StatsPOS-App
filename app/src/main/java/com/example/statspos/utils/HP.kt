package com.example.statspos.utils

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.statspos.domain.models.DropdownItem
import com.example.statspos.domain.models.items.Categories
import com.example.statspos.domain.models.main.Branches
import com.example.statspos.domain.models.main.LocalClients
import com.example.statspos.domain.models.utilities.settings.AdminPasswords
import com.example.statspos.domain.models.utilities.settings.AdminSettings
import com.example.statspos.domain.models.utilities.settings.Passwords
import com.example.statspos.domain.models.utilities.users.UserRights
import com.example.statspos.domain.models.utilities.users.Users
import com.google.gson.Gson
import com.google.gson.JsonObject
import okhttp3.internal.http2.Settings
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

object HP {
    var localClient: LocalClients? = null

    var clientId:Int = 1
    var branchId:Int = 1
    var branchGroupId:Int = 0

    var user = Users()
    var userRights = UserRights()
    var settings = Settings()
    var passwords = Passwords()
    var adminSettings = AdminSettings()
    var adminPasswords = AdminPasswords()

    var autoCompleteItems = emptyList<String>()
    var categories = emptyList<DropdownItem>()
    var subCategories = emptyList<DropdownItem>()
    var packages = emptyList<DropdownItem>()
    var customers = emptyList<DropdownItem>()
    var vendors = emptyList<DropdownItem>()
    var suppliers = emptyList<DropdownItem>()
    var fixedAccounts = emptyList<DropdownItem>()
    var users = emptyList<DropdownItem>()
    var accountCategories = emptyList<DropdownItem>()
    var expenses = emptyList<DropdownItem>()
    var subExpenses = emptyList<DropdownItem>()
    var banks = emptyList<DropdownItem>()
    var subBanks = emptyList<DropdownItem>()
    var warehouses = emptyList<DropdownItem>()
    var branches = emptyList<DropdownItem>()
    var branchGroups = emptyList<DropdownItem>()
    var printSizes = emptyList<DropdownItem>()
    var printLanguages = emptyList<DropdownItem>()
    var accounts = emptyList<DropdownItem>()
    var userTypes = emptyList<DropdownItem>()
    var shifts = emptyList<DropdownItem>()
    var branch = Branches()

    fun setDropdowns(jsonObject: JsonObject){
        settings = Gson().get<Settings>(jsonObject.get("settings").asJsonObject)
        passwords = Gson().get<Passwords>(jsonObject.get("passwords").asJsonObject)
        adminSettings = Gson().get<AdminSettings>(jsonObject.get("adminSettings").asJsonObject)
        adminPasswords = Gson().get<AdminPasswords>(jsonObject.get("adminPasswords").asJsonObject)

        autoCompleteItems = Gson().getListOf<String>(jsonObject.get("items").asJsonArray)
        categories = Gson().getListOf<DropdownItem>(jsonObject.get("categories").asJsonArray)
        subCategories = jsonObject.get("subCategories").asJsonArray.map { obj ->
            DropdownItem(
                id = obj.asJsonObject.get("id").asLong,
                name = obj.asJsonObject.get("name").asString,
                mainId = obj.asJsonObject.get("categoryId").asLong,
            )
        }
        packages = Gson().getListOf<DropdownItem>(jsonObject.get("packages").asJsonArray)
        customers = Gson().getListOf<DropdownItem>(jsonObject.get("customers").asJsonArray)
        vendors = Gson().getListOf<DropdownItem>(jsonObject.get("vendors").asJsonArray)
        suppliers = Gson().getListOf<DropdownItem>(jsonObject.get("suppliers").asJsonArray)
        fixedAccounts = Gson().getListOf<DropdownItem>(jsonObject.get("fixedAccounts").asJsonArray)
        users = Gson().getListOf<DropdownItem>(jsonObject.get("users").asJsonArray)
        accountCategories = Gson().getListOf<DropdownItem>(jsonObject.get("accountCategories").asJsonArray)
        expenses = Gson().getListOf<DropdownItem>(jsonObject.get("expenses").asJsonArray)
        subExpenses = jsonObject.get("subExpenses").asJsonArray.map { obj ->
            DropdownItem(
                id = obj.asJsonObject.get("id").asLong,
                name = obj.asJsonObject.get("name").asString,
                mainId = obj.asJsonObject.get("expenseId").asLong,
            )
        }
        banks = Gson().getListOf<DropdownItem>(jsonObject.get("banks").asJsonArray)
        subBanks = jsonObject.get("subBanks").asJsonArray.map { obj ->
            DropdownItem(
                id = obj.asJsonObject.get("id").asLong,
                name = obj.asJsonObject.get("name").asString,
                mainId = obj.asJsonObject.get("bankId").asLong,
            )
        }
        warehouses = Gson().getListOf<DropdownItem>(jsonObject.get("warehouses").asJsonArray)
        branches = Gson().getListOf<DropdownItem>(jsonObject.get("branches").asJsonArray)
        branchGroups = Gson().getListOf<DropdownItem>(jsonObject.get("branchGroups").asJsonArray)
        printSizes = Gson().getListOf<DropdownItem>(jsonObject.get("printSizes").asJsonArray)
        printLanguages = Gson().getListOf<DropdownItem>(jsonObject.get("printLanguages").asJsonArray)
        accounts = Gson().getListOf<DropdownItem>(jsonObject.get("accounts").asJsonArray)
        userTypes = Gson().getListOf<DropdownItem>(jsonObject.get("userTypes").asJsonArray)
        shifts = Gson().getListOf<DropdownItem>(jsonObject.get("shifts").asJsonArray)
        branch = Gson().get<Branches>(jsonObject.get("branch").asJsonObject)
    }

    fun getImageUrl(imageUrl: String ): String {
        return DB.HOST + clientId.toString() + "/images/" + imageUrl
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getFormatedDate(localDate: LocalDate): String{
        val customFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        return localDate.format(customFormatter)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getFormatedTime(localTime: LocalTime): String{
        val customFormatter = DateTimeFormatter.ofPattern("h:m a")
        return localTime.format(customFormatter)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getZonedDate(localDate: LocalDate): String{
        val zonedDateTime = ZonedDateTime.of(
            localDate,
            LocalTime.now(),
            ZoneId.systemDefault()
        )

        val isoString = zonedDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        return isoString
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getZonedTime(localTime: LocalTime): String{
        val zonedDateTime = ZonedDateTime.of(
            LocalDate.now(),
            localTime,
            ZoneId.systemDefault()
        )

        val isoString = zonedDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        return isoString
    }

    @RequiresApi(Build.VERSION_CODES.O)
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
