#define _GNU_SOURCE
#include <stddef.h>
#include <stdint.h>
#include <string.h>
#include <telephony/ril.h>

// Declare the real function (provided by qcrild)
void __real_RIL_onRequestComplete(RIL_Token t, RIL_Errno e, void *response, size_t responselen);

// Sanitize all int fields that are INT_MAX to -1
static void sanitize_signal_strength(RIL_SignalStrength_v10 *ss) {
    // GSM
    if (ss->GW_SignalStrength.signalStrength == INT_MAX) ss->GW_SignalStrength.signalStrength = -1;
    if (ss->GW_SignalStrength.bitErrorRate == INT_MAX) ss->GW_SignalStrength.bitErrorRate = -1;
    // CDMA / EVDO
    if (ss->CDMA_SignalStrength.dbm == INT_MAX) ss->CDMA_SignalStrength.dbm = -1;
    if (ss->CDMA_SignalStrength.ecio == INT_MAX) ss->CDMA_SignalStrength.ecio = -1;
    if (ss->EVDO_SignalStrength.dbm == INT_MAX) ss->EVDO_SignalStrength.dbm = -1;
    if (ss->EVDO_SignalStrength.ecio == INT_MAX) ss->EVDO_SignalStrength.ecio = -1;
    if (ss->EVDO_SignalStrength.signalNoiseRatio == INT_MAX) ss->EVDO_SignalStrength.signalNoiseRatio = -1;
    // LTE
    if (ss->LTE_SignalStrength.signalStrength == INT_MAX) ss->LTE_SignalStrength.signalStrength = -1;
    if (ss->LTE_SignalStrength.rsrp == INT_MAX) ss->LTE_SignalStrength.rsrp = -1;
    if (ss->LTE_SignalStrength.rsrq == INT_MAX) ss->LTE_SignalStrength.rsrq = -1;
    if (ss->LTE_SignalStrength.rssnr == INT_MAX) ss->LTE_SignalStrength.rssnr = -1;
    if (ss->LTE_SignalStrength.cqi == INT_MAX) ss->LTE_SignalStrength.cqi = -1;
    if (ss->LTE_SignalStrength.timingAdvance == INT_MAX) ss->LTE_SignalStrength.timingAdvance = -1;
    // WCDMA
    if (ss->WCDMA_SignalStrength.signalStrength == INT_MAX) ss->WCDMA_SignalStrength.signalStrength = -1;
    if (ss->WCDMA_SignalStrength.bitErrorRate == INT_MAX) ss->WCDMA_SignalStrength.bitErrorRate = -1;
    // TDSCDMA
    if (ss->TD_SCDMA_SignalStrength.rscp == INT_MAX) ss->TD_SCDMA_SignalStrength.rscp = -1;
    // NR (if present in v10; if not, adjust struct name)
    // For QPR2, v10 may not have NR, but just in case:
#ifdef RIL_SIGNAL_STRENGTH_V10_HAS_NR
    if (ss->NR_SignalStrength.ssRsrp == INT_MAX) ss->NR_SignalStrength.ssRsrp = -1;
    if (ss->NR_SignalStrength.ssRsrq == INT_MAX) ss->NR_SignalStrength.ssRsrq = -1;
    if (ss->NR_SignalStrength.ssSinr == INT_MAX) ss->NR_SignalStrength.ssSinr = -1;
#endif
}

void __wrap_RIL_onRequestComplete(RIL_Token t, RIL_Errno e, void *response, size_t responselen) {
    if (e == RIL_E_SUCCESS && response != NULL && responselen == sizeof(RIL_SignalStrength_v10)) {
        sanitize_signal_strength((RIL_SignalStrength_v10*)response);
    }
    __real_RIL_onRequestComplete(t, e, response, responselen);
}
