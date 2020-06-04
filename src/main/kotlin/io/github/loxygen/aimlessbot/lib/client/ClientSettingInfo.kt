package io.github.loxygen.aimlessbot.lib.client

import com.fasterxml.jackson.annotation.JsonProperty

data class ClientSettingInfo(
   @JsonProperty("loggingChannelId") val loggingChannelId: Long,
   @JsonProperty("botIdsWhiteList") val botIdsWhiteList: List<Long>,
   @JsonProperty("reactOnlyLoggingChanel") val reactOnlyLoggingChanel: Boolean
)