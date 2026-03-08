package com.example.statspos.utils

import com.example.statspos.domain.models.DropdownItem
import com.example.statspos.domain.models.items.Items
import com.example.statspos.domain.models.main.Branches
import com.example.statspos.domain.models.main.LocalClients
import com.example.statspos.domain.models.utilities.settings.AdminPasswords
import com.example.statspos.domain.models.utilities.settings.AdminSettings
import com.example.statspos.domain.models.utilities.settings.Passwords
import com.example.statspos.domain.models.utilities.settings.Settings
import com.example.statspos.domain.models.utilities.users.UserRights
import com.example.statspos.domain.models.utilities.users.Users
import com.google.gson.Gson
import com.google.gson.JsonObject
import net.objecthunter.exp4j.ExpressionBuilder
import java.text.DecimalFormat
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.abs

object HP {
    var localClient: LocalClients? = null

    const val ITEMS_PER_PAGE = 50

    var clientId: Int = 1
    var branchId: Int = 1
    var branchGroupId: Int = 0

    var user = Users(id = 1, clientId = 1, branchId = 1)
    var userRights = UserRights()
    var settings = Settings()
    var passwords = Passwords()
    var adminSettings = AdminSettings()
    var adminPasswords = AdminPasswords()

    var autoCompleteItems = emptyList<String>()
    var categories = emptyList<DropdownItem>()
    var subCategories = emptyList<DropdownItem>()
    var packages = emptyList<DropdownItem>()
    var purchaseOrders = emptyList<DropdownItem>()
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
    var gatepasses = emptyList<DropdownItem>()

    // region ComboBox Lists
    // Items
    var itemFilters = listOf(
        DropdownItem(0, "By Name"),
        DropdownItem(1, "By Barcode"),
        DropdownItem(2, "By Ref. Code"),
        DropdownItem(3, "By Urduname"),
        DropdownItem(4, "By Cost"),
        DropdownItem(5, "By Retail"),
        DropdownItem(6, "By Wholesale"),
        DropdownItem(7, "By Carton Rate"),
        DropdownItem(8, "By Carton Size"),
    )

    val mop = listOf(
        DropdownItem(1L, "Cash"),
        DropdownItem(2L, "Bank"),
    )
    val defaultRate = listOf(
        DropdownItem(0L, "Retail"),
        DropdownItem(1L, "Wholesale"),
    )
    val defaultDiscount = listOf(
        DropdownItem(0L, "Rs."),
        DropdownItem(1L, "Percent"),
    )
    // Sales
    val salesPostedBillsSearchBy = listOf(
        DropdownItem(0L, "By Name"),
        DropdownItem(1L, "By Sr."),
        DropdownItem(2L, "By Invoice No."),
    )
    val salesOn = listOf(
        DropdownItem(1L, "Cash"),
        DropdownItem(2L, "Credit"),
    )
    val salesType = listOf(
        DropdownItem(1L, "Sales"),
        DropdownItem(2L, "Return"),
    )
    val salesRetailType = listOf(
        DropdownItem(1L, "Retail"),
        DropdownItem(2L, "Wholesale"),
    )

    // Purchase
    val purchasePostedBillsSearchBy = listOf(
        DropdownItem(0L, "By Name"),
        DropdownItem(1L, "By Sr."),
        DropdownItem(2L, "By Invoice No."),
        DropdownItem(3L, "By Ref. Invoice No."),
    )
    val purchaseOn = listOf(
        DropdownItem(1L, "Cash"),
        DropdownItem(2L, "Credit"),
    )
    val purchaseType = listOf(
        DropdownItem(1L, "Purchase"),
        DropdownItem(2L, "Return"),
    )
    val chartDurations = listOf(
        DropdownItem(1L, "Day"),
        DropdownItem(2L, "Week"),
        DropdownItem(3L, "Month"),
        DropdownItem(4L, "Year"),
    )

