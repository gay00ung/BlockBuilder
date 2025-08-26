package net.lateinit.blockbuilder.presentation.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.lateinit.blockbuilder.data.Block
import net.lateinit.blockbuilder.data.Transaction
import net.lateinit.blockbuilder.data.Wallet
import net.lateinit.blockbuilder.presentation.viewmodel.BlockchainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlockBuilderScreen(viewModel: BlockchainViewModel) {
    val wallets = viewModel.wallets
    val balances = viewModel.balances.value
    val pendingTransactions = viewModel.pendingTransactionsState.value
    val chain = viewModel.chainState.value
    val miningInProgress = viewModel.miningInProgress.value
    val errorMessage = viewModel.errorMessage.value // 에러 메시지 상태 가져오기

    Scaffold {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(horizontal = 16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                SectionTitle("💰 지갑 목록")
                wallets.forEach { wallet ->
                    WalletItem(wallet, balances[wallet.address] ?: 0)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { viewModel.createWallet() }) {
                    Text("새 지갑 생성")
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                SectionTitle("💸 거래 생성")
                TransactionCreator(wallets = wallets, onTransactionCreate = { from, to, amount ->
                    viewModel.createTransaction(from, to, amount)
                })
                // 에러 메시지가 있을 경우 화면에 표시
                errorMessage?.let {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                SectionTitle("⛏️ 채굴")
                Text("대기 중인 거래: ${pendingTransactions.size}개")
                pendingTransactions.forEach { TransactionItem(it) }
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { viewModel.mineBlock() }, enabled = !miningInProgress && pendingTransactions.isNotEmpty()) {
                    if (miningInProgress) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("채굴 중...")
                    } else {
                        Text("새 블록 채굴 시작!")
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                SectionTitle("🔗 블록체인")
            }
            items(chain.asReversed()) { block ->
                BlockItem(block)
            }
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(title, fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))
}

@Composable
fun WalletItem(wallet: Wallet, balance: Int) {
    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text("주소: ${wallet.address.take(10)}...", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            Text("잔액: $balance 코인", fontSize = 16.sp, color = MaterialTheme.colorScheme.primary)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionCreator(wallets: List<Wallet>, onTransactionCreate: (String, String, Int) -> Unit) {
    var fromWallet by remember { mutableStateOf(wallets.firstOrNull()) }
    var toWallet by remember { mutableStateOf(wallets.getOrNull(1)) }
    var amount by remember { mutableStateOf("") }
    var expandedFrom by remember { mutableStateOf(false) }
    var expandedTo by remember { mutableStateOf(false) }

    Column {
        // 보내는 지갑 선택
        ExposedDropdownMenuBox(expanded = expandedFrom, onExpandedChange = { expandedFrom = !expandedFrom }) {
            OutlinedTextField(
                value = fromWallet?.address?.take(10) ?: "선택",
                onValueChange = {},
                readOnly = true,
                label = { Text("보내는 사람") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedFrom) },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(expanded = expandedFrom, onDismissRequest = { expandedFrom = false }) {
                wallets.forEach { wallet ->
                    DropdownMenuItem(text = { Text(wallet.address.take(10)) }, onClick = {
                        fromWallet = wallet
                        expandedFrom = false
                    })
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        // 받는 지갑 선택
        ExposedDropdownMenuBox(expanded = expandedTo, onExpandedChange = { expandedTo = !expandedTo }) {
            OutlinedTextField(
                value = toWallet?.address?.take(10) ?: "선택",
                onValueChange = {},
                readOnly = true,
                label = { Text("받는 사람") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedTo) },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(expanded = expandedTo, onDismissRequest = { expandedTo = false }) {
                wallets.forEach { wallet ->
                    DropdownMenuItem(text = { Text(wallet.address.take(10)) }, onClick = {
                        toWallet = wallet
                        expandedTo = false
                    })
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        // 금액 입력
        OutlinedTextField(
            value = amount,
            onValueChange = { amount = it.filter { char -> char.isDigit() } },
            label = { Text("금액") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        Button(onClick = {
            val from = fromWallet
            val to = toWallet
            val amt = amount.toIntOrNull()
            if (from != null && to != null && amt != null && from != to) {
                onTransactionCreate(from.address, to.address, amt)
                amount = ""
            }
        }, enabled = wallets.size >= 2) {
            Text("거래 생성")
        }
    }
}


@Composable
fun TransactionItem(transaction: Transaction) {
    Row(modifier = Modifier.padding(vertical = 2.dp)) {
        Text("💰 ${transaction.fromAddress.take(6)} -> ${transaction.toAddress.take(6)} : ${transaction.amount} 코인")
    }
}

@Composable
fun BlockItem(block: Block) {
    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("블록 #${block.index}", fontWeight = FontWeight.Bold)
            Text("해시: ${block.hash.take(15)}...")
            Text("이전 해시: ${block.previousHash.take(15)}...")
            Text("Nonce: ${block.nonce}")
            if (block.transactions.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text("포함된 거래:", fontWeight = FontWeight.SemiBold)
                block.transactions.forEach { TransactionItem(it) }
            }
        }
    }
}