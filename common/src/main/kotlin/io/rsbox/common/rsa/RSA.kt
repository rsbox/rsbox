package io.rsbox.common.rsa

import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.util.io.pem.PemObject
import org.bouncycastle.util.io.pem.PemReader
import org.bouncycastle.util.io.pem.PemWriter
import org.tinylog.kotlin.Logger
import java.io.File
import java.math.BigInteger
import java.nio.file.Files
import java.nio.file.Path
import java.security.KeyFactory
import java.security.KeyPairGenerator
import java.security.Security
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.security.spec.PKCS8EncodedKeySpec

class RSA(private val dir: File) {

    lateinit var exponent: BigInteger

    lateinit var modulus: BigInteger

    private lateinit var privateKey: RSAPrivateKey

    private val publicKeyPath = dir.toPath().resolve("public.key")
    private val privateKeyPath= dir.toPath().resolve("private.key")

    fun init() {
        val path = dir.toPath()
        val privateKeyPath = path.resolve("private.key")
        val publicKeyPath = path.resolve("public.key")
        val modulusPath = path.resolve("modulus.txt")

        if(listOf(privateKeyPath, publicKeyPath, modulusPath).any { !Files.exists(it) }) {
            this.generateKeyPair(publicKeyPath, privateKeyPath, modulusPath)
        }

        this.load()
    }

    private fun load() {
        if(!Files.exists(publicKeyPath) || !Files.exists(privateKeyPath)) {
            throw IllegalStateException("Unable to locate public or private key files in path: '${dir.path}'.")
        }

        PemReader(Files.newBufferedReader(privateKeyPath)).use { reader ->
            val pem = reader.readPemObject()
            val keySpec = PKCS8EncodedKeySpec(pem.content)

            Security.addProvider(BouncyCastleProvider())
            val factory = KeyFactory.getInstance("RSA", "BC")

            /*
             * Load the private key
             */
            privateKey = factory.generatePrivate(keySpec) as RSAPrivateKey

            exponent = privateKey.privateExponent
            modulus = privateKey.modulus
        }
    }

    private fun generateKeyPair(publicKeyPath: Path, privateKeyPath: Path, modulusPath: Path) {
        Logger.info("Generating new RSA key-pair...")

        listOf(publicKeyPath, privateKeyPath, modulusPath).forEach { path ->
            Files.deleteIfExists(path)
        }

        Security.addProvider(BouncyCastleProvider())

        val generator = KeyPairGenerator.getInstance("RSA", "BC")
        generator.initialize(KEY_BITS)

        val keypair = generator.generateKeyPair()
        val privateKey = keypair.private as RSAPrivateKey
        val publicKey = keypair.public as RSAPublicKey

        Logger.info("Successfully generated private and public RSA keys. Information printed below.")

        println("----------------------------------------------------------")
        println("Exponent: ${publicKey.publicExponent.toString(RADIX)}")
        println("Modulus: ${publicKey.modulus.toString(RADIX)}")
        println("----------------------------------------------------------")

        /*
         * Write the Private key to file
         */
        Logger.info("Writing RSA private key to file: '$privateKeyPath'.")
        PemWriter(Files.newBufferedWriter(privateKeyPath)).use { writer ->
            writer.writeObject(PemObject("RSA PRIVATE KEY", privateKey.encoded))
        }

        /*
         * Write the public key to file
         */
        Logger.info("Writing RSA public key to file: '$publicKeyPath'.")
        PemWriter(Files.newBufferedWriter(publicKeyPath)).use { writer ->
            writer.writeObject(PemObject("RSA PUBLIC KEY", publicKey.encoded))
        }

        /*
         * Write the modulus to file
         */
        Logger.info("Writing RSA public modulus to file: '$modulusPath'.")
        Files.newBufferedWriter(modulusPath).use { writer ->
            writer.write(publicKey.modulus.toString(RADIX))
        }

        Logger.info("Successfully generated and saved new RSA key-pair.")
    }

    companion object {
        private const val KEY_BITS = 2048
        private const val RADIX = 16
    }
}