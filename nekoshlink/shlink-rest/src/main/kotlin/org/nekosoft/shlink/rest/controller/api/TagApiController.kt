package org.nekosoft.shlink.rest.controller.api

import org.nekosoft.shlink.dao.TagDataAccess
import org.nekosoft.shlink.entity.Tag
import org.nekosoft.shlink.entity.support.TagStats
import org.nekosoft.shlink.rest.ShlinkRestApiServer.Companion.VERSION_STRING
import org.nekosoft.utils.rest.pagination.PaginationData
import org.nekosoft.utils.rest.pagination.PaginationData.Companion.paginationToPageable
import org.nekosoft.utils.rest.pagination.RestResult
import org.nekosoft.shlink.vo.*
import org.nekosoft.utils.rest.pagination.PaginationOptions
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

// https://www.marcobehler.com/guides/spring-security

// Role-based access is defined in the core TagDataAccess class

@RestController
@CrossOrigin(origins = ["*"])
@RequestMapping("api/v$VERSION_STRING/tags") // this short mapping guarantees it cannot be a short code (min five characters)
class TagApiController(
    private val tags: TagDataAccess,
) {

    @GetMapping
    fun tags(options: TagListOptions, pagination: PaginationOptions): ResponseEntity<RestResult<TagStats>> {
        val results = tags.findAll(options, paginationToPageable(pagination))
        return ResponseEntity.status(HttpStatus.OK).body(RestResult(
            results = results.content,
            pagination = PaginationData.fromPage(results)
        ))
    }

    @PostMapping
    fun createTag(@RequestBody meta: TagCreateMeta): ResponseEntity<Tag> {
        val tag = tags.create(meta.name, meta.description)
        return ResponseEntity.status(HttpStatus.OK).body(tag)
    }

    @GetMapping("{name}")
    fun findTag(@PathVariable("name") name: String): ResponseEntity<Tag> {
        val tag = tags.findByName(name)
        return ResponseEntity.status(HttpStatus.OK).body(tag)
    }

    @PatchMapping("rename")
    fun renameTag(@RequestBody meta: TagRenameMeta): ResponseEntity<Void> {
        tags.rename(meta.oldName, meta.newName, meta.newDescription)
        return ResponseEntity.status(HttpStatus.OK).body(null)
    }

    @PatchMapping("describe")
    fun describeTag(@RequestBody meta: TagDescribeMeta): ResponseEntity<Void> {
        tags.describe(meta.name, meta.description)
        return ResponseEntity.status(HttpStatus.OK).body(null)
    }

    @DeleteMapping("{name}")
    fun deleteTags(@PathVariable("name") name: String): ResponseEntity<Void> {
        tags.deleteByName(name)
        return ResponseEntity.status(HttpStatus.OK).body(null)
    }

}
