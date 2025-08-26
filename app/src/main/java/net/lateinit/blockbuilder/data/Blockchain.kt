package net.lateinit.blockbuilder.data

class Blockchain {
    val chain = mutableListOf<Block>()
    // 'Mempool' 역할을 하는, 아직 블록에 포함되지 않은 거래 목록
    var pendingTransactions = mutableListOf<Transaction>()

    // 채굴 난이도와 보상 설정
    var difficulty = 2 // 해시가 '00'으로 시작해야 함
    private val miningReward = 100 // 채굴 성공 시 보상

    init {
        createGenesisBlock()
    }

    private fun createGenesisBlock() {
        val genesisBlock = Block(0, transactions = emptyList(), previousHash = "0")
        genesisBlock.hash = genesisBlock.calculateHash()
        chain.add(genesisBlock)
    }

    /**
     * 새로운 거래를 생성하여 대기열(pendingTransactions)에 추가합니다.
     */
    fun createTransaction(transaction: Transaction) {
        pendingTransactions.add(transaction)
    }

    /**
     * 대기 중인 모든 거래를 모아 새로운 블록을 채굴합니다.
     * @param minerAddress 채굴 보상을 받을 사람의 지갑 주소.
     */
    fun minePendingTransactions(minerAddress: String) {
        // 대기 중인 거래들을 담아 새 블록 생성
        val newBlock = Block(
            index = chain.size,
            transactions = pendingTransactions.toList(), // 현재 대기중인 모든 거래를 포함
            previousHash = chain.last().hash
        )

        // 어려운 수학 문제 풀기 (작업증명)
        newBlock.mineBlock(difficulty)

        // 체인에 새 블록 추가
        chain.add(newBlock)

        // 거래 대기열을 비우고, 채굴 보상 거래를 새로 추가
        pendingTransactions = mutableListOf(
            Transaction("System", minerAddress, miningReward)
        )
    }

    /**
     * 특정 지갑 주소의 잔액을 계산합니다.
     * 체인의 모든 블록을 순회하며 입출금 내역을 모두 더하고 뺍니다.
     * @param address 잔액을 조회할 지갑 주소.
     * @return 해당 지갑의 최종 잔액.
     */
    fun getBalanceOfAddress(address: String): Int {
        var balance = 0
        for (block in chain) {
            for (trans in block.transactions) {
                if (trans.fromAddress == address) {
                    balance -= trans.amount
                }
                if (trans.toAddress == address) {
                    balance += trans.amount
                }
            }
        }
        return balance
    }
}