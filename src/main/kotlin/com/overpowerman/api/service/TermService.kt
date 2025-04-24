package com.overpowerman.api.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.overpowerman.api.dto.term.CreateTerm
import com.overpowerman.api.dto.term.PublishTerm
import com.overpowerman.api.dto.term.SaveTermDraft
import com.overpowerman.api.dto.term.TermMetadata
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.*
import java.nio.charset.StandardCharsets

@Service
class TermService(
    private val s3Client: S3Client,
    @Value("\${aws.s3.bucket}") private val bucketName: String,
) {
    private val BASE_TERM_PATH: String = "overpowerman-bucket/terms"
    private val objectMapper: ObjectMapper = ObjectMapper();

    private fun getPath(path: String): String {
        return "$BASE_TERM_PATH/$path"
    }

    fun saveTermDraft(saveTermDraft: SaveTermDraft) {
        s3Client.putObject(
            PutObjectRequest.builder().bucket(bucketName).key("${getPath(saveTermDraft.path)}/draft.html").build(),
            RequestBody.fromString(saveTermDraft.termContent)
        )
    }

    fun createTerm(createTerm: CreateTerm) {
        val termMetadata = TermMetadata(createTerm.title, mutableMapOf())
        s3Client.putObject(
            PutObjectRequest.builder().bucket(bucketName).key("${getPath(createTerm.path)}/term_metadata.json").build(),
            RequestBody.fromString(objectMapper.writeValueAsString(termMetadata))
        )
    }

    fun getTermMetadata(path: String): TermMetadata {
        val termMetadataJson = s3Client.getObject(
            GetObjectRequest.builder().bucket(bucketName).key("${getPath(path)}/term_metadata.json").build()
        ).bufferedReader(StandardCharsets.UTF_8).readText()
        return objectMapper.readValue(termMetadataJson, TermMetadata::class.java)
    }

    fun publishTerm(publishTerm: PublishTerm) {
        val copyRequest = CopyObjectRequest.builder()
            .sourceBucket(bucketName)
            .sourceKey("${getPath(publishTerm.path)}/draft.html")
            .destinationBucket(bucketName)
            .destinationKey("${getPath(publishTerm.path)}/published.html")
            .build()
        val resp = s3Client.copyObject(copyRequest)
        resp.versionId()

        val termMetadata = getTermMetadata(publishTerm.path);
        termMetadata.versions.set(publishTerm.version, resp.versionId())

        objectMapper.writeValueAsString(termMetadata)

        s3Client.putObject(
            PutObjectRequest.builder().bucket(bucketName).key("${getPath(publishTerm.path)}/term_metadata.json").build(),
            RequestBody.fromString(objectMapper.writeValueAsString(termMetadata))
        )

    }
}
