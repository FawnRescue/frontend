package core.utils

import android.os.Build
import androidx.annotation.RequiresApi
import java.security.SecureRandom
import java.util.Base64
import javax.crypto.KeyGenerator

@RequiresApi(Build.VERSION_CODES.O)
actual fun randomAESKey(): String {
    val keyGenerator = KeyGenerator.getInstance("AES")
    keyGenerator.init(128, SecureRandom())
    val secretKey = keyGenerator.generateKey()
    return Base64.getEncoder().encodeToString(secretKey.encoded)
}