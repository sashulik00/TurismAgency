package com.example.travelagency.data.repository

import com.example.travelagency.data.dao.ContractListItem
import com.example.travelagency.data.dao.ContractsDao
import kotlinx.coroutines.flow.Flow

class ContractsRepository(private val dao: ContractsDao) {
    fun observeContracts(): Flow<List<ContractListItem>> = dao.observeAll()

    fun observeContractDetails(contractId: Long): Flow<ContractListItem?> = dao.observeDetails(contractId)
}
