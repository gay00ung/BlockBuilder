package net.lateinit.blockbuilder.presentation.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.lateinit.blockbuilder.data.Blockchain
import net.lateinit.blockbuilder.data.Transaction
import net.lateinit.blockbuilder.data.Wallet
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay

class BlockchainViewModel : ViewModel() {
    private val blockchain = Blockchain()

    // UI 상태
    val wallets = mutableStateListOf<Wallet>()
    val chainState = mutableStateOf(blockchain.chain.toList())
    val pendingTransactionsState = mutableStateOf(blockchain.pendingTransactions.toList())
    val miningInProgress = mutableStateOf(false)
    val balances = mutableStateOf<Map<String, Int>>(emptyMap())
    val errorMessage = mutableStateOf<String?>(null) // 에러 메시지 상태 추가
    val isAutoMining = mutableStateOf(false)
    private var autoMiningJob: Job? = null

    init {
        // 앱 시작 시 기본 지갑 2개 생성
        createWallet()
        createWallet()
        // 각 지갑에 초기 코인 분배
        distributeInitialCoins()
    }

    private fun distributeInitialCoins() {
        if (wallets.isNotEmpty()) {
            wallets.forEach { wallet ->
                val amount = (100..500).random()
                blockchain.createTransaction(Transaction("System", wallet.address, amount))
            }
            // 첫 번째 지갑을 채굴자로 하여 초기 코인 지급 거래를 채굴
            blockchain.minePendingTransactions(wallets.first().address)

            // UI 상태 업데이트
            chainState.value = blockchain.chain.toList()
            pendingTransactionsState.value = blockchain.pendingTransactions.toList()
            updateBalances()
        }
    }

    fun createWallet() {
        errorMessage.value = null // 에러 메시지 초기화
        wallets.add(Wallet())
        updateBalances()
    }

    fun createTransaction(from: String, to: String, amount: Int) {
        errorMessage.value = null // 이전 에러 메시지 초기화
        val fromBalance = balances.value[from] ?: 0
        if (fromBalance >= amount) {
            val transaction = Transaction(from, to, amount)
            blockchain.createTransaction(transaction)
            pendingTransactionsState.value = blockchain.pendingTransactions.toList()
        } else {
            // 잔액 부족 시 에러 메시지 설정
            errorMessage.value = "잔액이 부족합니다! 먼저 채굴을 통해 보상을 받아야 합니다."
        }
    }

    fun mineBlock() {
        errorMessage.value = null // 에러 메시지 초기화
        if (wallets.isNotEmpty()) {
            viewModelScope.launch {
                miningInProgress.value = true
                // 채굴은 계산량이 많은 작업이므로 IO 스레드에서 수행
                withContext(Dispatchers.IO) {
                    blockchain.minePendingTransactions(wallets.first().address) // 첫 번째 지갑을 채굴자로 지정
                }
                // UI 업데이트는 메인 스레드에서 수행
                chainState.value = blockchain.chain.toList()
                pendingTransactionsState.value = blockchain.pendingTransactions.toList()
                updateBalances()
                miningInProgress.value = false
            }
        }
    }

    fun toggleAutoMining() {
        isAutoMining.value = !isAutoMining.value
        if (isAutoMining.value) {
            startAutoMining()
        } else {
            stopAutoMining()
        }
    }

    private fun startAutoMining() {
        autoMiningJob = viewModelScope.launch {
            while (isAutoMining.value) {
                if (pendingTransactionsState.value.isNotEmpty()) {
                    mineBlock()
                }
                delay(3000) // 3초 대기
            }
        }
    }

    private fun stopAutoMining() {
        autoMiningJob?.cancel()
        autoMiningJob = null
        miningInProgress.value = false // 자동 채굴 중단 시 채굴 진행 상태 초기화
    }

    private fun updateBalances() {
        val newBalances = wallets.associate { wallet ->
            wallet.address to blockchain.getBalanceOfAddress(wallet.address)
        }
        balances.value = newBalances
    }
}