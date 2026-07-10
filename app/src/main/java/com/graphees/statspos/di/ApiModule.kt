package com.graphees.statspos.di

import com.graphees.statspos.data.repository.accounts.AccountCategoriesRepositoryImpl
import com.graphees.statspos.data.repository.accounts.AccountsRepositoryImpl
import com.graphees.statspos.data.repository.accounts.BanksRepositoryImpl
import com.graphees.statspos.data.repository.accounts.CustomersRepositoryImpl
import com.graphees.statspos.data.repository.accounts.EmployeesRepositoryImpl
import com.graphees.statspos.data.repository.accounts.ExpensesRepositoryImpl
import com.graphees.statspos.data.repository.accounts.FixedAccountsRepositoryImpl
import com.graphees.statspos.data.repository.accounts.SuppliersRepositoryImpl
import com.graphees.statspos.data.repository.accounts.VendorsRepositoryImpl
import com.graphees.statspos.data.repository.firebase.FirebaseRepositoryImpl
import com.graphees.statspos.data.repository.items.CategoriesRepositoryImpl
import com.graphees.statspos.data.repository.items.ItemsRepositoryImpl
import com.graphees.statspos.data.repository.items.LinkedItemsRepositoryImpl
import com.graphees.statspos.data.repository.items.PackagesRepositoryImpl
import com.graphees.statspos.data.repository.items.SubBarcodesRepositoryImpl
import com.graphees.statspos.data.repository.main.ClientsRepositoryImpl
import com.graphees.statspos.data.repository.main.MainRepositoryImpl
import com.graphees.statspos.data.repository.purchase.PurchaseItemsRepositoryImpl
import com.graphees.statspos.data.repository.purchase.PurchaseOrderItemsRepositoryImpl
import com.graphees.statspos.data.repository.purchase.PurchaseOrdersRepositoryImpl
import com.graphees.statspos.data.repository.purchase.PurchaseRepositoryImpl
import com.graphees.statspos.data.repository.purchase.RejectedItemsRepositoryImpl
import com.graphees.statspos.data.repository.reports.AccountReportsRepositoryImpl
import com.graphees.statspos.data.repository.reports.ItemsReportsRepositoryImpl
import com.graphees.statspos.data.repository.reports.ProfitReportsRepositoryImpl
import com.graphees.statspos.data.repository.reports.PurchaseReportsRepositoryImpl
import com.graphees.statspos.data.repository.reports.SalesReportsRepositoryImpl
import com.graphees.statspos.data.repository.reports.StockReportsRepositoryImpl
import com.graphees.statspos.data.repository.sales.SalesItemsRepositoryImpl
import com.graphees.statspos.data.repository.sales.SalesOrderItemsRepositoryImpl
import com.graphees.statspos.data.repository.sales.SalesRepositoryImpl
import com.graphees.statspos.data.repository.utilities.AuditRepositoryImpl
import com.graphees.statspos.data.repository.utilities.BarcodeLabelsRepositoryImpl
import com.graphees.statspos.data.repository.utilities.SettingsRepositoryImpl
import com.graphees.statspos.data.repository.utilities.ShiftsRepositoryImpl
import com.graphees.statspos.data.repository.utilities.UsersRepositoryImpl
import com.graphees.statspos.data.repository.warehouse.GatepassItemsRepositoryImpl
import com.graphees.statspos.data.repository.warehouse.GatepassesRepositoryImpl
import com.graphees.statspos.data.repository.warehouse.StockEntriesRepositoryImpl
import com.graphees.statspos.data.repository.warehouse.WarehousesRepositoryImpl
import com.graphees.statspos.domain.repository.accounts.AccountCategoriesRepository
import com.graphees.statspos.domain.repository.accounts.AccountsRepository
import com.graphees.statspos.domain.repository.accounts.BanksRepository
import com.graphees.statspos.domain.repository.accounts.CustomersRepository
import com.graphees.statspos.domain.repository.accounts.EmployeesRepository
import com.graphees.statspos.domain.repository.accounts.ExpensesRepository
import com.graphees.statspos.domain.repository.accounts.FixedAccountsRepository
import com.graphees.statspos.domain.repository.accounts.SuppliersRepository
import com.graphees.statspos.domain.repository.accounts.VendorsRepository
import com.graphees.statspos.domain.repository.firebase.FirebaseRepository
import com.graphees.statspos.domain.repository.items.CategoriesRepository
import com.graphees.statspos.domain.repository.items.ItemsRepository
import com.graphees.statspos.domain.repository.items.LinkedItemsRepository
import com.graphees.statspos.domain.repository.items.PackagesRepository
import com.graphees.statspos.domain.repository.items.SubBarcodesRepository
import com.graphees.statspos.domain.repository.main.ClientsRepository
import com.graphees.statspos.domain.repository.main.MainRepository
import com.graphees.statspos.domain.repository.purchase.PurchaseItemsRepository
import com.graphees.statspos.domain.repository.purchase.PurchaseOrderItemsRepository
import com.graphees.statspos.domain.repository.purchase.PurchaseOrdersRepository
import com.graphees.statspos.domain.repository.purchase.PurchaseRepository
import com.graphees.statspos.domain.repository.purchase.RejectedItemsRepository
import com.graphees.statspos.domain.repository.reports.AccountReportsRepository
import com.graphees.statspos.domain.repository.reports.ItemsReportsRepository
import com.graphees.statspos.domain.repository.reports.ProfitReportsRepository
import com.graphees.statspos.domain.repository.reports.PurchaseReportsRepository
import com.graphees.statspos.domain.repository.reports.SalesReportsRepository
import com.graphees.statspos.domain.repository.reports.StockReportsRepository
import com.graphees.statspos.domain.repository.sales.SalesItemsRepository
import com.graphees.statspos.domain.repository.sales.SalesRepository
import com.graphees.statspos.domain.repository.utilities.AuditRepository
import com.graphees.statspos.domain.repository.utilities.BarcodeLabelsRepository
import com.graphees.statspos.domain.repository.utilities.SettingsRepository
import com.graphees.statspos.domain.repository.utilities.ShiftsRepository
import com.graphees.statspos.domain.repository.utilities.UsersRepository
import com.graphees.statspos.domain.repository.warehouse.GatepassItemsRepository
import com.graphees.statspos.domain.repository.warehouse.GatepassesRepository
import com.graphees.statspos.domain.repository.warehouse.StockEntriesRepository
import com.graphees.statspos.domain.repository.warehouse.WarehousesRepository
import com.graphees.statspos.data.repository.sales.SalesOrdersRepositoryImpl
import com.graphees.statspos.domain.repository.sales.SalesOrderItemsRepository
import com.graphees.statspos.domain.repository.sales.SalesOrdersRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class ApiModule {

    // region Accounts
    @Binds
    abstract fun bindsAccountCategoriesRepositoryImpl(accountCategoriesRepositoryImpl: AccountCategoriesRepositoryImpl): AccountCategoriesRepository

    @Binds
    abstract fun bindsAccountsRepositoryImpl(accountsRepositoryImpl: AccountsRepositoryImpl): AccountsRepository

    @Binds
    abstract fun bindsBanksRepositoryImpl(banksRepositoryImpl: BanksRepositoryImpl): BanksRepository

    @Binds
    abstract fun bindsCustomersRepositoryImpl(customersRepositoryImpl: CustomersRepositoryImpl): CustomersRepository

    @Binds
    abstract fun bindsEmployeesRepositoryImpl(employeesRepositoryImpl: EmployeesRepositoryImpl): EmployeesRepository

    @Binds
    abstract fun bindsExpensesRepositoryImpl(expensesRepositoryImpl: ExpensesRepositoryImpl): ExpensesRepository

    @Binds
    abstract fun bindsFixedAccountsRepositoryImpl(fixedAccountsRepositoryImpl: FixedAccountsRepositoryImpl): FixedAccountsRepository

    @Binds
    abstract fun bindsSuppliersRepositoryImpl(suppliersRepositoryImpl: SuppliersRepositoryImpl): SuppliersRepository

    @Binds
    abstract fun bindsVendorsRepositoryImpl(vendorsRepositoryImpl: VendorsRepositoryImpl): VendorsRepository

    // endregion

    // region Items
    @Binds
    abstract fun bindsCategoriesRepositoryImpl(categoriesRepositoryImpl: CategoriesRepositoryImpl): CategoriesRepository

    @Binds
    abstract fun bindsItemsRepositoryImpl(itemsRepositoryImpl: ItemsRepositoryImpl): ItemsRepository

    @Binds
    abstract fun bindsLinkedItemsRepositoryImpl(linkedItemsRepositoryImpl: LinkedItemsRepositoryImpl): LinkedItemsRepository

    @Binds
    abstract fun bindsPackagesRepositoryImpl(packagesRepositoryImpl: PackagesRepositoryImpl): PackagesRepository

    @Binds
    abstract fun bindsSubBarcodesRepositoryImpl(subBarcodesRepositoryImpl: SubBarcodesRepositoryImpl): SubBarcodesRepository
    // endregion

    // region Main
    @Binds
    abstract fun bindsClientsRepo(clientsRepoImpl: ClientsRepositoryImpl): ClientsRepository

    @Binds
    abstract fun bindsMainRepositoryImpl(mainRepositoryImpl: MainRepositoryImpl): MainRepository
    // endregion

    // region Purchase
    @Binds
    abstract fun bindsPurchaseItemsRepositoryImpl(purchaseItemsRepositoryImpl: PurchaseItemsRepositoryImpl): PurchaseItemsRepository

    @Binds
    abstract fun bindsPurchaseOrderItemsRepositoryImpl(purchaseOrderItemsRepositoryImpl: PurchaseOrderItemsRepositoryImpl): PurchaseOrderItemsRepository

    @Binds
    abstract fun bindsPurchaseOrdersRepositoryImpl(purchaseOrdersRepositoryImpl: PurchaseOrdersRepositoryImpl): PurchaseOrdersRepository

    @Binds
    abstract fun bindsPurchaseRepositoryImpl(purchaseRepositoryImpl: PurchaseRepositoryImpl): PurchaseRepository

    @Binds
    abstract fun bindsRejectedItemsRepositoryImpl(rejectedItemsRepositoryImpl: RejectedItemsRepositoryImpl): RejectedItemsRepository
    // endregion

    // region Reports
    @Binds
    abstract fun bindsAccountReportsRepositoryImpl(accountReportsRepositoryImpl: AccountReportsRepositoryImpl): AccountReportsRepository

    @Binds
    abstract fun bindsItemsReportsRepositoryImpl(itemsReportsRepositoryImpl: ItemsReportsRepositoryImpl): ItemsReportsRepository

    @Binds
    abstract fun bindsProfitReportsRepositoryImpl(profitReportsRepositoryImpl: ProfitReportsRepositoryImpl): ProfitReportsRepository

    @Binds
    abstract fun bindsPurchaseReportsRepositoryImpl(purchaseReportsRepositoryImpl: PurchaseReportsRepositoryImpl): PurchaseReportsRepository

    @Binds
    abstract fun bindsSalesReportsRepositoryImpl(salesReportsRepositoryImpl: SalesReportsRepositoryImpl): SalesReportsRepository

    @Binds
    abstract fun bindsStockReportsRepositoryImpl(stockReportsRepositoryImpl: StockReportsRepositoryImpl): StockReportsRepository
    // endregion

    // region Sales
    @Binds
    abstract fun bindsSalesItemsRepositoryImpl(salesItemsRepositoryImpl: SalesItemsRepositoryImpl): SalesItemsRepository

    @Binds
    abstract fun bindsSalesRepositoryImpl(salesRepositoryImpl: SalesRepositoryImpl): SalesRepository

    @Binds
    abstract fun bindsSalesOrdersRepo(salesOrdersRepositoryImpl: SalesOrdersRepositoryImpl): SalesOrdersRepository

    @Binds
    abstract fun bindsSalesOrderItemsRepo(salesOrderItemsRepositoryImpl: SalesOrderItemsRepositoryImpl): SalesOrderItemsRepository
    // endregion

    // region Utilities
    @Binds
    abstract fun bindsAuditRepositoryImpl(auditRepositoryImpl: AuditRepositoryImpl): AuditRepository

    @Binds
    abstract fun bindsBarcodeLabelsRepositoryImpl(barcodeLabelsRepositoryImpl: BarcodeLabelsRepositoryImpl): BarcodeLabelsRepository

    @Binds
    abstract fun bindsSettingsRepositoryImpl(settingsRepositoryImpl: SettingsRepositoryImpl): SettingsRepository

    @Binds
    abstract fun bindsShiftsRepositoryImpl(shiftsRepositoryImpl: ShiftsRepositoryImpl): ShiftsRepository

    @Binds
    abstract fun bindsUsersRepositoryImpl(usersRepositoryImpl: UsersRepositoryImpl): UsersRepository
    // endregion

    // region Warehouse
    @Binds
    abstract fun bindsGatepassesRepositoryImpl(gatepassesRepositoryImpl: GatepassesRepositoryImpl): GatepassesRepository

    @Binds
    abstract fun bindsGatepassItemsRepositoryImpl(gatepassItemsRepositoryImpl: GatepassItemsRepositoryImpl): GatepassItemsRepository

    @Binds
    abstract fun bindsStockEntriesRepositoryImpl(stockEntriesRepositoryImpl: StockEntriesRepositoryImpl): StockEntriesRepository

    @Binds
    abstract fun bindsWarehousesRepositoryImpl(warehousesRepositoryImpl: WarehousesRepositoryImpl): WarehousesRepository
    // endregion

    // region Firebase
    @Binds
    abstract fun bindsFirebaseRepo(firebaseRepositoryImpl: FirebaseRepositoryImpl): FirebaseRepository
    // endregion

}