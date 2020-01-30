package fi.hsl.common;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class BlobStorage {
    private final BlobContainerClient blobContainerClient;

    public BlobStorage(@Value(value = "${blobstorage.connectionString}") String connectionString, @Value(value = "${blobstorage.blobcontainer}") String blobContainer) {
        BlobServiceClient blobServiceClient = new BlobServiceClientBuilder().connectionString(connectionString).buildClient();
        blobContainerClient = blobServiceClient.getBlobContainerClient(blobContainer);
    }

    public void uploadBlob(String filePath) {
        BlobClient blobClient = blobContainerClient.getBlobClient(filePath);
        blobClient.uploadFromFile(filePath);
    }
}

