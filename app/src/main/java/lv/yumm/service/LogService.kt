package lv.yumm.service

interface LogService {
    fun logNonFatalCrash(throwable: Throwable)
}