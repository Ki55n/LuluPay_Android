package com.sdk.lulupay.listeners

import com.sdk.lulupay.model.response.CodeResponse

interface InstrumentListener {
	/**
	 * Called when the instrument operation is successful
	 * @param response The response containing the instrument code details
	 */
	fun onSuccess(response: CodeResponse)

	/**
	 * Called when the instrument operation fails
	 * @param errorMessage A string describing what went wrong
	 */
	fun onFailed(errorMessage: String)
}