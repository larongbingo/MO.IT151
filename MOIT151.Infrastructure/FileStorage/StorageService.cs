using Amazon.S3;
using Amazon.S3.Model;
using MOIT151.Application;

namespace MOIT151.Infrastructure.FileStorage;

public class StorageService(AmazonS3Client s3Client) : IStorageService
{
    private readonly string BucketName = Environment.GetEnvironmentVariable("MOIT151_S3_BUCKET_NAME") ?? throw new Exception("Bucket Name should be set"); 

    public async Task<string?> GetPresignedUploadUriAsync(string key, CancellationToken cancellationToken)
    {
        var presignRequest = new GetPreSignedUrlRequest()
        {
            BucketName = BucketName,
            Key = key,
            Verb = HttpVerb.PUT,
            Expires = DateTime.Now.AddHours(1),
        };

        var path = await s3Client.GetPreSignedURLAsync(presignRequest);

        return path;
    }

    public async Task<bool> ObjectExistsByKeyAsync(string key, CancellationToken cancellationToken = default)
    {
        try
        {
            await s3Client.GetObjectMetadataAsync(BucketName, key, cancellationToken);
            return true;
        }
        catch
        {
            return false;
        }
    }
}