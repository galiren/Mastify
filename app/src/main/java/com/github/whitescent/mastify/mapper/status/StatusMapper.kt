package com.github.whitescent.mastify.mapper.status

import com.github.whitescent.mastify.data.model.ui.StatusUiData
import com.github.whitescent.mastify.database.model.TimelineEntity
import com.github.whitescent.mastify.network.model.status.Status
import kotlinx.collections.immutable.toImmutableList

fun Status.toUiData(): StatusUiData {
  return StatusUiData(
    id = this.id,
    uuid = this.uuid,
    reblog = this.reblog,
    accountId = this.account.id,
    avatar = this.reblog?.account?.avatar ?: this.account.avatar,
    application = this.reblog?.application ?: this.application,
    rebloggedAvatar = this.account.avatar,
    displayName = this.reblog?.account?.displayName?.ifEmpty {
      this.reblog.account.username
    } ?: this.account.displayName.ifEmpty { this.account.username },
    reblogDisplayName = this.account.displayName
      .ifEmpty { this.account.username },
    fullname = this.reblog?.account?.fullName ?: this.account.fullName,
    createdAt = this.reblog?.createdAt ?: this.createdAt,
    content = this.reblog?.content ?: this.content,
    sensitive = this.reblog?.sensitive ?: this.sensitive,
    spoilerText = this.reblog?.spoilerText ?: this.spoilerText,
    attachments = this.reblog?.attachments?.toImmutableList() ?: this.attachments.toImmutableList(),
    repliesCount = this.reblog?.repliesCount ?: this.repliesCount,
    reblogsCount = this.reblog?.reblogsCount ?: this.reblogsCount,
    favouritesCount = this.reblog?.favouritesCount ?: this.favouritesCount,
    favourited = this.reblog?.favourited ?: this.favourited,
    inReplyToId = this.reblog?.inReplyToId ?: this.inReplyToId,
    replyChainType = this.replyChainType,
    hasUnloadedReplyStatus = this.hasUnloadedReplyStatus,
    hasUnloadedStatus = this.hasUnloadedStatus,
    hasMultiReplyStatus = this.hasMultiReplyStatus,
    shouldShow = this.shouldShow,
    actionable = this.actionableStatus,
    actionableId = this.actionableStatus.id
  )
}

fun Status.toEntity(timelineUserId: Long): TimelineEntity {
  return TimelineEntity(
    id = id,
    uuid = uuid,
    timelineUserId = timelineUserId,
    createdAt = createdAt,
    sensitive = sensitive,
    spoilerText = spoilerText,
    visibility = visibility,
    uri = uri,
    url = url,
    repliesCount = repliesCount,
    reblogsCount = reblogsCount,
    favouritesCount = favouritesCount,
    editedAt = editedAt,
    favourited = favourited,
    inReplyToId = inReplyToId,
    inReplyToAccountId = inReplyToAccountId,
    reblogged = reblogged,
    reblog = reblog,
    content = content,
    tags = tags,
    mentions = mentions,
    account = account,
    application = application,
    attachments = attachments,
    replyChainType = replyChainType,
    hasUnloadedReplyStatus = hasUnloadedReplyStatus,
    hasUnloadedStatus = hasUnloadedStatus,
    hasMultiReplyStatus = hasMultiReplyStatus,
    shouldShow = shouldShow
  )
}

fun List<Status>.toUiData() = this.map { it.toUiData() }