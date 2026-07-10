package com.graphees.statspos.di

import com.graphees.statspos.data.remote.accounts.AccountCategoriesApi
import com.graphees.statspos.data.remote.accounts.AccountsApi
import com.graphees.statspos.data.remote.accounts.BanksApi
import com.graphees.statspos.data.remote.accounts.CustomersApi
import com.graphees.statspos.data.remote.accounts.EmployeesApi
import com.graphees.statspos.data.remote.accounts.ExpensesApi
import com.graphees.statspos.data.remote.accounts.FixedAccountsApi
import com.graphees.statspos.data.remote.accounts.SuppliersApi
import com.graphees.statspos.data.remote.accounts.VendorsApi
import com.graphees.statspos.data.remote.items.CategoriesApi
import com.graphees.statspos.data.remote.items.ItemsApi
import com.graphees.statspos.data.remote.items.LinkedItemsApi
import com.graphees.statspos.data.remote.items.PackagesApi
import com.graphees.statspos.data.remote.items.SubBarcodesApi
import com.graphees.statspos.data.remote.main.ClientsApi
import com.graphees.statspos.data.remote.main.MainApi
import com.graphees.statspos.data.remote.purchase.PurchaseApi
import com.graphees.statspos.data.remote.purchase.PurchaseItemsApi
import com.graphees.statspos.data.remote.purchase.PurchaseOrderItemsApi
import com.graphees.statspos.data.remote.purchase.PurchaseOrdersApi
import com.graphees.statspos.data.remote.purchase.RejectedItemsApi
import com.graphees.statspos.data.remote.reports.AccountReportsApi
import com.graphees.statspos.data.remote.reports.ItemsReportsApi
import com.graphees.statspos.data.remote.reports.ProfitReportsApi
import com.graphees.statspos.data.remote.reports.PurchaseReportsApi
import com.graphees.statspos.data.remote.reports.SalesReportsApi
import com.graphees.statspos.data.remote.reports.StockReportsApi
import com.graphees.statspos.data.remote.sales.SalesApi
import com.graphees.statspos.data.remote.sales.SalesItemsApi
import com.graphees.statspos.data.remote.sales.SalesOrderItemsApi
import com.graphees.statspos.data.remote.sales.SalesOrdersApi
import com.graphees.statspos.data.remote.utilities.AuditApi
import com.graphees.statspos.data.remote.utilities.BarcodeLabelsApi
import com.graphees.statspos.data.remote.utilities.SettingsApi
import com.graphees.statspos.data.remote.utilities.ShiftsApi
import com.graphees.statspos.data.remote.utilities.UsersApi
import com.graphees.statspos.data.remote.warehouse.GatepassItemsApi
import com.graphees.statspos.data.remote.warehouse.GatepassesApi
import com.graphees.statspos.data.remote.warehouse.StockEntriesApi
import com.graphees.statspos.data.remote.warehouse.WarehousesApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import retrofit2.Retrofit

@Module
@InstallIn(ViewModelComponent::class)
class NetworkModule {

    // region Accounts
    @Provides
    fun provideAccountCategoriesApi(retrofitInstance: Retrofit): AccountCategoriesApi {
        return retrofitInstance.create(AccountCategoriesApi::class.java)
    }

    @Provides
    fun provideAccountsApi(retrofitInstance: Retrofit): AccountsApi {
        return retrofitInstance.create(AccountsApi::class.java)
    }

    @Provides
    fun provideBanksApi(retrofitInstance: Retrofit): BanksApi {
        return retrofitInstance.create(BanksApi::class.java)
    }

    @Provides
    fun provideCustomersApi(retrofitInstance: Retrofit): CustomersApi {
        return retrofitInstance.create(CustomersApi::class.java)
    }

    @Provides
    fun provideEmployeesApi(retrofitInstance: Retrofit): EmployeesApi {
        return retrofitInstance.create(EmployeesApi::class.java)
    }

    @Provides
    fun provideExpensesApi(retrofitInstance: Retrofit): ExpensesApi {
        return retrofitInstance.create(ExpensesApi::class.java)
    }

    @Provides
    fun provideFixedAccountsApi(retrofitInstance: Retrofit): FixedAccountsApi {
        return retrofitInstance.create(FixedAccountsApi::class.java)
    }

    @Provides
    fun provideSuppliersApi(retrofitInstance: Retrofit): SuppliersApi {
        return retrofitInstance.create(SuppliersApi::class.java)
    }

    @Provides
    fun provideVendorsApi(retrofitInstance: Retrofit): VendorsApi {
        return retrofitInstance.create(VendorsApi::class.java)
    }
    // endregion

    // region Items
    @Provides
    fun provideCategoriesApi(retrofitInstance: Retrofit): CategoriesApi {
        return retrofitInstance.create(CategoriesApi::class.java)
    }

    @Provides
    fun provideItemsApi(retrofitInstance: Retrofit): ItemsApi {
        return retrofitInstance.create(ItemsApi::class.java)
    }

    @Provides
    fun provideLinkedItemsApi(retrofitInstance: Retrofit): LinkedItemsApi {
        return retrofitInstance.create(LinkedItemsApi::class.java)
    }

    @Provides
    fun providePackagesApi(retrofitInstance: Retrofit): PackagesApi {
        return retrofitInstance.create(PackagesApi::class.java)
    }