    // Stock
    val stockShowing = listOf(
        DropdownItem(1L, "Current Stock"),
        DropdownItem(2L, "Opening Stock"),
    )
    val stockAt = listOf(
        DropdownItem(1L, "Cost"),
        DropdownItem(2L, "Retail"),
        DropdownItem(3L, "Wholesale"),
    )
    val stockExpiry = listOf(
        DropdownItem(1L, "All"),
        DropdownItem(2L, "Expirable"),
        DropdownItem(3L, "Expired"),
    )
    val stockType = listOf(
        DropdownItem(1L, "All"),
        DropdownItem(2L, "Zero Stock"),
        DropdownItem(3L, "Not Zero"),
        DropdownItem(4L, "About to End"),
        DropdownItem(5L, "Over Stock"),
    )
    // endregion

    fun setDropdowns(jsonObject: JsonObject) {
        settings = Gson().get<Settings>(jsonObject.get("settings").asJsonObject)
        passwords = Gson().get<Passwords>(jsonObject.get("passwords").asJsonObject)
        adminSettings = Gson().get<AdminSettings>(jsonObject.get("adminSettings").asJsonObject)
        adminPasswords = Gson().get<AdminPasswords>(jsonObject.get("adminPasswords").asJsonObject)

        autoCompleteItems = Gson().getListOf<Items>(jsonObject.get("items").asJsonArray).map {
            it.itemname!!
        }

//        autoCompleteItems = Gson().getListOf<String>(jsonObject.get("items").asJsonArray)
        categories = Gson().getListOf<DropdownItem>(jsonObject.get("categories").asJsonArray)
        subCategories = jsonObject.get("subCategories").asJsonArray.map { obj ->
            DropdownItem(
                id = obj.asJsonObject.get("id").asLong,
                name = obj.asJsonObject.get("name").asString,
                mainId = obj.asJsonObject.get("categoryId").asLong,
            )
        }
        packages = Gson().getListOf<DropdownItem>(jsonObject.get("packages").asJsonArray)
        purchaseOrders =
            Gson().getListOf<DropdownItem>(jsonObject.get("purchaseOrders").asJsonArray)
        customers = Gson().getListOf<DropdownItem>(jsonObject.get("customers").asJsonArray)
        vendors = Gson().getListOf<DropdownItem>(jsonObject.get("vendors").asJsonArray)
        suppliers = Gson().getListOf<DropdownItem>(jsonObject.get("suppliers").asJsonArray)
        fixedAccounts = Gson().getListOf<DropdownItem>(jsonObject.get("fixedAccounts").asJsonArray)
        users = Gson().getListOf<DropdownItem>(jsonObject.get("users").asJsonArray)
        accountCategories =
            Gson().getListOf<DropdownItem>(jsonObject.get("accountCategories").asJsonArray)
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
        printLanguages =
            Gson().getListOf<DropdownItem>(jsonObject.get("printLanguages").asJsonArray)
        accounts = Gson().getListOf<DropdownItem>(jsonObject.get("accounts").asJsonArray)
        userTypes = Gson().getListOf<DropdownItem>(jsonObject.get("userTypes").asJsonArray)
        shifts = Gson().getListOf<DropdownItem>(jsonObject.get("shifts").asJsonArray)
        branch = Gson().get<Branches>(jsonObject.get("branch").asJsonObject)
    }

    fun getImageUrl(imageUrl: String): String {
        return DB.HOST + clientId.toString() + "/images/" + imageUrl
    }

    fun formatDecimal(rate: Double?, numberOfDecimals: Int = 2, mustDecimals: Int = 0): String {
        if (rate == null) {
            return ""
        }

        val pattern = if (numberOfDecimals > 0) {
            "0." + "#".repeat(numberOfDecimals)
        } else {
            "0"
        }

        val df = DecimalFormat(pattern)
        val value = df.format(rate)

        if (value.contains('.'))
            return value
        else {
            return if (mustDecimals > 0)
                value + "." + "0".repeat(mustDecimals)
            else
                value
        }
    }

