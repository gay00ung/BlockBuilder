package net.lateinit.blockbuilder.data

import java.security.MessageDigest
import java.util.Date

/**
 * 블록체인의 기본 단위인 블록을 나타내는 데이터 클래스입니다.
 */
data class Block(
    val index: Int,
    val timestamp: Long = System.currentTimeMillis(),
    val data: String,
    val previousHash: String,
    var hash: String = "" // 생성 시점에 해시가 계산되므로 var로 선언
) {
    init {
        // 객체 생성 시 자신의 해시값을 계산하여 할당합니다.
        hash = calculateHash()
    }

    /**
     * 블록의 내용을 기반으로 SHA-256 해시를 계산합니다.
     */
    fun calculateHash(): String {
        val input = index.toString() + timestamp + data + previousHash
        val digest = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
        return digest.fold("") { str, it -> str + "%02x".format(it) }
    }
}