    @Provides
    fun provideSubBarcodesApi(retrofitInstance: Retrofit): SubBarcodesApi {
        return retrofitInstance.create(SubBarcodesApi::class.java)
    }
    // endregion

    // region Main
    @Provides
    fun provideClientsApi(@com.graphees.statspos.di.MainApi retrofitInstance: Retrofit): ClientsApi {
        return retrofitInstance.create(ClientsApi::class.java)
    }

    @Provides
    fun provideMainApi(retrofitInstance: Retrofit): MainApi {
        return retrofitInstance.create(MainApi::class.java)
    }
    // endregion

    // region Purchase
    @Provides
    fun providePurchaseApi(retrofitInstance: Retrofit): PurchaseApi {
        return retrofitInstance.create(PurchaseApi::class.java)
    }

    @Provides
    fun providePurchaseItemsApi(retrofitInstance: Retrofit): PurchaseItemsApi {
        return retrofitInstance.create(PurchaseItemsApi::class.java)
    }

    @Provides
    fun providePurchaseOrderItemsApi(retrofitInstance: Retrofit): PurchaseOrderItemsApi {
        return retrofitInstance.create(PurchaseOrderItemsApi::class.java)
    }

    @Provides
    fun providePurchaseOrdersApi(retrofitInstance: Retrofit): PurchaseOrdersApi {
        return retrofitInstance.create(PurchaseOrdersApi::class.java)
    }

    @Provides
    fun provideRejectedItemsApi(retrofitInstance: Retrofit): RejectedItemsApi {
        return retrofitInstance.create(RejectedItemsApi::class.java)
    }

    // endregion

    // region Reports
    @Provides
    fun provideAccountReportsApi(retrofitInstance: Retrofit): AccountReportsApi {
        return retrofitInstance.create(AccountReportsApi::class.java)
    }

    @Provides
    fun provideItemsReportsApi(retrofitInstance: Retrofit): ItemsReportsApi {
        return retrofitInstance.create(ItemsReportsApi::class.java)
    }

    @Provides
    fun provideProfitReportsApi(retrofitInstance: Retrofit): ProfitReportsApi {
        return retrofitInstance.create(ProfitReportsApi::class.java)
    }

    @Provides
    fun providePurchaseReportsApi(retrofitInstance: Retrofit): PurchaseReportsApi {
        return retrofitInstance.create(PurchaseReportsApi::class.java)
    }

    @Provides
    fun provideSalesReportsApi(retrofitInstance: Retrofit): SalesReportsApi {
        return retrofitInstance.create(SalesReportsApi::class.java)
    }

    @Provides
    fun provideStockReportsApi(retrofitInstance: Retrofit): StockReportsApi {
        return retrofitInstance.create(StockReportsApi::class.java)
    }

    // endregion

    // region Sales
    @Provides
    fun provideSalesApi(retrofitInstance: Retrofit): SalesApi {
        return retrofitInstance.create(SalesApi::class.java)
    }

    @Provides
    fun provideSalesItemsApi(retrofitInstance: Retrofit): SalesItemsApi {
        return retrofitInstance.create(SalesItemsApi::class.java)
    }

    @Provides
    fun provideSalesOrdersApi(retrofitInstance: Retrofit): SalesOrdersApi {
        return retrofitInstance.create(SalesOrdersApi::class.java)
    }

    @Provides
    fun provideSalesOrderItemsApi(retrofitInstance: Retrofit): SalesOrderItemsApi {
        return retrofitInstance.create(SalesOrderItemsApi::class.java)
    }
    // endregion

    // region Utilities
    @Provides
    fun provideAuditApi(retrofitInstance: Retrofit): AuditApi {
        return retrofitInstance.create(AuditApi::class.java)
    }

    @Provides
    fun provideBarcodeLabelsApi(retrofitInstance: Retrofit): BarcodeLabelsApi {
        return retrofitInstance.create(BarcodeLabelsApi::class.java)
    }

    @Provides
    fun provideSettingsApi(retrofitInstance: Retrofit): SettingsApi {
        return retrofitInstance.create(SettingsApi::class.java)
    }

    @Provides
    fun provideShiftsApi(retrofitInstance: Retrofit): ShiftsApi {
        return retrofitInstance.create(ShiftsApi::class.java)
    }

    @Provides
    fun provideUsersApi(retrofitInstance: Retrofit): UsersApi {
        return retrofitInstance.create(UsersApi::class.java)
    }
    // endregion

    // region Warehouse
    @Provides
    fun provideGatepassesApi(retrofitInstance: Retrofit): GatepassesApi {
        return retrofitInstance.create(GatepassesApi::class.java)
    }

    @Provides
    fun provideGatepassItemsApi(retrofitInstance: Retrofit): GatepassItemsApi {
        return retrofitInstance.create(GatepassItemsApi::class.java)
    }

    @Provides
    fun provideStockEntriesApi(retrofitInstance: Retrofit): StockEntriesApi {
        return retrofitInstance.create(StockEntriesApi::class.java)
    }

    @Provides
    fun provideWarehousesApi(retrofitInstance: Retrofit): WarehousesApi {
        return retrofitInstance.create(WarehousesApi::class.java)
    }
    // endregion

}