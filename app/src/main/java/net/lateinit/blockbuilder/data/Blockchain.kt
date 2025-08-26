package net.lateinit.blockbuilder.data

/**
 * 블록들을 체인 형태로 관리하는 클래스입니다.
 */
class Blockchain {
    val chain = mutableListOf<Block>()

    init {
        // 블록체인이 생성될 때 첫 번째 블록인 제네시스 블록을 추가합니다.
        createGenesisBlock()
    }

    /**
     * 체인의 시작점인 제네시스 블록을 생성합니다.
     */
    private fun createGenesisBlock() {
        val genesisBlock = Block(0, data = "제네시스 블록", previousHash = "0")
        chain.add(genesisBlock)
    }

    /**
     * 새로운 데이터를 받아 새 블록을 체인에 추가합니다.
     */
    fun addBlock(data: String) {
        val lastBlock = chain.last()
        val newBlock = Block(
            index = lastBlock.index + 1,
            data = data,
            previousHash = lastBlock.hash
        )
        chain.add(newBlock)
    }

    /**
     * 블록체인의 무결성을 검증합니다.
     */
    fun isChainValid(): Boolean {
        for (i in 1 until chain.size) {
            val currentBlock = chain[i]
            val previousBlock = chain[i - 1]

            // 현재 블록의 해시값이 데이터와 일치하는지 재계산하여 확인
            if (currentBlock.hash != currentBlock.calculateHash()) {
                return false
            }
            // 현재 블록이 가리키는 이전 해시값이 실제 이전 블록의 해시값과 일치하는지 확인
            if (currentBlock.previousHash != previousBlock.hash) {
                return false
            }
        }
        return true
    }

    /**
     * 특정 블록의 데이터를 임의로 변경합니다. (실험용)
     * @param index 조작할 블록의 인덱스
     * @param newData 새로운 데이터
     */
    fun tamperBlock(index: Int, newData: String) {
        if (index > 0 && index < chain.size) { // 제네시스 블록(index 0)은 변경하지 않음
            val originalBlock = chain[index]
            // 데이터만 변경하고 나머지 속성은 그대로 유지하여 새 블록 객체를 생성
            chain[index] = originalBlock.copy(data = newData)
        }
    }
}