    fun getNoneDropdownItem(noneText: String = ""): DropdownItem {
        return DropdownItem(0L, noneText)
    }

    fun getDoubleValue(value: String): Double {
        return try {
            value.toDouble()
        } catch (e: Exception) {
            0.0
        }
    }

    fun getIntValue(value: String): Int {
        return try {
            value.toInt()
        } catch (e: Exception) {
            0
        }
    }

    fun getLongValue(value: String): Long {
        return try {
            value.toLong()
        } catch (e: Exception) {
            0L
        }
    }

    fun evaluateExpression(expression: String): String {
        return try {
            val result = ExpressionBuilder(expression)
                .build()
                .evaluate()

            if (result % 1 == 0.0)
                result.toLong().toString()
            else
                result.toString()

        } catch (e: Exception) {
            expression
        }
    }

    fun getDropdownNameById(id: Long, list: List<DropdownItem>, defaultValue: String = ""): String {
        return list.firstOrNull { it.id == id }?.name ?: defaultValue
    }

    fun getDropdownById(id: Long, list: List<DropdownItem>): DropdownItem {
        return list.firstOrNull { it.id == id } ?: getNoneDropdownItem()
    }

    fun getBalanceWithLabel(balance: Double): String {
        val label = getReceivableOrPayableLabel(balance);
        return "Balance: " + abs(balance).toString() + " " + label
    }

    fun getReceivableOrPayableLabel(balance: Double): String {
        return if (balance > 0)
            "(R)";
        else if (balance < 0)
            "(P)";
        else
            ""
    }

    // region Datetime
    fun getFormatedDate(localDate: LocalDate = LocalDate.now()): String {
        val customFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        return localDate.format(customFormatter)
    }

    fun getFormatedTime(localTime: LocalTime = LocalTime.now()): String {
        val customFormatter = DateTimeFormatter.ofPattern("h:m a")
        return localTime.format(customFormatter)
    }

    fun toLocalDate(isoDateString: String): LocalDate {
        return Instant.parse(isoDateString)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
    }

    fun toLocalTime(isoTimeString: String): LocalTime {
        val temp = isoTimeString.split(".")[0] + "+05:00"

        return Instant.parse(temp)
            .atZone(ZoneId.systemDefault())
            .toLocalTime()
    }

    fun getZonedDate(localDate: LocalDate = LocalDate.now()): String {
        val zonedDateTime = ZonedDateTime.of(
            localDate,
            LocalTime.now(),
            ZoneId.systemDefault()
        )

        val isoString = zonedDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        return isoString
    }

    fun getZonedDateWithTime(localDate: LocalDate = LocalDate.now(), localTime: LocalTime): String {
        val zonedDateTime = ZonedDateTime.of(
            localDate,
            localTime,
            ZoneId.systemDefault()
        )

        val isoString = zonedDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        return isoString
    }

    fun getZonedDateWithFromTime(localDate: LocalDate = LocalDate.now()): String {
        val time = LocalTime.of(0, 0, 0)

        val zonedDateTime = ZonedDateTime.of(
            localDate,
            time,
            ZoneId.systemDefault()
        )

        val isoString = zonedDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        return isoString
    }

    fun getZonedDateWithToTime(localDate: LocalDate = LocalDate.now()): String {
        val time = LocalTime.of(23, 59, 59)

        val zonedDateTime = ZonedDateTime.of(
            localDate,
            time,
            ZoneId.systemDefault()
        )

        val isoString = zonedDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        return isoString
    }

    fun getZonedTime(localTime: LocalTime = LocalTime.now()): String {
        val zonedDateTime = ZonedDateTime.of(
            LocalDate.now(),
            localTime,
            ZoneId.systemDefault()
        )

        val isoString = zonedDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        return isoString
    }

    fun getZonedDateTime(localDate: LocalDate, localTime: LocalTime): String {
        val zonedDateTime = ZonedDateTime.of(
            localDate,
            localTime,
            ZoneId.systemDefault()
        )

        val isoString = zonedDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        return isoString
    }
    // endregion

}
