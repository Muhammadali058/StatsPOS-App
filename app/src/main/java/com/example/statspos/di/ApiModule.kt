package com.example.statspos.di

import com.example.statspos.data.repository.accounts.AccountCategoriesRepositoryImpl
import com.example.statspos.data.repository.accounts.AccountsRepositoryImpl
import com.example.statspos.data.repository.accounts.BanksRepositoryImpl
import com.example.statspos.data.repository.accounts.CustomersRepositoryImpl
import com.example.statspos.data.repository.accounts.EmployeesRepositoryImpl
import com.example.statspos.data.repository.accounts.ExpensesRepositoryImpl
import com.example.statspos.data.repository.accounts.FixedAccountsRepositoryImpl
import com.example.statspos.data.repository.accounts.SuppliersRepositoryImpl
import com.example.statspos.data.repository.accounts.VendorsRepositoryImpl
import com.example.statspos.data.repository.items.CategoriesRepositoryImpl
import com.example.statspos.data.repository.items.ItemsRepositoryImpl
import com.example.statspos.data.repository.items.LinkedItemsRepositoryImpl
import com.example.statspos.data.repository.items.PackagesRepositoryImpl
import com.example.statspos.data.repository.items.SubBarcodesRepositoryImpl
import com.example.statspos.data.repository.main.ClientsRepositoryImpl
import com.example.statspos.data.repository.main.MainRepositoryImpl
import com.example.statspos.data.repository.purchase.PurchaseItemsRepositoryImpl
import com.example.statspos.data.repository.purchase.PurchaseOrderItemsRepositoryImpl
import com.example.statspos.data.repository.purchase.PurchaseOrdersRepositoryImpl
import com.example.statspos.data.repository.purchase.PurchaseRepositoryImpl
import com.example.statspos.data.repository.purchase.RejectedItemsRepositoryImpl
import com.example.statspos.data.repository.reports.AccountReportsRepositoryImpl
import com.example.statspos.data.repository.reports.ItemsReportsRepositoryImpl
import com.example.statspos.data.repository.reports.ProfitReportsRepositoryImpl
import com.example.statspos.data.repository.reports.PurchaseReportsRepositoryImpl
import com.example.statspos.data.repository.reports.SalesReportsRepositoryImpl
import com.example.statspos.data.repository.reports.StockReportsRepositoryImpl
import com.example.statspos.data.repository.sales.SalesItemsRepositoryImpl
import com.example.statspos.data.repository.sales.SalesRepositoryImpl
import com.example.statspos.data.repository.utilities.AuditRepositoryImpl
import com.example.statspos.data.repository.utilities.BarcodeLabelsRepositoryImpl
import com.example.statspos.data.repository.utilities.PrintSettingsRepositoryImpl
import com.example.statspos.data.repository.utilities.SettingsRepositoryImpl
import com.example.statspos.data.repository.utilities.ShiftsRepositoryImpl
import com.example.statspos.data.repository.utilities.UsersRepositoryImpl
import com.example.statspos.data.repository.warehouse.GatepassItemsRepositoryImpl
import com.example.statspos.data.repository.warehouse.GatepassesRepositoryImpl
import com.example.statspos.data.repository.warehouse.StockEntriesRepositoryImpl
import com.example.statspos.data.repository.warehouse.WarehousesRepositoryImpl
import com.example.statspos.domain.repository.accounts.AccountCategoriesRepository
import com.example.statspos.domain.repository.accounts.AccountsRepository
import com.example.statspos.domain.repository.accounts.BanksRepository
import com.example.statspos.domain.repository.accounts.CustomersRepository
import com.example.statspos.domain.repository.accounts.EmployeesRepository
import com.example.statspos.domain.repository.accounts.ExpensesRepository
import com.example.statspos.domain.repository.accounts.FixedAccountsRepository
import com.example.statspos.domain.repository.accounts.SuppliersRepository
import com.example.statspos.domain.repository.accounts.VendorsRepository
import com.example.statspos.domain.repository.items.CategoriesRepository
import com.example.statspos.domain.repository.items.ItemsRepository
import com.example.statspos.domain.repository.items.LinkedItemsRepository
import com.example.statspos.domain.repository.items.PackagesRepository
import com.example.statspos.domain.repository.items.SubBarcodesRepository
import com.example.statspos.domain.repository.main.ClientsRepository
import com.example.statspos.domain.repository.main.MainRepository
import com.example.statspos.domain.repository.purchase.PurchaseItemsRepository
import com.example.statspos.domain.repository.purchase.PurchaseOrderItemsRepository
import com.example.statspos.domain.repository.purchase.PurchaseOrdersRepository
import com.example.statspos.domain.repository.purchase.PurchaseRepository
import com.example.statspos.domain.repository.purchase.RejectedItemsRepository
import com.example.statspos.domain.repository.reports.AccountReportsRepository
import com.example.statspos.domain.repository.reports.ItemsReportsRepository
import com.example.statspos.domain.repository.reports.ProfitReportsRepository
import com.example.statspos.domain.repository.reports.PurchaseReportsRepository
import com.example.statspos.domain.repository.reports.SalesReportsRepository
import com.example.statspos.domain.repository.reports.StockReportsRepository
import com.example.statspos.domain.repository.sales.SalesItemsRepository
import com.example.statspos.domain.repository.sales.SalesRepository
import com.example.statspos.domain.repository.utilities.AuditRepository
import com.example.statspos.domain.repository.utilities.BarcodeLabelsRepository
import com.example.statspos.domain.repository.utilities.PrintSettingsRepository
import com.example.statspos.domain.repository.utilities.SettingsRepository
import com.example.statspos.domain.repository.utilities.ShiftsRepository
import com.example.statspos.domain.repository.utilities.UsersRepository
import com.example.statspos.domain.repository.warehouse.GatepassItemsRepository
import com.example.statspos.domain.repository.warehouse.GatepassesRepository
import com.example.statspos.domain.repository.warehouse.StockEntriesRepository
import com.example.statspos.domain.repository.warehouse.WarehousesRepository
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
    // endregion

    // region Utilities
    @Binds
    abstract fun bindsAuditRepositoryImpl(auditRepositoryImpl: AuditRepositoryImpl): AuditRepository

    @Binds
    abstract fun bindsBarcodeLabelsRepositoryImpl(barcodeLabelsRepositoryImpl: BarcodeLabelsRepositoryImpl): BarcodeLabelsRepository

    @Binds
    abstract fun bindsSettingsRepositoryImpl(settingsRepositoryImpl: SettingsRepositoryImpl): SettingsRepository

    @Binds
    abstract fun bindsPrintSettingsRepositoryImpl(printSettingsRepositoryImpl: PrintSettingsRepositoryImpl): PrintSettingsRepository

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

}