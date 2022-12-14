package org.nekosoft.shlink.sec.user.rest

import org.javers.spring.annotation.JaversSpringDataAuditable
import org.nekosoft.shlink.sec.user.User
import org.nekosoft.shlink.sec.user.UserDataAccess
import org.nekosoft.shlink.sec.user.rest.UserController.Companion.VERSION_STRING
import org.nekosoft.utils.rest.pagination.PaginationData
import org.nekosoft.utils.rest.pagination.PaginationData.Companion.paginationToPageable
import org.nekosoft.utils.rest.pagination.PaginationOptions
import org.nekosoft.utils.rest.pagination.RestResult
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@RestController
@CrossOrigin(origins = ["*"])
@RequestMapping("adm/v$VERSION_STRING/users") // this short mapping guarantees it cannot be a short code (min five characters)
@ConditionalOnWebApplication
@ConditionalOnExpression("'\${nekoshlink.security.userbase}'=='NATIVE' || '\${nekoshlink.security.userbase}' == 'X509'")
class UserController(
    private val users: UserDataAccess,
) {

    @PreAuthorize("hasRole('Users') and hasRole('Viewer')")
    @GetMapping
    fun users(options: UserListOptions, pagination: PaginationOptions): ResponseEntity<RestResult<User>> {
        val results = users.loadAllUsers(options, paginationToPageable(pagination))
        return ResponseEntity.status(HttpStatus.OK).body(RestResult(
            results = results.content.map { it.user },
            pagination = PaginationData.fromPage(results)
        )
        )
    }

    @PreAuthorize("hasRole('Users') and hasRole('Admin')")
    @PostMapping()
    fun addUser(@RequestBody user: User): ResponseEntity<User> {
        val newUser = users.createUser(user)
        return ResponseEntity.status(HttpStatus.OK).body(newUser)
    }

    @PreAuthorize("hasRole('Users') and hasRole('Editor')")
    @PatchMapping("{id}")
    fun editUser(@RequestBody user: User, @PathVariable("id") userId: Long): ResponseEntity<User> {
        user.id = userId
        user.lastModifiedDate = LocalDateTime.now()
        val newUser = users.updateUser(user)
        return ResponseEntity.status(HttpStatus.OK).body(newUser)
    }

    @PreAuthorize("hasRole('Users') and hasRole('Editor')")
    @PatchMapping("password")
    fun changePassword(@RequestBody meta: ChangePasswordMeta): ResponseEntity<Void> {
        users.changePassword(meta.username, meta.oldPassword, meta.newPassword)
        return ResponseEntity.status(HttpStatus.OK).body(null)
    }

    @PreAuthorize("hasRole('Users') and hasRole('Admin')")
    @PatchMapping("api-key")
    fun addApiKey(@RequestBody meta: ChangeApiKeyMeta): ResponseEntity<Void> {
        users.addApiKey(meta.username, meta.apiKey)
        return ResponseEntity.status(HttpStatus.OK).body(null)
    }

    @PreAuthorize("hasRole('Users') and hasRole('Admin')")
    @DeleteMapping("api-key")
    fun deleteApiKey(@RequestBody meta: ChangeApiKeyMeta): ResponseEntity<Void> {
        if (meta.apiKey != null) {
            val user = users.loadUserByApiKey(meta.apiKey!!)
            users.removeApiKey(user.username)
        } else {
            users.removeApiKey(meta.username)
        }
        return ResponseEntity.status(HttpStatus.OK).body(null)
    }

    companion object {
        const val VERSION_STRING = "1"
    }
}
