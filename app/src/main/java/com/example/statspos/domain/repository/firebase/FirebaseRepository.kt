package com.example.statspos.domain.repository.firebase

import com.example.statspos.domain.models.sales.SalesOrders
import com.example.statspos.utils.Resource
import kotlinx.coroutines.flow.Flow

interface FirebaseRepository {
    suspend fun loadOrders(status:String, date:String): Resource<List<SalesOrders>>
    fun loadOrdersRealtime(status:String, date:String): Flow<Resource<List<SalesOrders>>>
    suspend fun updateStatus(id:Long, status:String): Resource<String>
}