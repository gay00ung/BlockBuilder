package net.lateinit.blockbuilder.data

import java.util.UUID

/**
 * 암호화폐 지갑을 나타냅니다.
 * @property address 지갑의 고유 주소 (공개키 역할)
 * @property privateKey 거래에 서명하는 데 사용되는 비밀 키 (여기서는 시뮬레이션용)
 */
data class Wallet(
    val address: String = UUID.randomUUID().toString().replace("-", ""),
    val privateKey: String = UUID.randomUUID().toString().replace("-", "")
)
