package org.rhasspy.mobile.platformspecific.utils

import org.rhasspy.mobile.data.link.LinkType
import org.rhasspy.mobile.platformspecific.clipboard.ClipboardUtils
import org.rhasspy.mobile.platformspecific.external.ExternalRedirectResult
import org.rhasspy.mobile.platformspecific.external.ExternalResultRequest
import org.rhasspy.mobile.platformspecific.external.ExternalResultRequestIntention
import org.rhasspy.mobile.resources.MR

class OpenLinkUtils(
    private val externalResultRequest: ExternalResultRequest
) {

    /**
     * true: link was opened
     * false: link could not be opened, url copied to clipboard
     */
    fun openLink(link: LinkType): Boolean {
        return if (externalResultRequest.launch(ExternalResultRequestIntention.OpenLink(link)) is ExternalRedirectResult.Success) {
            true
        } else {
            ClipboardUtils.copyToClipboard(MR.strings.url, link.url)
            false
        }
    }

}