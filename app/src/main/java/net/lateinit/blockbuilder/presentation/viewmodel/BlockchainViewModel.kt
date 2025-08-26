package net.lateinit.blockbuilder.presentation.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import net.lateinit.blockbuilder.data.Block
import net.lateinit.blockbuilder.data.Blockchain

class BlockchainViewModel : ViewModel() {
    // 블록체인 인스턴스를 생성합니다.
    private val blockchain = Blockchain()

    // Compose UI에서 관찰할 수 있는 상태들을 정의합니다.
    val chainState = mutableStateOf<List<Block>>(emptyList())
    val validityState = mutableStateOf(true)

    init {
        // ViewModel이 생성될 때 초기 상태를 UI에 반영합니다.
        updateChainState()
    }

    /**
     * 새로운 데이터를 받아 블록을 추가하고 UI 상태를 갱신합니다.
     */
    fun addBlock(data: String) {
        blockchain.addBlock(data)
        updateChainState()
    }

    /**
     * 특정 블록의 데이터를 조작하고 UI 상태를 갱신합니다. (실험용)
     * @param index 조작할 블록의 인덱스
     */
    fun tamperBlock(index: Int) {
        val tamperedData = "데이터 조작됨! ${System.currentTimeMillis()}"
        blockchain.tamperBlock(index, tamperedData)
        updateChainState() // 유효성 검사를 다시 하고 UI를 갱신
    }

    /**
     * 블록체인의 현재 상태(블록 리스트, 유효성)를 가져와 UI 상태 변수를 업데이트합니다.
     */
    private fun updateChainState() {
        chainState.value = blockchain.chain.toList() // 불변 리스트로 변환하여 UI에 전달
        validityState.value = blockchain.isChainValid()
    }
}