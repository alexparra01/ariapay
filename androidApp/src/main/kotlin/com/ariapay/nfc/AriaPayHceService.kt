package com.ariapay.nfc

import android.nfc.cardemulation.HostApduService
import android.os.Bundle

class AriaPayHceService : HostApduService() {
    
    companion object {
        private val STATUS_SUCCESS = byteArrayOf(0x90.toByte(), 0x00.toByte())
        private val STATUS_FAILED = byteArrayOf(0x6F.toByte(), 0x00.toByte())
    }
    
    override fun processCommandApdu(commandApdu: ByteArray, extras: Bundle?): ByteArray {
        // Handle SELECT command
        if (commandApdu.size >= 4 && commandApdu[1] == 0xA4.toByte()) {
            return STATUS_SUCCESS
        }
        return STATUS_FAILED
    }
    
    override fun onDeactivated(reason: Int) {
        // Handle deactivation
    }
}
