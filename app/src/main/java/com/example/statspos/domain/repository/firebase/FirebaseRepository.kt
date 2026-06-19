package com.example.statspos.domain.repository.firebase

import com.example.statspos.domain.models.sales.SalesOrders
import com.example.statspos.utils.Resource

interface FirebaseRepository {
    suspend fun loadOrders(): Resource<List<SalesOrders>>
}