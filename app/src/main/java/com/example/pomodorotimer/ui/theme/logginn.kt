package com.example.pomodorotimer.ui.theme

//import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import com.example.pomodorotimer.R

@Composable
fun Logginn ( Logg_inn: () -> Unit) {
    var password by remember { mutableStateOf ("Password") }
    Column (horizontalAlignment = Alignment.CenterHorizontally){

       // Image(painter = painterResource(id = R.drawable.konto), contentDescription = "konto")
        OutlinedTextField(value = "E-mail", onValueChange = {})
        OutlinedTextField(value = password, onValueChange = { password = it},
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )
        Button(onClick = {Logg_inn() }) {
            Text(text = stringResource(R.string.logg_inn))
        }
    }

}

@Preview
@Composable
fun LogginnP(){
    Logginn(Logg_inn = {})
}
