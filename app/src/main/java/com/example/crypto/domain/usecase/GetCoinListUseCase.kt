package com.example.crypto.domain.usecase



import com.example.crypto.domain.model.Coin
import com.example.crypto.domain.repository.CoinRepository
import javax.inject.Inject

class GetCoinListUseCase @Inject constructor(
    private val repository: CoinRepository
) {
    suspend operator fun invoke(): Result<List<Coin>> {
        return repository.getCoinList()
    }
}