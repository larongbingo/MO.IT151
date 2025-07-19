package noobnoob.mmdc

import aws.sdk.kotlin.runtime.auth.credentials.StaticCredentialsProvider
import aws.sdk.kotlin.services.s3.S3Client
import aws.sdk.kotlin.services.s3.model.GetObjectRequest
import aws.sdk.kotlin.services.s3.model.HeadObjectRequest
import aws.sdk.kotlin.services.s3.model.PutObjectRequest
import aws.sdk.kotlin.services.s3.presigners.presignGetObject
import aws.sdk.kotlin.services.s3.presigners.presignPutObject
import aws.smithy.kotlin.runtime.auth.awscredentials.Credentials
import aws.smithy.kotlin.runtime.net.url.Url
import kotlin.time.Duration.Companion.hours

private val client = S3Client {
    region = "auto"
    endpointUrl = Url.parse(System.getenv("MOIT151_S3_SERVICE_URL"))
    credentialsProvider = StaticCredentialsProvider(Credentials(
        System.getenv("MOIT151_S3_ACCESS_KEY"),
        System.getenv("MOIT151_S3_SECRET_KEY"))
    )
}

private val BUCKET_NAME = System.getenv("MOIT151_S3_BUCKET_NAME")

class Storage(val s3Client: S3Client = client, val bucketName: String = BUCKET_NAME) : StorageService {
    override suspend fun getUploadPresignedUrl(fileName: String): String? {
        val unsignedRequest = PutObjectRequest {
            bucket = bucketName
            key = fileName
        }

        val presignedRequest = s3Client.presignPutObject(unsignedRequest, 1.hours)

        return presignedRequest.url.toString()
    }
    override suspend fun objectExistsByKey(fileName: String): Boolean {
        val request = HeadObjectRequest {
            bucket = bucketName
            key = fileName
        }

        try {
            s3Client.headObject(request)
            return true
        } catch(e: Exception) {
            return false
        }
    }
    override suspend fun getDownloadPresignedUrl(fileName: String): String? {
        val downloadRequest = GetObjectRequest {
            bucket = bucketName
            key = fileName
        }

        val presignedRequest = s3Client.presignGetObject(downloadRequest, 1.hours)

        return presignedRequest.url.toString()
    }
}

interface StorageService {
    suspend fun getUploadPresignedUrl(fileName: String): String?
    suspend fun objectExistsByKey(fileName: String): Boolean
    suspend fun getDownloadPresignedUrl(fileName: String): String?
}