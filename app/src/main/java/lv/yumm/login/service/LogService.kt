package lv.yumm.login.service

interface LogService {
    fun logNonFatalCrash(throwable: Throwable)
}