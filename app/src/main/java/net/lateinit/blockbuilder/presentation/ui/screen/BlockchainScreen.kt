package net.lateinit.blockbuilder.presentation.ui.screen

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.lateinit.blockbuilder.data.Block
import net.lateinit.blockbuilder.presentation.ui.theme.BlockBuilderTheme
import net.lateinit.blockbuilder.presentation.viewmodel.BlockchainViewModel

@Composable
fun BlockchainScreen(viewModel: BlockchainViewModel) {
    // UI 상태 변수들을 선언합니다.
    var dataInput by remember { mutableStateOf("") }
    val chain = viewModel.chainState.value
    val isChainValid = viewModel.validityState.value

    Column(modifier = Modifier.padding(16.dp)) {
        // 앱 제목
        Text("BlockBuilder", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        // 데이터 입력 및 블록 추가 UI
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = dataInput,
                onValueChange = { dataInput = it },
                label = { Text("블록 데이터") },
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = {
                if (dataInput.isNotBlank()) {
                    viewModel.addBlock(dataInput)
                    dataInput = "" // 입력 필드 초기화
                }
            }) {
                Text("블록 추가")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 체인 유효성 상태 표시
        Text(
            text = "체인 유효성: ${if (isChainValid) "유효함" else "손상됨!"}",
            color = if (isChainValid) Color(0xFF008000) else Color.Red,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 블록 리스트
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(chain.asReversed()) { block -> // 최신 블록이 위로 오도록 역순으로 표시
                BlockItem(
                    block = block,
                    onTamperClick = {
                        // 제네시스 블록(index 0)은 조작할 수 없도록 막음
                        if (block.index > 0) {
                            viewModel.tamperBlock(block.index)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun BlockItem(block: Block, onTamperClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("인덱스: ${block.index}", fontWeight = FontWeight.Bold)
            Text("타임스탬프: ${block.timestamp}")
            Text("데이터: ${block.data}")
            // 해시값은 너무 길기 때문에 앞부분만 잘라서 보여줌
            Text("이전 해시: ${block.previousHash.take(10)}...")
            Text("해시: ${block.hash.take(10)}...")

            // 제네시스 블록이 아닐 경우에만 조작 버튼 표시
            if (block.index > 0) {
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = onTamperClick,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("이 블록 데이터 조작하기 (실험)")
                }
            }
        }
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    BlockBuilderTheme {
        val dummyViewModel = BlockchainViewModel()
        BlockchainScreen(dummyViewModel)
    }
}